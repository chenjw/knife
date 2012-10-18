package com.chenjw.knife.client.bak;


/**
 * 本地jvm连接
 * 
 * @author chenjw
 * 
 */
public class LocalVMConnection {
	//
	// /**
	// * 返回数据包的缓冲区
	// */
	// private BlockingQueue<Packet> responsePacketBuffer = new
	// LinkedBlockingDeque<Packet>();
	// private List<String> vmIdList;
	// private int port = 2222;
	// private String[] exceptJars = new String[] { "jline-1.0.jar",
	// "knife-client.jar" };
	// private Socket socket;
	// private InputStream is;
	// private OutputStream os;
	// private VirtualMachine vm = null;
	//
	// private volatile boolean isConnected = false;
	// private volatile boolean isAttached = false;
	//
	// public LocalVMConnection() {
	//
	// }
	//
	// private String appendBootstrapJars() {
	// return "";
	// }
	//
	// private String appendSystemJars() {
	// String jars = JarHelper.getToolsJarPath();
	// for (String jar : JarHelper.findJars()) {
	// if (jars.length() != 0) {
	// if (!isExceptJar(jar)) {
	// jars += ";" + jar;
	// }
	// }
	// }
	// return jars;
	// }
	//
	// private boolean isExceptJar(String str) {
	// for (String exceptJar : exceptJars) {
	// if (str.indexOf(exceptJar) != -1) {
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// private String createArgFile(String str) {
	// File tmpFile = null;
	// try {
	// tmpFile = File.createTempFile("agentArgs", ".dat");
	// FileHelper.writeStringToFile(tmpFile, str, "UTF-8");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// tmpFile.deleteOnExit();
	// return tmpFile.getAbsolutePath();
	// }
	//
	// @Override
	// public void listVM() throws IOException {
	// List<VirtualMachineDescriptor> list = VirtualMachine.list();
	// List<String> idList = new ArrayList<String>();
	// int i = 0;
	// String selfJvmId = JvmHelper.getPID();
	// for (VirtualMachineDescriptor vm : list) {
	// if (vm.id().equals(selfJvmId)) {
	// continue;
	// }
	// idList.add(vm.id());
	// sendMessage(i + ". " + vm.displayName());
	// i++;
	// }
	// if (idList.isEmpty()) {
	// sendMessage("cant find vm process!");
	// } else {
	// sendMessage("input [0-" + (i - 1) + "] to choose vm! ");
	// }
	// vmIdList = idList;
	// }
	//
	// private void sendMessage(String str) {
	// responsePacketBuffer.offer(new TextPacket(str));
	// }
	//
	// @Override
	// public void attachVM(String num) throws Exception {
	// if (vmIdList == null) {
	// sendMessage("listVM first!");
	// }
	// String errorMsg = "input [0-" + (vmIdList.size() - 1)
	// + "] to choose vm! ";
	// int n = 0;
	// try {
	// Integer.parseInt(num);
	// } catch (NumberFormatException e) {
	// sendMessage(errorMsg);
	// return;
	// }
	// if (n < 0 || n >= vmIdList.size()) {
	// sendMessage(errorMsg);
	// return;
	// }
	// // attach到jvm
	// String pid = vmIdList.get(n);
	// vm = VirtualMachine.attach(pid);
	// String agentArgs = "port=" + port;
	// agentArgs += "&bootstrapJars=" + appendBootstrapJars();
	// agentArgs += "&systemJars=" + appendSystemJars();
	// vm.loadAgent(JarHelper.findJar("knife-agent.jar"),
	// createArgFile(agentArgs));
	// connect();
	// // 标记为已attach上
	// isAttached = true;
	// }
	//
	// @Override
	// public boolean isAttached() {
	// return isAttached;
	// }
	//
	// @Override
	// public void sendCommand(CommandPacket command) throws Exception {
	// send(command);
	// }
	//
	// @Override
	// public Packet readPacket() throws Exception {
	// try {
	// return responsePacketBuffer.take();
	// } catch (InterruptedException e) {
	// return null;
	// }
	// }
	//
	// private void send(Packet command) throws Exception {
	// if (!isConnected) {
	// throw new Exception("not ready!");
	// }
	// PacketResolver.write(command, os);
	// }
	//
	// private Packet read() throws Exception {
	// if (!isConnected) {
	// throw new Exception("not ready!");
	// }
	// return PacketResolver.read(is);
	// }
	//
	// private void startHandler() {
	// Thread t = new Thread("knife-localconnection-handler") {
	// @Override
	// public void run() {
	// try {
	// while (true) {
	// Packet cmd = read();
	// responsePacketBuffer.offer(cmd);
	// }
	// } catch (Exception e) {
	// return;
	// }
	// }
	// };
	// t.setDaemon(true);
	// t.start();
	// }
	//
	// private void addShutdownHook() {
	// Runtime.getRuntime().addShutdownHook(new Thread() {
	// @Override
	// public void run() {
	// Command msg = new Command();
	// msg.setName("close");
	// try {
	// send(new CommandPacket(msg));
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	//
	// private void connect() {
	// // 创建socket连接
	// while (!isConnected) {
	// try {
	// socket = new Socket("127.0.0.1", port);
	// is = socket.getInputStream();
	// os = socket.getOutputStream();
	// // 创建接收线程
	// startHandler();
	// // 创建关闭
	// addShutdownHook();
	// isConnected = true;
	// break;
	// } catch (Exception e) {
	// }
	// try {
	// Thread.sleep(2000);
	// } catch (InterruptedException e) {
	// }
	// }
	// }
	//
	// public void close() throws IOException {
	// if (socket != null && socket.isConnected()) {
	// socket.close();
	// socket = null;
	// is = null;
	// os = null;
	// }
	// this.isConnected = false;
	// }

}
