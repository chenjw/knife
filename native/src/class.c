
#include <string.h>

#include "jni.h"

#include "jvmti.h"
#include "api.h"
#include "util.h"


jclass nativeHelperClass = NULL;

jmethodID transformMethodId = NULL;

jmethodID classLoadedMethodId = NULL;





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
	if (tag_ptr != NULL ) {
		*tag_ptr = 1;
	}

	return JVMTI_ITERATION_IGNORE;
}


void JNICALL
eventHandlerClassFileLoadHook(jvmtiEnv * jvmti, JNIEnv * env,
		jclass classBeingRedefined, jobject loader, const char* name,
		jobject protectionDomain, jint classDataLen,
		const unsigned char* classData, jint* newClassDataLen,
		unsigned char** newClassData) {
	unsigned char * resultBuffer = NULL;
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
	if (transformedBufferObject != NULL ) {
		jsize transformedBufferSize = (*env)->GetArrayLength(env,
				transformedBufferObject);
		(*env)->GetByteArrayRegion(env, transformedBufferObject, 0,
				transformedBufferSize, (jbyte *) resultBuffer);
		*newClassDataLen = (transformedBufferSize);
		*newClassData = resultBuffer;
	}
	return;
}

void JNICALL
eventHandlerClassLoadedHook(jvmtiEnv * jvmti, JNIEnv* env, jthread thread,
		jclass klass) {
	(*env)->CallStaticObjectMethod(env, nativeHelperClass, classLoadedMethodId,
			klass);
}


/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    startClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_startClassFileLoadHook0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	initClassInfo(env);
	jvmtiEventCallbacks callbacks;
	memset(&callbacks, 0, sizeof(callbacks));
	callbacks.ClassFileLoadHook = &eventHandlerClassFileLoadHook;
	(*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE,
			JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL );

}

/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    stopClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_stopClassFileLoadHook0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_DISABLE,
			JVMTI_EVENT_CLASS_FILE_LOAD_HOOK, NULL );
}

/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    startClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_startClassLoadHook0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	initClassLoadedInfo(env);
	jvmtiEventCallbacks callbacks;
	memset(&callbacks, 0, sizeof(callbacks));
	callbacks.ClassLoad = &eventHandlerClassLoadedHook;
	(*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE,
			JVMTI_EVENT_CLASS_LOAD, NULL );

}

/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    stopClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_stopClassLoadHook0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_DISABLE,
			JVMTI_EVENT_CLASS_LOAD, NULL );
}

/*
 * Class:     com_chenjw_knife_agent_utils_NativeHelper
 * Method:    retransformClasses
 * Signature: ([Ljava/lang/Class;)V
 */
JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_retransformClasses0(
		JNIEnv * env, jclass thisClass, jobjectArray classes) {
	initJvmti(env);
	jsize numClasses = 0;
	jclass * classArray = NULL;
	numClasses = (*env)->GetArrayLength(env, classes);
	classArray = (jclass *) allocate(numClasses * sizeof(jclass));
	jint index;
	for (index = 0; index < numClasses; index++) {
		classArray[index] = (jclass) (*env)->GetObjectArrayElement(env, classes,
				index);
	}
	(*jvmti)->RetransformClasses(jvmti, numClasses, classArray);
	if (classArray != NULL ) {
		deallocate((void*) classArray);
	}
}





JNIEXPORT jobjectArray JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_findInstancesByClass0(
		JNIEnv * env, jclass thisClass, jclass klass) {
	initJvmti(env);
	jclass loadedObject = (*env)->FindClass(env, "java/lang/Object");
	(*jvmti)->IterateOverInstancesOfClass(jvmti, klass,
			JVMTI_HEAP_OBJECT_EITHER, iterate_markTag, NULL );

	jint countObjts = 0;
	jobject * objs;
	jlong * tagResults;
	jlong idToQuery = 1;

	(*jvmti)->GetObjectsWithTags(jvmti, 1, &idToQuery, &countObjts, &objs,
			&tagResults);
	// Set the object array
	jobjectArray arrayReturn = (*env)->NewObjectArray(env, countObjts,
			loadedObject, 0);
	jint i;
	for (i = 0; i < countObjts; i++) {
		(*env)->SetObjectArrayElement(env, arrayReturn, i, objs[i]);
	}
	deallocate(tagResults);

	deallocate(objs);
	releaseTags();

	return arrayReturn;
}



JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_redefineClass0(
		JNIEnv * env, jclass thisClass, jclass klass, jbyteArray byteArray) {
	initJvmti(env);
	jvmtiClassDefinition * classDef = allocate(sizeof(jvmtiClassDefinition));
	classDef->klass = klass;
	classDef->class_bytes = (unsigned char*) (*env)->GetByteArrayElements(env,
			byteArray, NULL );
	classDef->class_byte_count = (*env)->GetArrayLength(env, byteArray);
	(*jvmti)->RedefineClasses(jvmti, 1, classDef);
	deallocate(classDef);
}




JNIEXPORT jstring JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_getClassSourceFileName0(
		JNIEnv *env, jclass thisClass, jclass clazz) {
	initJvmti(env);
	char* source_name_ptr;
	(*jvmti)->GetSourceFileName(jvmti, clazz, &source_name_ptr);
	jstring result = (*env)->NewStringUTF(env, source_name_ptr);
	return result;
}

JNIEXPORT jobject JNICALL Java_com_chenjw_knife_agent_utils_NativeHelper_getCallerClassLoader0(
		JNIEnv *env, jclass thisClass) {
	//jclass caller = JVM_GetCallerClass(env,2);
	//return caller != 0 ? JVM_GetClassLoader(env,caller) : 0;

	// not implemented
	return NULL ;
}