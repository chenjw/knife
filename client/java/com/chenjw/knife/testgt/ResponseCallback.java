package com.chenjw.knife.testgt;

import com.chenjw.knife.client.core.ResultModel;
import com.chenjw.knife.core.model.ResultPart;

public interface ResponseCallback {

	void onPart(ResultPart part);

	void done(ResultModel result);

}