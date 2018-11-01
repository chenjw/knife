package com.chenjw.knife.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import com.chenjw.knife.core.packet.Packet;
import com.chenjw.knife.utils.IOHelper;

/**
 * 格式
 * 
 * magic(5位) 类型长度(8位) 类型 内容长度(8位) 内容
 * 
 * 
 * @author chenjw
 * 
 */
public class PacketResolver {

  private static final String MAGIC = "KNIFE";
  private static int MAGIC_LENGTH;
  static {
    try {
      MAGIC_LENGTH = MAGIC.getBytes("UTF-8").length;
    } catch (UnsupportedEncodingException e) {
    }
  }

  private static Object netInstance(Class<?> clazz) {
    try {
      return clazz.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static Packet read(InputStream is) throws IOException {
    // System.out.println(is);
    synchronized (is) {
      Packet packet = null;
      checkMagic(is);
      int typeLength = readInt(is);

      String type = readType(is, typeLength);

      packet = initPacket(type);
      int contentLength = readInt(is);

      byte[] bytes = null;
      if (contentLength == 0) {
        bytes = new byte[0];
      } else {
        bytes = new byte[contentLength];
        IOHelper.readBytes(is, bytes);
      }
      packet.fromBytes(bytes);

      return packet;
    }

  }

  public static void write(Packet packet, OutputStream os) throws IOException {
    // 构建完再统一写，防止写一半再序列化出错的情况下，读数据异常
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    bos.write(MAGIC.getBytes("UTF-8"));

    byte[] type = packet.getClass().getName().getBytes("UTF-8");

    bos.write(int2bytes(type.length));
    bos.write(type);
    byte[] content = packet.toBytes();

    bos.write(int2bytes(content.length));
    if (content.length > 0) {
      bos.write(content);

    }

    synchronized (os) {

      os.write(bos.toByteArray());

    }

  }

  private static int readInt(InputStream is) throws IOException {

    byte[] bytes = new byte[4];
    int size = IOHelper.readBytes(is, bytes);
    if (size != 4) {
      throw new IOException("read " + size + " expect 4");
    }
    return bytes2int(bytes);
  }

  private static void checkMagic(InputStream is) throws IOException {

    byte[] bytes = new byte[MAGIC_LENGTH];


    int size = IOHelper.readBytes(is, bytes);
    if (size != MAGIC_LENGTH) {
      throw new IOException("read " + size + " expect " + MAGIC_LENGTH);
    }
    if (!MAGIC.equals(new String(bytes, "UTF-8"))) {
      throw new IOException("MAGIC check fail (" + new String(bytes, "UTF-8") + ")");
    }
  }

  private static Packet initPacket(String type) throws IOException {
    try {
      Class<?> clazz = Class.forName(type);
      return (Packet) netInstance(clazz);
    } catch (ClassNotFoundException e) {
      throw new IOException("type not found (" + type + ")");
    }
  }

  private static String readType(InputStream is, int length) throws IOException {

    byte[] bytes = new byte[length];
    int size = IOHelper.readBytes(is, bytes);
    if (size != length) {
      throw new IOException("read " + size + " expect " + length);
    }
    return new String(bytes, "UTF-8");
  }


  public static int bytes2int(byte[] b) {

    int l = 0;
    l = b[0];
    l &= 0xff;
    l |= ((int) b[1] << 8);
    l &= 0xffff;
    l |= ((int) b[2] << 16);
    l &= 0xffffff;
    l |= ((int) b[3] << 24);
    l &= 0xffffffff;
    return l;
  }

  public static byte[] int2bytes(int l) {
    byte[] b = new byte[4];
    for (int i = 0; i < b.length; i++) {
      b[i] = new Integer(l).byteValue();
      l = l >> 8;
    }
    return b;
  }

  public static void main(String[] args) {
    byte[] b = int2bytes(133);
    System.out.println(b.length);
    long l = bytes2int(b);
    System.out.println(l);
  }
}
