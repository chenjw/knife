
#include <string.h>

#include "jni.h"

#include "jvmti.h"
#include "api.h"
#include "util.h"

jmethodID methodEnterMethodId = NULL;

void initMethodEnterInfo(JNIEnv * env) {
	nativeHelperClass = (*env)->FindClass(env,
			"com/chenjw/knife/agent/utils/NativeHelper");
	if ((*env)->ExceptionCheck(env)) {
		(*env)->ExceptionDescribe(env);
	}
	methodEnterMethodId = (*env)->GetStaticMethodID(env, nativeHelperClass,
			"methodEnter", "(Ljava/lang/reflect/Method;)V");
}

void eventHandlerMethodEnter(jvmtiEnv * jvmti, JNIEnv* env, jthread thread,
		jmethodID method) {
	jclass* clazz = 0;
	(*jvmti)->GetMethodDeclaringClass(jvmti, method, clazz);
	jobject obj = (*env)->ToReflectedMethod(env, *clazz, method, 1);

	(*env)->CallStaticObjectMethod(env, nativeHelperClass, methodEnterMethodId,
			obj);
}

 void  Java_com_chenjw_knife_agent_utils_NativeHelper_startMethodTrace0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	initMethodEnterInfo(env);
	jvmtiEventCallbacks callbacks;
	memset(&callbacks, 0, sizeof(callbacks));
	callbacks.MethodEntry = &eventHandlerMethodEnter;
	(*jvmti)->SetEventCallbacks(jvmti, &callbacks, sizeof(callbacks));
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_ENABLE,
			JVMTI_EVENT_METHOD_ENTRY, NULL );
	printf("aaa\n");

}

 void  Java_com_chenjw_knife_agent_utils_NativeHelper_stopMethodTrace0(
		JNIEnv * env, jclass thisClass) {
	initJvmti(env);
	(*jvmti)->SetEventNotificationMode(jvmti, JVMTI_DISABLE,
			JVMTI_EVENT_METHOD_ENTRY, NULL );
	printf("ddd\n");
}

