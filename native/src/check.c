#include <string.h>
#include "jni.h"

#include "jvmti.h"
#include "util.h"


jboolean  Java_com_chenjw_knife_agent_utils_NativeHelper_checkCapabilities0(
		JNIEnv * env, jint idx) {
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

	jvmtiError error;
	jvmtiCapabilities capabilities;
	error = (*jvmti)->GetCapabilities(jvmti, &capabilities);
	if (idx == 0) {
		capabilities.can_tag_objects = 1;
	} else if (idx == 1) {
		capabilities.can_generate_field_modification_events = 1;
	} else if (idx == 2) {
		capabilities.can_generate_field_access_events = 1;
	} else if (idx == 3) {
		capabilities.can_generate_field_modification_events = 1;
	} else if (idx == 4) {
		capabilities.can_get_bytecodes = 1;
	} else if (idx == 5) {
		capabilities.can_get_synthetic_attribute = 1;
	} else if (idx == 6) {
		capabilities.can_get_owned_monitor_info = 1;
	} else if (idx == 7) {
		capabilities.can_get_current_contended_monitor = 1;
	} else if (idx == 8) {
		capabilities.can_get_monitor_info = 1;
	} else if (idx == 9) {
		capabilities.can_pop_frame = 1;
	} else if (idx == 10) {
		capabilities.can_redefine_classes = 1;
	} else if (idx == 1) {
		capabilities.can_signal_thread = 1;
	} else if (idx == 1) {
		capabilities.can_get_source_file_name = 1;
	} else if (idx == 1) {
		capabilities.can_get_line_numbers = 1;
	} else if (idx == 1) {
		capabilities.can_get_source_debug_extension = 1;
	} else if (idx == 1) {
		capabilities.can_access_local_variables = 1;
	} else if (idx == 1) {
		capabilities.can_maintain_original_method_order = 1;
	} else if (idx == 1) {
		capabilities.can_generate_single_step_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_exception_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_frame_pop_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_breakpoint_events = 1;
	} else if (idx == 1) {
		capabilities.can_suspend = 1;
	} else if (idx == 1) {
		capabilities.can_redefine_any_class = 1;
	} else if (idx == 1) {
		capabilities.can_get_current_thread_cpu_time = 1;
	} else if (idx == 1) {
		capabilities.can_get_thread_cpu_time = 1;
	} else if (idx == 1) {
		capabilities.can_generate_method_entry_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_method_exit_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_all_class_hook_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_compiled_method_load_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_monitor_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_vm_object_alloc_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_native_method_bind_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_garbage_collection_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_object_free_events = 1;
	} else if (idx == 1) {
		capabilities.can_force_early_return = 1;
	} else if (idx == 1) {
		capabilities.can_get_owned_monitor_stack_depth_info = 1;
	} else if (idx == 1) {
		capabilities.can_get_constant_pool = 1;
	} else if (idx == 1) {
		capabilities.can_set_native_method_prefix = 1;
	} else if (idx == 1) {
		capabilities.can_retransform_classes = 1;
	} else if (idx == 1) {
		capabilities.can_retransform_any_class = 1;
	} else if (idx == 1) {
		capabilities.can_generate_resource_exhaustion_heap_events = 1;
	} else if (idx == 1) {
		capabilities.can_generate_resource_exhaustion_threads_events = 1;
	}
	error = (*jvmti)->AddCapabilities(jvmti, &capabilities);
	if (error != 0) {
		return 0;
	}
	else{
		return 1;
	}
}


