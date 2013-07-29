package com.chenjw.knife.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.chenjw.knife.core.model.Command;
import com.chenjw.knife.core.packet.CommandPacket;
import com.chenjw.knife.core.packet.Packet;
import com.chenjw.knife.core.packet.TextPacket;

public class SocketTest {
    
    public static void read() throws IOException{
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(1122));
        Socket socket = null;
        socket = serverSocket.accept();
        InputStream is = socket.getInputStream();
        while(true){
           Packet packet= PacketResolver.read(is);
           System.out.println(2);
           System.out.println(packet);
        }
    }
    
    public static void write() throws IOException{

        Socket socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", 1122), 3000);
          OutputStream  os = socket.getOutputStream();
         
          PacketResolver.write(new TextPacket("vvvv"), os);
          PacketResolver.write(new CommandPacket(new Command("cccc","ddd")), os);
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        String arch =System.getProperty("os.arch");
        String os=System.getProperty("os.name");
        System.out.println(arch);
        System.out.println(os);
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    read();
                } catch (IOException e) {

                }
            }
            
        }){
            
        }.start();
        Thread.sleep(3000);
        new Thread(new Runnable(){

            @Override
            public void run() {
                try {
                    write();
                } catch (IOException e) {
                
                }
            }
            
        }){
            
        }.start();
    }
}
