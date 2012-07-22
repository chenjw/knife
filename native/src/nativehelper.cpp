#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "jvmti.h"
#include "agent.h"
#include "nativehelper.h"
jvmtiEnv *jvmti=NULL;

jclass nativeHelperClass=NULL;;

jmethodID transformMethodId=NULL;

void initJvmti(JNIEnv * env){
	if(jvmti == NULL){
		JavaVM *jvm = 0;
		int res;
		res = env->GetJavaVM(&jvm);
		if (res < 0 || jvm == 0) {
			throwException(env,"java/lang/RuntimeException","GetJavaVM fail");
		}
		res = jvm->GetEnv((void **)&jvmti, JVMTI_VERSION_1_0);
		if (res != JNI_OK || jvmti == 0) {
			throwException(env,"java/lang/RuntimeException","GetEnv fail");
		}
		jvmtiError error;
	  	jvmtiCapabilities   capabilities;
	  	error = jvmti->GetCapabilities(&capabilities);
	  	capabilities.can_tag_objects = 1;
	  	capabilities.can_generate_garbage_collection_events = 1;
		capabilities.can_retransform_classes = 1;
		capabilities.can_retransform_any_class = 1;
		capabilities.can_redefine_classes = 1;
		capabilities.can_redefine_any_class = 1;
	  	error= jvmti->AddCapabilities(&capabilities);
	}	
}

void initClassInfo(JNIEnv * env){
	nativeHelperClass = env->FindClass("com/chenjw/knife/agent/NativeHelper");
	if(env->ExceptionCheck()){
		env->ExceptionDescribe();	
	}
	transformMethodId = env->GetStaticMethodID(nativeHelperClass,"transform","(Ljava/lang/ClassLoader;Ljava/lang/String;Ljava/lang/Class;Ljava/security/ProtectionDomain;[B)[B"); 
}

void throwException(JNIEnv * env,char * clazz, char * message)
{
  	jclass exceptionClass = env->FindClass(clazz);
  	if (exceptionClass==NULL) 
  	{
		exceptionClass = env->FindClass("java/lang/RuntimeException");
     		if (exceptionClass==NULL) 
     		{
			fprintf (stderr,"Couldn't throw exception %s - %s\n",clazz,message);
     		}
 	}
  	env->ThrowNew(exceptionClass,message);
}


void * allocate(jvmtiEnv * jvmti, size_t bytecount) {
	void * resultBuffer = NULL;
	jvmtiError error = jvmti->Allocate(bytecount,(unsigned char**) &resultBuffer);
	if ( error != JVMTI_ERROR_NONE ) {
		resultBuffer = NULL;
	}
	return resultBuffer;
}

void deallocate(jvmtiEnv * jvmti, void * buffer) {
	jvmti->Deallocate((unsigned char*)buffer);
}

jvmtiIterationControl JNICALL iterate_markTag
    (jlong class_tag, jlong size, jlong* tag_ptr, void* user_data) 
{
	IteraOverObjectsControl * control = (IteraOverObjectsControl *) user_data;
    	*tag_ptr=1;
    	control->count++;
    	return JVMTI_ITERATION_CONTINUE;
}

jvmtiIterationControl JNICALL iterate_cleanTag
    (jlong class_tag, jlong size, jlong* tag_ptr, void* user_data)
{
	*tag_ptr=0;
   	return JVMTI_ITERATION_CONTINUE;   
}


void releaseTags()
{
  	jvmti->IterateOverHeap( JVMTI_HEAP_OBJECT_TAGGED,
                          &iterate_cleanTag, NULL);
}

jobject getBooleanField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jboolean fieldValue=env->GetBooleanField(obj,fieldId);
	jclass clazz=env->FindClass("java/lang/Boolean");
	jobject r=env->NewObject(clazz,env->GetMethodID(clazz,"<init>","(Z)V"),fieldValue);
	return r;
}

jobject getByteField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jbyte fieldValue=env->GetByteField(obj,fieldId);
	jclass clazz=env->FindClass("java/lang/Byte");
	jobject r=env->NewObject(clazz,env->GetMethodID(clazz,"<init>","(B)V"),fieldValue);
	return r;
}

jobject getCharField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jchar fieldValue=env->GetCharField(obj,fieldId);
	jclass clazz=env->FindClass("java/lang/Character");
	jobject r=env->NewObject(clazz,env->GetMethodID(clazz,"<init>","(C)V"),fieldValue);
	return r;
}

jobject getShortField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jshort fieldValue=env->GetShortField(obj,fieldId);
	jclass clazz=env->FindClass("java/lang/Short");
	jobject r=env->NewObject(clazz,env->GetMethodID(clazz,"<init>","(S)V"),fieldValue);
	return r;
}

