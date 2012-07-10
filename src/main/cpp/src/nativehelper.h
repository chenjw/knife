
#include <jni.h>
#ifndef _Included_com_chenjw_attach_agent_NativeHelper
#define _Included_com_chenjw_attach_agent_NativeHelper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_chenjw_attach_agent_NativeHelper
 * Method:    findInstancesByClass
 * Signature: (Ljava/lang/Class;)[Ljava/lang/Object;
 */
JNIEXPORT jobjectArray JNICALL Java_com_chenjw_knife_agent_NativeHelper_findInstancesByClass0
  (JNIEnv *, jclass, jclass);

JNIEXPORT jobject JNICALL Java_com_chenjw_knife_agent_NativeHelper_getFieldValue0
  (JNIEnv *, jclass, jobject,jstring,jclass fieldClass);

jobjectArray findInstancesByClass0(JNIEnv *, jclass, jclass);


#ifdef __cplusplus
}
#endif
#endif

