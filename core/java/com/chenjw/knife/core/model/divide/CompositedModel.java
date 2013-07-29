package com.chenjw.knife.core.model.divide;

import java.io.Serializable;

public class CompositedModel implements Serializable {

	private static final long serialVersionUID = 5532269405839948008L;
	private HeaderFragment header;
	private BodyElement[] bodyElements;

	private FooterFragment footer;

	public HeaderFragment getHeader() {
		return header;
	}

	public void setHeader(HeaderFragment header) {
		this.header = header;
	}

	public BodyElement[] getBodyElements() {
		return bodyElements;
	}

	public void setBodyElements(BodyElement[] bodyElements) {
		this.bodyElements = bodyElements;
	}

	public FooterFragment getFooter() {
		return footer;
	}

	public void setFooter(FooterFragment footer) {
		this.footer = footer;
	}

}
