package com.chenjw.knife.client.formater;

import java.util.ArrayList;
import java.util.List;

import com.chenjw.knife.core.model.ClassMethodInfo;
import com.chenjw.knife.core.model.MethodInfo;
import com.chenjw.knife.utils.StringHelper;

public class ClassMethodFormater extends BasePrintFormater<ClassMethodInfo> {

	@Override
	protected void print(ClassMethodInfo classMethodInfo) {
		PreparedTableFormater table = new PreparedTableFormater(level, printer,
				grep);
		table.setTitle("idx", "type", "method");
		MethodInfo[] methodInfos = classMethodInfo.getMethods();
		List<String> methodNames=new ArrayList<String>();
		if (methodInfos != null) {
			int i = 0;
			for (MethodInfo method : methodInfos) {
				methodNames.add(method.getName());
				table.addLine(
						String.valueOf(i),
						method.isStatic() ? "[static-method]" : "[method]",
						method.getName()
								+ "("
								+ StringHelper.join(
										method.getParamClassNames(), ",") + ")");
				i++;
			}

		}
		table.print();
		this.completeHandler.setArgCompletors(methodNames.toArray(new String[methodNames.size()]));
		this.printLine("finished!");
	}

}
