package com.adobe.aem.lacounty.dpss.core.beans;

public class HomePageBannerButtonBean {

	String label;
	String fontAwesomeKey;
	String inkUrl;
	String openInNewTab;
	String accessbilityLabel;
	//Will be set only for YBN button type
	String isYbnButton;
	String ybnImage;
	String ybnImageAltText;
	public String getLabel() {
		return label;
	}
	public String getFontAwesomeKey() {
		return fontAwesomeKey;
	}
	public String getInkUrl() {
		return inkUrl;
	}
	public String getOpenInNewTab() {
		return openInNewTab;
	}
	public String getAccessbilityLabel() {
		return accessbilityLabel;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public void setFontAwesomeKey(String fontAwesomeKey) {
		this.fontAwesomeKey = fontAwesomeKey;
	}
	public void setInkUrl(String inkUrl) {
		this.inkUrl = inkUrl;
	}
	public void setOpenInNewTab(String openInNewTab) {
		this.openInNewTab = openInNewTab;
	}
	public void setAccessbilityLabel(String accessbilityLabel) {
		this.accessbilityLabel = accessbilityLabel;
	}
	public String getYbnImage() {
		return ybnImage;
	}
	public String getYbnImageAltText() {
		return ybnImageAltText;
	}
	public void setYbnImage(String ybnImage) {
		this.ybnImage = ybnImage;
	}
	public void setYbnImageAltText(String ybnImageAltText) {
		this.ybnImageAltText = ybnImageAltText;
	}
	public String getIsYbnButton() {
		return isYbnButton;
	}
	public void setIsYbnButton(String isYbnButton) {
		this.isYbnButton = isYbnButton;
	}
}
