#include "jni.h"
#include "jvmti.h"

#include "util.h"

JNIEXPORT jbooleanArray  Java_com_chenjw_knife_agent_utils_NativeHelper_checkCapabilities0(
		JNIEnv * env) {
	initJvmtiWithoutCapabilities(env);
	int size=41;
	jbooleanArray result = (*env)->NewBooleanArray(env,size);
	jvmtiCapabilities capabilities;
	(*jvmti)->GetPotentialCapabilities(jvmti, &capabilities);
	jboolean* r;
	r = (jboolean*) allocate(size * sizeof(jboolean));
	r[0]=capabilities.can_tag_objects;
	r[1]=capabilities.can_generate_field_modification_events;
	r[2]=capabilities.can_generate_field_access_events;
	r[3]=capabilities.can_get_bytecodes;
	r[4]=capabilities.can_get_synthetic_attribute;
	r[5]=capabilities.can_get_owned_monitor_info;
	r[6]=capabilities.can_get_current_contended_monitor;
	r[7]=capabilities.can_get_monitor_info;
	r[8]=capabilities.can_pop_frame;
	r[9]=capabilities.can_redefine_classes;
	r[10]=capabilities.can_signal_thread;
	r[11]=capabilities.can_get_source_file_name;
	r[12]=capabilities.can_get_line_numbers;
	r[13]=capabilities.can_get_source_debug_extension;
	r[14]=capabilities.can_access_local_variables;
	r[15]=capabilities.can_maintain_original_method_order;
	r[16]=capabilities.can_generate_single_step_events;
	r[17]=capabilities.can_generate_exception_events;
	r[18]=capabilities.can_generate_frame_pop_events;
	r[19]=capabilities.can_generate_breakpoint_events;
	r[20]=capabilities.can_suspend;
	r[21]=capabilities.can_redefine_any_class;
	r[22]=capabilities.can_get_current_thread_cpu_time;
	r[23]=capabilities.can_get_thread_cpu_time;
	r[24]=capabilities.can_generate_method_entry_events;
	r[25]=capabilities.can_generate_method_exit_events;
	r[26]=capabilities.can_generate_all_class_hook_events;
	r[27]=capabilities.can_generate_compiled_method_load_events;
	r[28]=capabilities.can_generate_monitor_events;
	r[29]=capabilities.can_generate_vm_object_alloc_events;
	r[30]=capabilities.can_generate_native_method_bind_events;
	r[31]=capabilities.can_generate_garbage_collection_events;
	r[32]=capabilities.can_generate_object_free_events;
	r[33]=capabilities.can_force_early_return;
	r[34]=capabilities.can_get_owned_monitor_stack_depth_info;
	r[35]=capabilities.can_get_constant_pool;
	r[36]=capabilities.can_set_native_method_prefix;
	r[37]=capabilities.can_retransform_classes;
	r[38]=capabilities.can_retransform_any_class;
	r[39]=capabilities.can_generate_resource_exhaustion_heap_events;
	r[40]=capabilities.can_generate_resource_exhaustion_threads_events;
	(*env)->SetBooleanArrayRegion(env, result, 0,size-1,r);
	deallocate(r);
	clearJvmti(env);
	return result;
}

