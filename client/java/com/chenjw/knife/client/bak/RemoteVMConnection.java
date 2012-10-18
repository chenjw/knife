package com.chenjw.knife.client.bak;


/**
 * 远程链接
 * 
 * @author chenjw
 * 
 */
public class RemoteVMConnection {

	// private String ip;
	// private int port;
	//
	// private Socket socket;
	// private InputStream is;
	// private OutputStream os;
	//
	// private volatile boolean isConnected = false;
	//
	// public RemoteVMConnection(String ip, int port) throws IOException {
	// this.ip = ip;
	// this.port = port;
	// connect();
	// }
	//
	// private void addShutdownHook() {
	// Runtime.getRuntime().addShutdownHook(new Thread() {
	// @Override
	// public void run() {
	// Command msg = new Command();
	// msg.setName("close");
	// try {
	// if (isConnected) {
	// sendCommand(new CommandPacket(msg));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }
	//
	// private void connect() throws IOException {
	// while (!isConnected) {
	// try {
	// socket = new Socket(ip, port);
	// is = socket.getInputStream();
	// os = socket.getOutputStream();
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
	//
	// }
	//
	// public void close() throws IOException {
	// if (socket != null && socket.isConnected()) {
	// socket.close();
	// }
	// this.isConnected = false;
	// }
	//
	// @Override
	// public void listVM() throws Exception {
	// sendPacket(new ListVMPacket());
	// }
	//
	// @Override
	// public void attachVM(String num) throws Exception {
	// sendPacket(new ChooseVMPacket(num));
	// }
	//
	// @Override
	// public boolean isAttached() throws Exception {
	// sendPacket(new GetIsAttachedPacket());
	// Packet p = readPacket();
	// if (p instanceof ResponseGetIsAttachedPacket) {
	// return ((ResponseGetIsAttachedPacket) p).getObject();
	// } else {
	// throw new Exception("not ResponseGetIsAttachedPacket");
	// }
	// }
	//
	// @Override
	// public void sendCommand(CommandPacket command) throws Exception {
	// if (!isConnected) {
	// throw new Exception("not ready!");
	// }
	// sendPacket(command);
	// }
	//
	// private void sendPacket(Packet p) throws Exception {
	// PacketResolver.write(p, os);
	// }
	//
	// @Override
	// public Packet readPacket() throws Exception {
	// if (!isConnected) {
	// throw new Exception("not ready!");
	// }
	// return PacketResolver.read(is);
	// }

}
