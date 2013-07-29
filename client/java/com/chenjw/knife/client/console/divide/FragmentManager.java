package com.chenjw.knife.client.console.divide;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chenjw.knife.core.model.divide.BodyElement;
import com.chenjw.knife.core.model.divide.CompositedModel;
import com.chenjw.knife.core.model.divide.Dividable;
import com.chenjw.knife.core.model.divide.FooterFragment;
import com.chenjw.knife.core.model.divide.Fragment;
import com.chenjw.knife.core.model.divide.HeaderFragment;
import com.chenjw.knife.utils.ReflectHelper;
import com.chenjw.knife.utils.invoke.InvokeResult;
import com.chenjw.knife.utils.invoke.MethodInvokeException;

public class FragmentManager {
	private Map<String, CompositedModel> fragmentMap = new ConcurrentHashMap<String, CompositedModel>();

	public Object processFragment(Fragment fragment) {
		Object result = null;
		if (fragment instanceof HeaderFragment) {
			HeaderFragment header = (HeaderFragment) fragment;
			CompositedModel model = new CompositedModel();
			model.setHeader(header);
			model.setBodyElements(new BodyElement[header.getCount()]);
			fragmentMap.put(header.getId(), model);
		} else if (fragment instanceof BodyElement) {
			BodyElement element = (BodyElement) fragment;
			CompositedModel model = fragmentMap.get(element.getId());
			if (model != null) {
				model.getBodyElements()[element.getIndex()] = element;
			}
		} else if (fragment instanceof FooterFragment) {
			CompositedModel model = fragmentMap.get(fragment.getId());
			if (model != null) {
				String type = model.getHeader().getType();
				InvokeResult r = null;
				try {
					r = ReflectHelper.invokeConstructor(type, new Object[0],
							new Object[0], null);
				} catch (MethodInvokeException e) {
					e.printStackTrace();
				}
				if (r.isSuccess() && r.getResult() != null) {
					if (r.getResult() instanceof Dividable) {
						Dividable d = (Dividable) r.getResult();
						Object[] objs = new Object[model.getHeader().getCount()];

						for (BodyElement be : model.getBodyElements()) {
							objs[be.getIndex()] = be.getContent();
						}
						d.combine(objs);
						result = d;
					}
				}
			}
		}
		return result;

	}
}
