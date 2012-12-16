package com.chenjw.knife.agent.utils.jvmti;

public enum Capabilitie {

	can_tag_objects(0, true), //
	can_generate_field_modification_events(1, false), //
	can_generate_field_access_events(2, false), //
	can_get_bytecodes(3, false), //
	can_get_synthetic_attribute(4, false), //
	can_get_owned_monitor_info(5, false), //
	can_get_current_contended_monitor(6, false), //
	can_get_monitor_info(7, false), //
	can_pop_frame(8, false), //
	can_redefine_classes(9, true), //
	can_signal_thread(10, false), //
	can_get_source_file_name(11, true), //
	can_get_line_numbers(12, false), //
	can_get_source_debug_extension(13, false), //
	can_access_local_variables(14, false), //
	can_maintain_original_method_order(15, false), //
	can_generate_single_step_events(16, false), //
	can_generate_exception_events(17, false), //
	can_generate_frame_pop_events(18, false), //
	can_generate_breakpoint_events(19, false), //
	can_suspend(20, false), //
	can_redefine_any_class(21, true), //
	can_get_current_thread_cpu_time(22, false), //
	can_get_thread_cpu_time(23, false), //
	can_generate_method_entry_events(24, false), //
	can_generate_method_exit_events(25, false), //
	can_generate_all_class_hook_events(26, false), //
	can_generate_compiled_method_load_events(27, false), //
	can_generate_monitor_events(28, false), //
	can_generate_vm_object_alloc_events(29, false), //
	can_generate_native_method_bind_events(30, false), //
	can_generate_garbage_collection_events(31, true), //
	can_generate_object_free_events(32, false), //
	can_force_early_return(33, false), //
	can_get_owned_monitor_stack_depth_info(34, false), //
	can_get_constant_pool(35, false), //
	can_set_native_method_prefix(36, false), //
	can_retransform_classes(37, true), //
	can_retransform_any_class(38, true), //
	can_generate_resource_exhaustion_heap_events(39, false), //
	can_generate_resource_exhaustion_threads_events(40, false);

	private int index;
	private boolean isNeed;

	private Capabilitie(int index, boolean isNeed) {
		this.index = index;
		this.isNeed = isNeed;
	}

	public int getIndex() {
		return this.index;
	}

	public boolean isNeed() {
		return isNeed;
	}

}
