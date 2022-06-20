package com.adobe.aem.lacounty.dpss.core.models;

import java.util.List;

import com.adobe.aem.lacounty.dpss.core.helper.FooterHelper;



public interface FooterModel {
	
	String getTitle();
	String getAddress();
	String getPhone();
	String getEmail();
	String getTagline();
	String getCopyrights();
	String getReserved();
	String getLogo();
	String getLogolink();
	List<FooterHelper> getBureausLinks();
	List<FooterHelper> getExternalLinks();
	List<FooterHelper> getPolicyLinks();

}
