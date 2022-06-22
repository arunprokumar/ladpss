package com.adobe.aem.lacounty.dpss.core.beans;

import java.util.List;

public class HeaderBean {
	String logo;
	String logoAltTxt;
	String logoLink;
	String logoTxt;
	String logoTextLink;
	String menulabel;
	String searchPlaceholder;
	String resultpage; 
	String showSearch;
	String currentLangName;
	String langListInfoText;
	String alertNotificationExfPath;
	List<HeaderLinkBean> links;
	List<HeaderLanguageBean> languages;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getLogoAltTxt() {
		return logoAltTxt;
	}

	public void setLogoAltTxt(String logoAltTxt) {
		this.logoAltTxt = logoAltTxt;
	}

	public List<HeaderLinkBean> getLinks() {
		return links;
	}

	public void setLinks(List<HeaderLinkBean> links) {
		this.links = links;
	}

	public String getLogoLink() {
		return logoLink;
	}

	public void setLogoLink(String logoLink) {
		this.logoLink = logoLink;
	}

	public String getLogoTxt() {
		return logoTxt;
	}

	public void setLogoTxt(String logoTxt) {
		this.logoTxt = logoTxt;
	}

	public String getMenulabel() {
		return menulabel;
	}

	public void setMenulabel(String menulabel) {
		this.menulabel = menulabel;
	}

	public String getSearchPlaceholder() {
		return searchPlaceholder;
	}

	public void setSearchPlaceholder(String searchPlaceholder) {
		this.searchPlaceholder = searchPlaceholder;
	}

	public String getLogoTextLink() {
		return logoTextLink;
	}

	public void setLogoTextLink(String logoTextLink) {
		this.logoTextLink = logoTextLink;
	}

	public String getShowSearch() {
		return showSearch;
	}

	public void setShowSearch(String showSearch) {
		this.showSearch = showSearch;
	}

	public List<HeaderLanguageBean> getLanguages() {
		return languages;
	}

	public void setLanguages(List<HeaderLanguageBean> languages) {
		this.languages = languages;
	}
	public String getCurrentLangName() {
		return currentLangName;
	}
	public void setCurrentLangName(String currentLangName) {
		this.currentLangName = currentLangName;
	}

	public String getLangListInfoText() {
		return langListInfoText;
	}

	public void setLangListInfoText(String langListInfoText) {
		this.langListInfoText = langListInfoText;
	}

	public String getAlertNotificationExfPath() {
		return alertNotificationExfPath;
	}

	public void setAlertNotificationExfPath(String alertNotificationExfPath) {
		this.alertNotificationExfPath = alertNotificationExfPath;
	}
	
	public String getResultpage() {
		return this.resultpage;
	}
	
	public void setResultpage(String resultpage) {
		this.resultpage = resultpage; 
	}
}
