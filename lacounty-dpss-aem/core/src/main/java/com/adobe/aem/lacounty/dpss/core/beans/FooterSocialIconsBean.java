package com.adobe.aem.lacounty.dpss.core.beans;

public class FooterSocialIconsBean {

	String fontAwesomeKey;
	String socialLinkUrl;
	String openInNewTab;
	String accessbilityLabel;

	public String getOpenInNewTab() {
		return openInNewTab;
	}

	public void setOpenInNewTab(String openInNewTab) {
		this.openInNewTab = openInNewTab;
	}

	public String getFontAwesomeKey() {
		return fontAwesomeKey;
	}

	public void setFontAwesomeKey(String fontAwesomeKey) {
		this.fontAwesomeKey = fontAwesomeKey;
	}

	public String getSocialLinkUrl() {
		return socialLinkUrl;
	}

	public void setSocialLinkUrl(String socialLinkUrl) {
		this.socialLinkUrl = socialLinkUrl;
	}

	public String getAccessbilityLabel() {
		return accessbilityLabel;
	}

	public void setAccessbilityLabel(String accessbilityLabel) {
		this.accessbilityLabel = accessbilityLabel;
	}
}
