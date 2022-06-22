package com.adobe.aem.lacounty.dpss.core.services;

public interface DpssGlobalService {

	default String getSearchAPIEndPoint() {
		return "https://locator.lacounty.gov/lac/Search?find=&near=&cat=826&tag=&loc=&lat=&lon=&page=1&pageSize=10";
	}
	
	default String getLaunchConfigUrl() {
		return "//assets.adobedtm.com/c70179af6cc1/689b8691df2c/launch-8cf082ecf6df-staging.min.js";
	}
}
