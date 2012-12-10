#include <string.h>
#include "jni.h"

#include "jvmti.h"
#include "util.h"

jvmtiEnv *jvmti = NULL;

void * allocate(jlong bytecount) {
	void * resultBuffer = NULL;
	jvmtiError error = (*jvmti)->Allocate(jvmti, bytecount,
			(unsigned char**) &resultBuffer);
	if (error != JVMTI_ERROR_NONE) {
		resultBuffer = NULL;
	}
	return resultBuffer;
}

void deallocate(void * buffer) {
	(*jvmti)->Deallocate(jvmti, (unsigned char*) buffer);
}

void throwException(JNIEnv * env, char * clazz, char * message) {
	jclass exceptionClass;
	if (clazz != NULL ) {
		exceptionClass = (*env)->FindClass(env, clazz);
	} else {
		exceptionClass = (*env)->FindClass(env, "java/lang/RuntimeException");
	}

	if (exceptionClass == NULL ) {
		fprintf(stderr, "Couldn't throw exception %s - %s\n", clazz, message);
	}
	(*env)->ThrowNew(env, exceptionClass, message);
	fprintf(stderr, "exception found %s - %s\n", clazz, message);
}

void enableCapabilities(JNIEnv * env) {
	jvmtiError error;
	jvmtiCapabilities capabilities;
	error = (*jvmti)->GetCapabilities(jvmti, &capabilities);
	capabilities.can_tag_objects = 1;
	capabilities.can_generate_garbage_collection_events = 1;
	capabilities.can_retransform_classes = 1;
	capabilities.can_retransform_any_class = 1;
	capabilities.can_redefine_classes = 1;
	capabilities.can_redefine_any_class = 1;
	capabilities.can_get_source_file_name = 1;
	/////////////////////
	//capabilities.can_tag_objects = 1;
	//capabilities.can_generate_field_modification_events = 1;
	//capabilities.can_generate_field_access_events = 1;
	//capabilities.can_get_bytecodes = 1;
	//capabilities.can_get_synthetic_attribute = 1;
	//capabilities.can_get_owned_monitor_info = 1;
	//capabilities.can_get_current_contended_monitor = 1;
	//capabilities.can_get_monitor_info = 1;
	//capabilities.can_pop_frame = 1;
	//capabilities.can_redefine_classes = 1;
	//capabilities.can_signal_thread = 1;
	//capabilities.can_get_source_file_name = 1;
	//capabilities.can_get_line_numbers = 1;
	//capabilities.can_get_source_debug_extension = 1;
	//capabilities.can_access_local_variables = 1;
	//capabilities.can_maintain_original_method_order = 1;
	//capabilities.can_generate_single_step_events = 1;
	//capabilities.can_generate_exception_events = 1;
	//capabilities.can_generate_frame_pop_events = 1;
	//capabilities.can_generate_breakpoint_events = 1;
	//capabilities.can_suspend = 1;
	//capabilities.can_redefine_any_class = 1;
	//capabilities.can_get_current_thread_cpu_time = 1;
	//capabilities.can_get_thread_cpu_time = 1;
	//capabilities.can_generate_method_entry_events = 1;
	//capabilities.can_generate_method_exit_events = 1;
	//capabilities.can_generate_all_class_hook_events = 1;
	//capabilities.can_generate_compiled_method_load_events = 1;
	//capabilities.can_generate_monitor_events = 1;
	//capabilities.can_generate_vm_object_alloc_events = 1;
	//capabilities.can_generate_native_method_bind_events = 1;
	//capabilities.can_generate_garbage_collection_events = 1;
	//capabilities.can_generate_object_free_events = 1;
	//capabilities.can_force_early_return = 1;
	//capabilities.can_get_owned_monitor_stack_depth_info = 1;
	//capabilities.can_get_constant_pool = 1;
	//capabilities.can_set_native_method_prefix = 1;
	//capabilities.can_retransform_classes = 1;
	//capabilities.can_retransform_any_class = 1;
	//capabilities.can_generate_resource_exhaustion_heap_events = 1;
	//capabilities.can_generate_resource_exhaustion_threads_events = 1;

	error = (*jvmti)->AddCapabilities(jvmti, &capabilities);
	if (error != 0) {
		throwException(env, NULL, "AddCapabilities fail");
	}
}

void initJvmti(JNIEnv * env) {
	if (jvmti == NULL ) {
		JavaVM *jvm = 0;
		int res;
		res = (*env)->GetJavaVM(env, &jvm);
		if (res < 0 || jvm == 0) {
			throwException(env, NULL, "GetJavaVM fail");
		}
		res = (*jvm)->GetEnv(jvm, (void **) &jvmti, JVMTI_VERSION_1_1);
		if (res != 0 || jvmti == 0) {
			throwException(env, NULL, "GetEnv fail");
		}
		enableCapabilities(env);
	}
}

void initJvmtiWithoutCapabilities(JNIEnv * env) {
	if (jvmti == NULL ) {
		JavaVM *jvm = 0;
		int res;
		res = (*env)->GetJavaVM(env, &jvm);
		if (res < 0 || jvm == 0) {
			throwException(env, NULL, "GetJavaVM fail");
		}
		res = (*jvm)->GetEnv(jvm, (void **) &jvmti, JVMTI_VERSION_1_1);
		if (res != 0 || jvmti == 0) {
			throwException(env, NULL, "GetEnv fail");
		}
	}
}

jvmtiIterationControl iterate_cleanTag(jlong class_tag, jlong size,
		jlong* tag_ptr, void* user_data) {
	*tag_ptr = 0;
	return JVMTI_ITERATION_IGNORE;
}

void releaseTags() {
	(*jvmti)->IterateOverHeap(jvmti, JVMTI_HEAP_OBJECT_TAGGED,
			&iterate_cleanTag, NULL );
}

