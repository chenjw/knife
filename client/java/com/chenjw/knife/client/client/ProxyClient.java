package com.chenjw.knife.client.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.client.model.AttachRequest;
import com.chenjw.knife.client.model.AttachResult;
import com.chenjw.knife.client.model.VMDescriptor;
import com.chenjw.knife.core.Packet;
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.packet.RequestPacket;
import com.chenjw.knife.core.packet.ResponsePacket;

public class ProxyClient implements Client {

	private ServerSocket serverSocket;
	private int proxyPort;

	public ProxyClient(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	private void accecpt(VMConnector connector) {
		Socket socket = null;
		String remoteIp = null;
		try {
			socket = serverSocket.accept();
			remoteIp = socket.getRemoteSocketAddress().toString();
			System.out.println(remoteIp + " connected!");
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			Packet packet = null;
			while (true) {
				packet = PacketResolver.read(is);
				if (packet instanceof RequestPacket) {
					RequestPacket rp = (RequestPacket) packet;
					if (Constants.REQUEST_LIST_VM.equals(rp.getObject()
							.getContext()[0])) {
						List<VMDescriptor> r = connector.listVM();
						PacketResolver.write(new ResponsePacket(rp.getObject()
								.getId(), r), os);
					} else if (Constants.REQUEST_ATTACH_VM.equals(rp
							.getObject().getContext()[0])) {
						AttachRequest req = (AttachRequest) rp.getObject()
								.getContext()[1];
						AttachResult res = new AttachResult();
						try {
							connector.attachVM(req.getPid(), req.getPort());
							res.setSuccess(true);
							System.out.println(remoteIp + " attached ("
									+ req.getPid() + ")");
						} catch (Exception e) {
							res.setSuccess(false);
							res.setErrorInfo(e.getLocalizedMessage());
						}
						PacketResolver.write(new ResponsePacket(rp.getObject()
								.getId(), res), os);
					}
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
				}
			}
			socket = null;
			System.out.println(remoteIp + " disconnected!");
		}
	}

	public void start(VMConnector connector) throws Exception {
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(proxyPort));
		System.out.println("proxy started!");
		try {
			while (true) {
				accecpt(connector);
			}
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
