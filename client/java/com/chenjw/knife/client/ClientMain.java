package com.chenjw.knife.client;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import com.chenjw.knife.client.client.CommandClient;
import com.chenjw.knife.client.connector.LocalVMConnector;
import com.chenjw.knife.client.connector.RemoteVMConnector;
import com.chenjw.knife.client.console.JlineCommandConsole;
import com.chenjw.knife.client.console.SimpleCommandConsole;
import com.chenjw.knife.client.constants.Constants;
import com.chenjw.knife.client.core.Client;
import com.chenjw.knife.client.core.CommandService;
import com.chenjw.knife.client.core.VMConnector;
import com.chenjw.knife.client.utils.InetHelper;
import com.chenjw.knife.client.utils.ssh.SshClient;
import com.chenjw.knife.utils.FileHelper;
import com.chenjw.knife.utils.JarHelper;
import com.chenjw.knife.utils.PlatformHelper;
import com.chenjw.knife.utils.StringHelper;

public final class ClientMain {

    private static void install(String ip, String userName, String password, String programePath) {
        try {
            boolean neadReinstall = true;
            System.out.print("local version...");
            String localVersion = FileHelper.readFileToString(new File(programePath + "/VERSION"),
                "UTF-8");
            System.out.println(localVersion);
            System.out.print("remote version...");
            byte[] versionFile = InetHelper.scpGet(ip, userName, password, "/tmp/knife/VERSION");
            if (versionFile != null) {
                String remoteVersion;
                remoteVersion = new String(versionFile, "UTF-8");
                System.out.println(remoteVersion);
                if (StringHelper.equals(localVersion, remoteVersion)) {
                    neadReinstall = false;
                }

            } else {
                System.out.println("not found");
            }
            if (neadReinstall) {
                System.out.print("installing...");
                InetHelper.ssh("rm /tmp/knife -rf;",ip, userName, password);
                InetHelper.scpPut(ip, userName, password, programePath, "/tmp/");
                System.out.println(" done!");
            }
        } catch (IOException e) {
            //
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
        CommandService console = null;
        // jline对windows的eclipse控制台支持不好
        if (PlatformHelper.isWindows()) {//&& JarHelper.isDevMode()) {
            console = new SimpleCommandConsole();
        } else {
            console = new JlineCommandConsole();
        }
        Client client = new CommandClient(console);
        VMConnector connector = null;
        if (args == null || args.length == 0) {
            connector = new LocalVMConnector();
        } else if (args.length == 1 && "view".equals(args[0])) {
            if ("view".equals(args[0])) {
                for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
                    System.out.println(entry.getKey() + "=" + entry.getValue());
                }
            }
        } else {

            String ip = args[0];
            if (args.length >= 2) {
                String userName = StringHelper.substringBefore(args[1], "/");
                String password = StringHelper.substringAfterLast(args[1], "/");

                String programePath = JarHelper.findJarFolder().getParentFile().getAbsolutePath();
                // 安装
                install(ip, userName, password, programePath);
                System.out.print("connecting...");
                final SshClient sshClient = InetHelper.ssh(ip, userName, password);
                System.out.println(" done!");
                System.out.print("proxy starting...");
                sshClient.exec("cd /tmp/knife/;sh proxy.sh -d;");
                while (true) {
                    String line = sshClient.readLine();
                    if (line != null && line.indexOf(Constants.PROXY_STARTED_MESSAGE) != -1) {
                        break;
                    }
                }
                System.out.println(" done!");
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {

                        sshClient.close();
                    }
                });
            }
            connector = new RemoteVMConnector(ip, Constants.DEFAULT_PROXY_PORT);
        }
        client.start(connector);
    }
}
