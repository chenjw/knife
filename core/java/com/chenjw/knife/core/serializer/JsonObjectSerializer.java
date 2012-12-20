package com.chenjw.knife.core.serializer;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

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
		try {
			String str = new String(bytes, "UTF-8");
			return JSON.parseObject(str, Object.class);
		} catch (UnsupportedEncodingException e) {
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
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
}
