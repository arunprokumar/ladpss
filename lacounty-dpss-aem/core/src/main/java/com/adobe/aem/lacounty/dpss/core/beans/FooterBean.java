package com.adobe.aem.lacounty.dpss.core.beans;

import java.util.List;

public class FooterBean {
	String logo;
	String logoAltTxt;
	String logoLink;
	String copyRightTxt;
	List<FooterLinkBean> links;
	List<FooterSocialIconsBean> socialIcons;

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

	public List<FooterLinkBean> getLinks() {
		return links;
	}

	public void setLinks(List<FooterLinkBean> links) {
		this.links = links;
	}

	public String getLogoLink() {
		return logoLink;
	}

	public void setLogoLink(String logoLink) {
		this.logoLink = logoLink;
	}

	public List<FooterSocialIconsBean> getSocialIcons() {
		return socialIcons;
	}

	public void setSocialIcons(List<FooterSocialIconsBean> socialIcons) {
		this.socialIcons = socialIcons;
	}

	public String getCopyRightTxt() {
		return copyRightTxt;
	}

	public void setCopyRightTxt(String copyRightTxt) {
		this.copyRightTxt = copyRightTxt;
	}
}
