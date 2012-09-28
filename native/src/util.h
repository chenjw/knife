#ifndef  NATIVE_HELPER_UTIL
#define NATIVE_HELPER_UTIL

extern jvmtiEnv *jvmti;

extern jclass nativeHelperClass;

void * allocate( jlong bytecount);

void deallocate( void * buffer);

void throwException(JNIEnv * env,char * clazz, char * message);

void initJvmti(JNIEnv * env);

void releaseTags();

#endif
