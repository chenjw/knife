#include "jni.h"
#include "jvmti.h"
#include "util.h"


jclass nativeHelperClass = 0;

jmethodID transformMethodId = 0;

jmethodID classLoadedMethodId = 0;





void initClassInfo(JNIEnv * env) {
	nativeHelperClass = (*env)->FindClass(env,
			"com/chenjw/knife/agent/utils/NativeHelper");
	if ((*env)->ExceptionCheck(env)) {
		(*env)->ExceptionDescribe(env);
	}
	transformMethodId =
			(*env)->GetStaticMethodID(env, nativeHelperClass, "transform",
					"(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/lang/Class;Ljava/security/ProtectionDomain;[B)[B");
}

void initClassLoadedInfo(JNIEnv * env) {
	nativeHelperClass = (*env)->FindClass(env,
			"com/chenjw/knife/agent/utils/NativeHelper");
	if ((*env)->ExceptionCheck(env)) {
		(*env)->ExceptionDescribe(env);
	}
	classLoadedMethodId = (*env)->GetStaticMethodID(env, nativeHelperClass,
			"classLoaded", "(Ljava/lang/Class;)V");
}

jvmtiIterationControl iterate_markTag(jlong class_tag, jlong size,
		jlong* tag_ptr, void* user_data) {
	if (tag_ptr != 0 ) {
		*tag_ptr = 1;
	}
	printf("cccc\n");
	return JVMTI_ITERATION_IGNORE;
}


void eventHandlerClassFileLoadHook(jvmtiEnv * jvmti, JNIEnv * env,
		jclass classBeingRedefined, jobject loader, const char* name,
		jobject protectionDomain, jint classDataLen,
		const unsigned char* classData, jint* newClassDataLen,
		unsigned char** newClassData) {
	unsigned char * resultBuffer = 0;
	jstring classNameStringObject = (*env)->NewStringUTF(env, name);
	jbyteArray classFileBufferObject = (*env)->NewByteArray(env, classDataLen);
	jbyte * typedBuffer = (jbyte *) classData;
	(*env)->SetByteArrayRegion(env, classFileBufferObject, 0, classDataLen,
			typedBuffer);
	jbyteArray transformedBufferObject =
			(jbyteArray) (*env)->CallStaticObjectMethod(env, nativeHelperClass,
					transformMethodId, loader, classNameStringObject,
					classBeingRedefined, protectionDomain,
					classFileBufferObject);
	if (transformedBufferObject != 0 ) {
		jsize transformedBufferSize = (*env)->GetArrayLength(env,
				transformedBufferObject);
		(*env)->GetByteArrayRegion(env, transformedBufferObject, 0,
				transformedBufferSize, (jbyte *) resultBuffer);
		*newClassDataLen = (transformedBufferSize);
		*newClassData = resultBuffer;
	}
	return;
}

void eventHandlerClassLoadedHook(jvmtiEnv * jvmti, JNIEnv* env, jthread thread,
		jclass klass) {
	(*env)->CallStaticObjectMethod(env, nativeHelperClass, classLoadedMethodId,
			klass);
}


/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    startClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_startClassFileLoadHook0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	initClassInfo(env);
	jvmtiEventCallbacks * callbacks;
	callbacks = (jvmtiEventCallbacks *) allocate(sizeof(jvmtiEventCallbacks));
	callbacks->ClassFileLoadHook = (jvmtiEventClassFileLoadHook)&eventHandlerClassFileLoadHook;
	(*jvmti)->SetEventCallbacks(jvmti, callbacks, sizeof(callbacks));
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE,
			JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, 0 );

}

/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    stopClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_stopClassFileLoadHook0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_DISABLE,
			JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, 0 );
}

/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    startClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_startClassLoadHook0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	initClassLoadedInfo(env);
	jvmtiEventCallbacks callbacks;
	//memset(&callbacks, 0, sizeof(callbacks));
	callbacks.ClassLoad = (jvmtiEventClassLoad)&eventHandlerClassLoadedHook;
	(*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE,
			JVMTI_EVENT_CLASS_LOAD, 0 );

}

