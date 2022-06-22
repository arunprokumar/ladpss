package com.adobe.aem.lacounty.dpss.core.beans;

public class HeaderLinkBean {

	String linkText;
	String linkUrl;
	String openInNewTab;
	String linkIcon;

	public String getLinkIcon() {
		return linkIcon;
	}

	public void setLinkIcon(String linkIcon) {
		this.linkIcon = linkIcon;
	}

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
