package com.chenjw.knife.client.core;

import com.chenjw.knife.core.model.ResultPart;

public interface ResponseCallback {

	void onPart(ResultPart part);

	void done(ResultModel result);

}