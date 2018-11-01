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
import com.chenjw.knife.core.PacketResolver;
import com.chenjw.knife.core.model.Result;
import com.chenjw.knife.core.model.VMDescriptor;
import com.chenjw.knife.core.packet.CommandPacket;
import com.chenjw.knife.core.packet.Packet;
import com.chenjw.knife.core.packet.ResultPacket;

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
				if (packet instanceof CommandPacket) {
					CommandPacket rp = (CommandPacket) packet;
					if (Constants.REQUEST_LIST_VM.equals(rp.getObject()
							.getName())) {
						List<VMDescriptor> r = connector.listVM();
						Result result = new Result(rp.getObject().getId());
						result.setSuccess(true);
						result.setContent(r);

						PacketResolver.write(new ResultPacket(result), os);
					} else if (Constants.REQUEST_ATTACH_VM.equals(rp
							.getObject().getName())) {
						AttachRequest req = (AttachRequest) rp.getObject()
								.getArgs();

						Result result = new Result(rp.getObject().getId());
						try {
							connector.attachVM(req.getPid(), req.getPort());
							result.setSuccess(true);
							System.out.println(remoteIp + " attached ("
									+ req.getPid() + ")");
						} catch (Exception e) {
							result.setSuccess(false);
							result.setErrorMessage(e.getLocalizedMessage());
						}
						PacketResolver.write(new ResultPacket(result), os);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
				  e.printStackTrace();
				}
			}
			socket = null;
			System.out.println(remoteIp + " disconnected!");
		}
	}

	public void start(VMConnector connector) throws Exception {
		serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress(proxyPort));
		System.out.println(Constants.PROXY_STARTED_MESSAGE);
		try {
			while (true) {
				accecpt(connector);
			}
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				  e.printStackTrace();
				}
			}
		}
	}
}
