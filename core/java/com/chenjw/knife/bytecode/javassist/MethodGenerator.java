package com.chenjw.knife.bytecode.javassist;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

/**
 * 方法构建器
 * 
 * @author chenjw 2012-6-13 上午11:46:07
 */
public interface MethodGenerator {
	public void generate(CtClass ctClass) throws CannotCompileException,
			NotFoundException;
}
