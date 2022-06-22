package com.adobe.aem.lacounty.dpss.core.beans;

public class HeaderLanguageBean {

	String langName;
	String langHomePageUrl;
	String otherLangPageLink;
	public String getLangName() {
		return langName;
	}
	public String getLangHomePageUrl() {
		return langHomePageUrl;
	}
	public void setLangName(String langName) {
		this.langName = langName;
	}
	public void setLangHomePageUrl(String langHomePageUrl) {
		this.langHomePageUrl = langHomePageUrl;
	}
	public String getOtherLangPageLink() {
		return otherLangPageLink;
	}
	public void setOtherLangPageLink(String otherLangPageLink) {
		this.otherLangPageLink = otherLangPageLink;
	}
	
}