jobject getIntField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jint fieldValue=env->GetIntField(obj,fieldId);
	jclass clazz=env->FindClass("java/lang/Integer");
	jobject r=env->NewObject(clazz,env->GetMethodID(clazz,"<init>","(I)V"),fieldValue);
	return r;
}

jobject getLongField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jlong fieldValue=env->GetLongField(obj,fieldId);
	jclass clazz=env->FindClass("java/lang/Long");
	jobject r=env->NewObject(clazz,env->GetMethodID(clazz,"<init>","(J)V"),fieldValue);
	return r;
}

jobject getFloatField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jfloat fieldValue=env->GetFloatField(obj,fieldId);
	jclass clazz=env->FindClass("java/lang/Float");
	jobject r=env->NewObject(clazz,env->GetMethodID(clazz,"<init>","(F)V"),fieldValue);
	return r;
}

jobject getDoubleField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jdouble fieldValue=env->GetDoubleField(obj,fieldId);
	jclass clazz=env->FindClass("java/lang/Double");
	jobject r=env->NewObject(clazz,env->GetMethodID(clazz,"<init>","(D)V"),fieldValue);
	return r;
}


jobject getObjectField(JNIEnv * env,jobject obj,jfieldID fieldId){
	jobject fieldValue=env->GetObjectField(obj,fieldId);
	return fieldValue;
}

jobject getFieldValue(JNIEnv * env,jobject obj,jclass fieldClass,jfieldID fieldId)
{
	char* signature;
	jvmti->GetClassSignature(fieldClass,&signature,NULL);
	//printf("bbb%s\n",signature);
	if(strcmp(signature,"Z")==0){
		return getBooleanField(env,obj,fieldId);
	}
	else if(strcmp(signature,"B")==0){
		return getByteField(env,obj,fieldId);
	}
	else if(strcmp(signature,"C")==0){
		return getCharField(env,obj,fieldId);
	}
	else if(strcmp(signature,"S")==0){
		return getShortField(env,obj,fieldId);
	}
	else if(strcmp(signature,"I")==0){
		return getIntField(env,obj,fieldId);
	}
	else if(strcmp(signature,"J")==0){
		return getLongField(env,obj,fieldId);
	}
	else if(strcmp(signature,"F")==0){
		return getFloatField(env,obj,fieldId);
	}
	else if(strcmp(signature,"D")==0){
		return getDoubleField(env,obj,fieldId);
	}
	else{
		return getObjectField(env,obj,fieldId);
	}
}

void JNICALL
eventHandlerClassFileLoadHook(  jvmtiEnv *              jvmti,
                                JNIEnv *                env,
                                jclass                  classBeingRedefined,
                                jobject                 loader,
                                const char*             name,
                                jobject                 protectionDomain,
                                jint                    classDataLen,
                                const unsigned char*    classData,
                                jint*                   newClassDataLen,
                                unsigned char**         newClassData) {
    	unsigned char * resultBuffer = NULL;
        jstring classNameStringObject = env->NewStringUTF(name);
 	jbyteArray classFileBufferObject = env->NewByteArray(classDataLen);
	jbyte * typedBuffer = (jbyte *) classData;          
 	env->SetByteArrayRegion(classFileBufferObject,0,classDataLen,typedBuffer);
     	jbyteArray transformedBufferObject = (jbyteArray)env->CallStaticObjectMethod(nativeHelperClass,transformMethodId,loader,classNameStringObject,classBeingRedefined,protectionDomain,classFileBufferObject);
	if (transformedBufferObject != NULL) {
		jsize transformedBufferSize = env->GetArrayLength(transformedBufferObject);
		env->GetByteArrayRegion(transformedBufferObject,0,transformedBufferSize,(jbyte *)resultBuffer);
		*newClassDataLen = (transformedBufferSize);
   		*newClassData     = resultBuffer;
	}
	return;
}

/*
 * Class:     com_chenjw_knife_agent_NativeHelper
 * Method:    startClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_NativeHelper_startClassFileLoadHook0
  (JNIEnv * env, jclass thisClass){
	initJvmti(env);
	initClassInfo(env);
	jvmtiEventCallbacks callbacks;
        memset(&callbacks, 0, sizeof(callbacks));
        callbacks.ClassFileLoadHook = &eventHandlerClassFileLoadHook;
        jvmti->SetEventCallbacks(&callbacks,sizeof(callbacks));
 	jvmti->SetEventNotificationMode(JVMTI_ENABLE,JVMTI_EVENT_CLASS_FILE_LOAD_HOOK,NULL);

}

/*
 * Class:     com_chenjw_knife_agent_NativeHelper
 * Method:    stopClassFileLoadHook0
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_NativeHelper_stopClassFileLoadHook0
  (JNIEnv * env, jclass thisClass){
	initJvmti(env);
	jvmti->SetEventNotificationMode(JVMTI_DISABLE,JVMTI_EVENT_CLASS_FILE_LOAD_HOOK,NULL);
}

/*
 * Class:     com_chenjw_knife_agent_NativeHelper
 * Method:    retransformClasses
 * Signature: ([Ljava/lang/Class;)V
 */
JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_NativeHelper_retransformClasses0
  (JNIEnv * env, jclass thisClass, jobjectArray classes){
	initJvmti(env);
    	jsize       numClasses           = 0;
    	jclass *    classArray           = NULL;
	numClasses = env->GetArrayLength(classes);
        classArray = (jclass *) allocate(jvmti,numClasses * sizeof(jclass));
        jint index;
        for (index = 0; index < numClasses; index++) {
      		classArray[index] = (jclass)env->GetObjectArrayElement(classes, index);
        }
 	jvmti->RetransformClasses(numClasses, classArray);
	if (classArray != NULL) {
		deallocate(jvmti, (void*)classArray);
    	}
}


JNIEXPORT jobjectArray JNICALL Java_com_chenjw_knife_agent_NativeHelper_findInstancesByClass0
  (JNIEnv * env, jclass thisClass, jclass klass)
{ 
	initJvmti(env);
	jclass loadedObject = env->FindClass("java/lang/Object");
	IteraOverObjectsControl control;
  	control.size = 0;
  	control.maxsize = 0;
  	control.count=0;

  	jvmtiError e=jvmti->IterateOverInstancesOfClass(klass,JVMTI_HEAP_OBJECT_EITHER,iterate_markTag, &control);
	
  	jint countObjts=0;
  	jobject * objs;
  	jlong * tagResults;
  	jlong idToQuery=1;  
   
  	jvmti->GetObjectsWithTags(1,&idToQuery,&countObjts,&objs,&tagResults);
  	// Set the object array
  	jobjectArray arrayReturn = env->NewObjectArray(countObjts,loadedObject,0);
  	for (jint i=0;i<countObjts;i++) {
     		env->SetObjectArrayElement(arrayReturn,i, objs[i]);
  	}
	jvmti->Deallocate((unsigned char *)tagResults);  
  	jvmti->Deallocate((unsigned char *)objs);  
  	releaseTags();         

  	return arrayReturn;
}

JNIEXPORT jobject JNICALL Java_com_chenjw_knife_agent_NativeHelper_getFieldValue0
  (JNIEnv * env, jclass thisClass, jobject obj,jclass fieldClass,jstring fieldName,jclass fieldType)
{
	
	initJvmti(env);
	char* fieldNameChars=(char*)env->GetStringUTFChars(fieldName,0);	
	//printf("123%s\n",fieldNameChars);	
	
	//jclass klass=env->GetObjectClass(obj);
	jclass klass=fieldClass;
	//jfieldID fieldId=env->GetFieldID(klass,fieldNameChars,fieldNameChars);
	jint count=0;
	jfieldID* fieldIds;
	jvmti->GetClassFields(klass,&count,&fieldIds);
	for(int i=0;i<count;i++){
		char* tFieldName;
		jvmti->GetFieldName(klass,fieldIds[i],&tFieldName,0,0);
		
		//printf("%s\n",fieldNameChars);
		if(strcmp(tFieldName,fieldNameChars)==0){
			//printf("123%s\n",tFieldName);
			//printf("aaa\n");
			jobject result=getFieldValue(env,obj,fieldType,fieldIds[i]);
			jvmti->Deallocate((unsigned char *)tFieldName);
			jvmti->Deallocate((unsigned char *)fieldIds);
			env->ReleaseStringUTFChars(fieldName,fieldNameChars);  	
			return result;
		}
	}
	//char* errorStr=strcat("field not found ",fieldNameChars);
	throwException(env,NULL,"field not found");
	jvmti->Deallocate((unsigned char *)fieldIds);  
	env->ReleaseStringUTFChars(fieldName,fieldNameChars);
	return NULL;
}


JNIEXPORT void JNICALL Java_com_chenjw_knife_agent_NativeHelper_redefineClass0
  (JNIEnv * env, jclass thisClass, jclass klass, jbyteArray byteArray)
{
	initJvmti(env);
    	jvmtiClassDefinition * classDef = NULL;
	jvmti->Allocate(sizeof(jvmtiClassDefinition),(unsigned char**)&classDef);
  	classDef->klass = klass;
	classDef->class_bytes = (unsigned char*)env->GetByteArrayElements(byteArray, NULL);
	classDef->class_byte_count = env->GetArrayLength(byteArray);
	jvmti->RedefineClasses(1, classDef);
	jvmti->Deallocate((unsigned char *)classDef);
}





