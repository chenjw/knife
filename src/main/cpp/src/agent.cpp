#include <jvmti.h>
#include <string>
#include <cstring>
#include <iostream>
#include <list>
#include <map>
#include <set>
#include <stdlib.h>
#include <jni_md.h>
#include "agent.h"

jvmtiEnv *jvmti=NULL;

jint initJVMTI(JavaVM *jvm)
{
   	jint res;
	res = jvm->GetEnv((void **) &jvmti, JVMTI_VERSION_1_0);   
  	if (res!=JNI_OK) {
      		return res;
   	}
   	jvmtiError error;
  	jvmtiCapabilities   capabilities;
  	error = jvmti->GetCapabilities(&capabilities);
	verifyError(error);
  	capabilities.can_tag_objects = 1;
  	capabilities.can_generate_garbage_collection_events = 1;
  	error= jvmti->AddCapabilities(&capabilities);
  	verifyError(error);
   	return JNI_OK;
}



JNIEXPORT jint JNICALL Agent_OnAttach(JavaVM *jvm, char *options,void *reserved) {
	return initJVMTI(jvm);
}

JNIEXPORT jint JNICALL Agent_OnLoad(JavaVM *jvm, char *options,void *reserved) {
        return initJVMTI(jvm);
}


JNIEXPORT void JNICALL Agent_OnUnload(JavaVM *vm) {

}
