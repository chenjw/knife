JVMTI介绍                {#jvmti}
========

# JVMTI是什么？

The JVMTM Tool Interface (JVM TI) 是jvm提供给开发和监控工具使用的编程接口。 可以用于检测jvm的状态并控制应用程序在jvm中的运行。

jvmti是jvm定义的一个标准，但并不是所有的jvm都实现了jvmti，可使用GetPotentialCapabilities方法返回当前jvm支持的特性。

有c和c++两个版本，定义都在头文件jvmti.h中（jdk1.6.0_34/include/jvmti.h）

# JVMTI能做什么？

线程相关：
1. 获得线程信息。
2. Suspend、Resume、Stop、Interrupt线程。 
3. Get information about the monitors owned by the specified thread. 

调试
1. 设置、清除断点。
2. 设置清除属性（field）监控。

类相关
1. 获得已加载的类。
2. Retransform。
3. Redefine。

堆相关
1. 遍历堆中的对象。
2. 遍历堆中对象的关系。
3. 设置对象标记和类标记。
4. GC。

线程栈(stack)相关
1. 获得栈深度、遍历帧(frame)。
2. 去除最顶层的帧。
3. 强制返回。
4. 获得帧中的本地变量。

对象相关
1. 占用内存大小。
2. 对象monitor信息，包括owner线程、等待队列中的线程。
3. 修改属性值。

方法相关
1. 监听方法调用事件。
2. 行号信息。


获得jvm实例的方式

1. 使用instrument api

（1）onload模式

~~~~~~~~~~
	-agentlib:<agent-lib-name>=<options> 
	-agentpath:<path-to-agent>=<options> 
~~~~~~~~~~

~~~~~~~~~~
	JNIEXPORT jint JNICALL 
	Agent_OnLoad(JavaVM *vm, char *options, void *reserved)
~~~~~~~~~~

（2）运行时模式

~~~~~~~~~~
	com.sun.tools.attach.VirtualMachine.VirtualMachine.loadAgentLibrary(String);
~~~~~~~~~~

~~~~~~~~~~
	JNIEXPORT jint JNICALL 
	Agent_OnAttach(JavaVM* vm, char *options, void *reserved)
~~~~~~~~~~

2. 使用jni

c

~~~~~~~~~~
	void Java_com_chenjw_knife_agent_utils_NativeHelper_countReferree0(JNIEnv * env, jclass thisClass, jint maxNum, jlongArray countArray,	jobjectArray objArray) {
~~~~~~~~~~

~~~~~~~~~~
	jvmtiEnv *jvmti=NULL;
	JavaVM *jvm = NULL;
	(*env)->GetJavaVM(env,&jvm);
	(*jvm)->GetEnv(jvm,(void **)&jvmti, JVMTI_VERSION_1_1);
~~~~~~~~~~

java

~~~~~~~~~~
	System.load(String absolutePath);
~~~~~~~~~~

~~~~~~~~~~
	private native static void countReferree0(int num, long[] nums,	Object[] objs);
~~~~~~~~~~