/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    stopClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_stopClassLoadHook0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_DISABLE,
			JVMTI_EVENT_CLASS_LOAD, 0 );
}

/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    retransformClasses
 * Signature: ([Ljava/lang/Class;)V
 */
JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_retransformClasses0(
		JNIEnv * env, jclass thisClass, jobjectArray classes) {
	initJvmti(env);
	jsize numClasses = 0;
	jclass * classArray = 0;
	numClasses = (*env)->GetArrayLength(env, classes);
	classArray = (jclass *) allocate(numClasses * sizeof(jclass));
	jint index;
	for (index = 0; index < numClasses; index++) {
		classArray[index] = (jclass) (*env)->GetObjectArrayElement(env, classes,
				index);
	}
	(*jvmti)->RetransformClasses(jvmti, numClasses, classArray);
	if (classArray != 0 ) {
		deallocate((void*) classArray);
	}
}





JNIEXPORT jobjectArray  Java_com_chenjw_knife_agent_utils_NativeHelper_findInstancesByClass0(
		JNIEnv * env, jclass thisClass, jclass klass) {
	printf("%d\n",jvmti);
	initJvmti(env);
	printf("1111\n");
	jclass loadedObject = (*env)->FindClass(env, "java/lang/Object");
	printf("%d\n",klass);
	printf("%d\n",JVMTI_HEAP_OBJECT_EITHER);
	printf("%d\n",iterate_markTag);
	jvmtiError error=(*jvmti)->IterateOverInstancesOfClass(jvmti, klass,
			JVMTI_HEAP_OBJECT_EITHER, (jvmtiHeapObjectCallback)iterate_markTag,0 );
	printf("%daaa\n",error);
	printf("2222\n");
	jint countObjs = 0;
	jobject * objs;
	jlong * tagResults;
	jlong idToQuery = 1;

	(*jvmti)->GetObjectsWithTags(jvmti, 1, &idToQuery, &countObjs, &objs,
			&tagResults);
	printf("3\n");
	// Set the object array
	jobjectArray arrayReturn = (*env)->NewObjectArray(env, countObjs,
			loadedObject, 0);
	jint i;
	for (i = 0; i < countObjs; i++) {
		(*env)->SetObjectArrayElement(env, arrayReturn, i, objs[i]);
	}
	printf("4\n");
	deallocate(tagResults);
	printf("5\n");
	deallocate(objs);
	printf("6\n");
	releaseTags();

	return arrayReturn;
}



JNIEXPORT void  Java_com_chenjw_knife_agent_utils_NativeHelper_redefineClass0(
		JNIEnv * env, jclass thisClass, jclass klass, jbyteArray byteArray) {
	initJvmti(env);
	jvmtiClassDefinition * classDef = allocate(sizeof(jvmtiClassDefinition));
	classDef->klass = klass;
	classDef->class_bytes = (unsigned char*) (*env)->GetByteArrayElements(env,
			byteArray, 0 );
	classDef->class_byte_count = (*env)->GetArrayLength(env, byteArray);
	(*jvmti)->RedefineClasses(jvmti, 1, classDef);
	deallocate(classDef);
}




JNIEXPORT jstring  Java_com_chenjw_knife_agent_utils_NativeHelper_getClassSourceFileName0(
		JNIEnv *env, jclass thisClass, jclass clazz) {
	initJvmti(env);
	char* source_name_ptr;
	(*jvmti)->GetSourceFileName(jvmti, clazz, &source_name_ptr);
	jstring result = (*env)->NewStringUTF(env, source_name_ptr);
	return result;
}

JNIEXPORT jobject  Java_com_chenjw_knife_agent_utils_NativeHelper_getCallerClassLoader0(
		JNIEnv *env, jclass thisClass) {
	//jclass caller = JVM_GetCallerClass(env,2);
	//return caller != 0 ? JVM_GetClassLoader(env,caller) : 0;

	// not implemented
	return 0 ;
}
