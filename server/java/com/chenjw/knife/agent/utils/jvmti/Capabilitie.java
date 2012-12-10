package com.chenjw.knife.agent.utils.jvmti;

public enum Capabilitie {
	can_tag_objects(0), //
	can_generate_field_modification_events(1), //
	can_generate_field_access_events(2), //
	can_get_bytecodes(3), //
	can_get_synthetic_attribute(4), //
	can_get_owned_monitor_info(5), //
	can_get_current_contended_monitor(6), //
	can_get_monitor_info(7), //
	can_pop_frame(8), //
	can_redefine_classes(9), //
	can_signal_thread(10), //
	can_get_source_file_name(11), //
	can_get_line_numbers(12), //
	can_get_source_debug_extension(13), //
	can_access_local_variables(14), //
	can_maintain_original_method_order(15), //
	can_generate_single_step_events(16), //
	can_generate_exception_events(17), //
	can_generate_frame_pop_events(18), //
	can_generate_breakpoint_events(19), //
	can_suspend(20), //
	can_redefine_any_class(21), //
	can_get_current_thread_cpu_time(22), //
	can_get_thread_cpu_time(23), //
	can_generate_method_entry_events(24), //
	can_generate_method_exit_events(25), //
	can_generate_all_class_hook_events(26), //
	can_generate_compiled_method_load_events(27), //
	can_generate_monitor_events(28), //
	can_generate_vm_object_alloc_events(29), //
	can_generate_native_method_bind_events(30), //
	can_generate_garbage_collection_events(31), //
	can_generate_object_free_events(32), //
	can_force_early_return(33), //
	can_get_owned_monitor_stack_depth_info(34), //
	can_get_constant_pool(35), //
	can_set_native_method_prefix(36), //
	can_retransform_classes(37), //
	can_retransform_any_class(38), //
	can_generate_resource_exhaustion_heap_events(39), //
	can_generate_resource_exhaustion_threads_events(40);

	private int index;

	private Capabilitie(int index) {
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

}
