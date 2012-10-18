package com.chenjw.knife.client.bak;

import java.io.IOException;

import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;

public class TestJmx1 {
	public static void main(String[] args) throws IOException {
		JMXServiceURL u = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"
				+ "10.20.142.82" + ":" + "9004" + "/jmxrmi");
		JMXConnector c = JMXConnectorFactory.connect(u);
		for (ObjectName objName : c.getMBeanServerConnection().queryNames(null,
				null)) {
			System.out.println("objName = " + objName);
		}

		JMXConnectorServer cc = null;
		// cc.
		System.out.println(c.getMBeanServerConnection().getMBeanCount());
		System.out.println("finished!");
	}
}
