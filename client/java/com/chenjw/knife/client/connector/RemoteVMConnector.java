package com.chenjw.knife.client.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import com.chenjw.knife.client.connection.DefaultVMConnection;
import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.VMConnection;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.client.model.AttachRequest;
import com.chenjw.knife.client.model.AttachResult;
import com.chenjw.knife.client.model.VMDescriptor;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.packet.RequestPacket;
import com.chenjw.knife.core.packet.ResponsePacket;

/**
 * 远程链接
 * 
 * @author chenjw
 * 
 */
public class RemoteVMConnector implements VMConnector {

	private String ip;
	private int port;

	private Socket socket;
	private InputStream is;
	private OutputStream os;

	private volatile boolean isConnected = false;

	public RemoteVMConnector(String ip, int port) throws IOException {
		this.ip = ip;
		this.port = port;

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VMDescriptor> listVM() throws Exception {
		sendPacket(new RequestPacket(Constants.REQUEST_LIST_VM));
		Packet p = readPacket();
		ResponsePacket rp = (ResponsePacket) p;
		return (List<VMDescriptor>) rp.getObject().getContext()[0];
	}

	@Override
	public void attachVM(String pid, int port) throws Exception {
		connect();
		AttachRequest r = new AttachRequest();
		r.setPid(pid);
		r.setPort(port);
		sendPacket(new RequestPacket(Constants.REQUEST_ATTACH_VM, r));
		Packet p = readPacket();
		ResponsePacket rp = (ResponsePacket) p;
		AttachResult result = (AttachResult) rp.getObject().getContext()[0];
		if (!result.isSuccess()) {
			throw new Exception(result.getErrorInfo());
		}
	}

	public VMConnection createVMConnection(int port) throws Exception {
		return new DefaultVMConnection(ip, port);
	}

	private void sendPacket(Packet p) throws Exception {
		connect();
		PacketResolver.write(p, os);
	}

	private Packet readPacket() throws Exception {
		connect();
		return PacketResolver.read(is);
	}

	private void connect() throws IOException {
		if (!isConnected) {
			try{
				socket = new Socket(ip, port);
			}
			catch(IOException e){
				throw new IOException(ip+":"+port+" 连接不上，请确保目标机器防火墙端口已打开！",e);
			}
			is = socket.getInputStream();
			os = socket.getOutputStream();
			isConnected = true;
		}
	}
}
