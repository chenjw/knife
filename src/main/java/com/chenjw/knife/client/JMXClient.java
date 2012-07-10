package com.chenjw.knife.client;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXClient {

	public static void main(String[] args) {
		try {
			String jndiPath = "jmxrmi";
			String serverhost = "10.20.141.170";
			String serverport = "1234";
			// url=service:jmx:rmi:///jndi/rmi://192.168.8.7:8088/jmxrmi
			String jmxurl = "service:jmx:rmi:///jndi/rmi://" + serverhost + ":"
					+ serverport + "/" + jndiPath;
			System.out.println("jmxurl:" + jmxurl);
			JMXServiceURL url = new JMXServiceURL(jmxurl);
			Map<String, Object> enviMap = new HashMap<String, Object>();

			JMXConnector connector = JMXConnectorFactory.connect(url, enviMap);

			MBeanServerConnection mbsc = connector.getMBeanServerConnection();
			System.out.println("successfulconnected");
			connector.close();
			System.out.println("closeconnect");
		} catch (Exception e) {
			System.out.println("error");
			e.printStackTrace();
		}
	}
}