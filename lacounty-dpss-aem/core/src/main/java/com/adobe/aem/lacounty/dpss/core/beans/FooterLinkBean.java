package com.adobe.aem.lacounty.dpss.core.beans;

public class FooterLinkBean {

	String linkText;
	String linkUrl;
	String openInNewTab;

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public String getOpenInNewTab() {
		return openInNewTab;
	}

	public void setOpenInNewTab(String openInNewTab) {
		this.openInNewTab = openInNewTab;
	}
}
