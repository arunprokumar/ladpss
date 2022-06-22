package com.adobe.aem.lacounty.dpss.core.models;

import java.util.List;

import com.adobe.aem.lacounty.dpss.core.helper.LAFooterHelper;

public interface LAFooterModel {

	String getTitle();

	String getAddress();

	String getPhone();

	String getEmail();

	String getTagline();

	String getCopyrights();

	String getReserved();

	String getLogo();

	String getLogolink();

	List<LAFooterHelper> getBureausLinks();

	List<LAFooterHelper> getExternalLinks();

	List<LAFooterHelper> getPolicyLinks();

}
