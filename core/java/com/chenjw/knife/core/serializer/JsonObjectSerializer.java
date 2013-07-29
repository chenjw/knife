package com.chenjw.knife.core.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chenjw.knife.core.model.result.ClassInfo;

/**
 * 使用fastjson方式实现的对象序列化和反序列化工具
 * 
 * @author chenjw
 * 
 */
public class JsonObjectSerializer implements ObjectSerializer {

	@Override
	public Object toObject(byte[] bytes) {
		if (bytes == null || bytes.length == 0) {
			return null;
		}
		String str = null;
		try {
			str = new String(bytes, "UTF-8");
			return JSON.parseObject(str, Object.class);
		} catch (Exception e) {
			System.out.println(str);
			e.printStackTrace();
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			return null;
		}
	}

	@Override
	public byte[] fromObject(Object obj) {
		if (obj == null) {
			return new byte[0];
		}
		try {
			String str = JSON
					.toJSONString(
							obj,
							new SerializerFeature[] { SerializerFeature.WriteClassName });
			byte[] b = str.getBytes("UTF-8");
			return b;
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		ClassInfo c = new ClassInfo();
		c.setName("aaa");
		String s = JSON.toJSONString(c,
				new SerializerFeature[] { SerializerFeature.WriteClassName });
		System.out.println(s);
	}
}
