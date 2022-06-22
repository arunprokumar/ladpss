package com.adobe.aem.lacounty.dpss.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "DPSS Global Services Configuration", description = "DPSS Global Services Configuration")
public @interface DpssGlobalConfig {

	@AttributeDefinition(name = "Search API End Point URL",description = " How to apply card search API end point url including queryParam ex- https://locator.lacounty.gov/lac/Search?find=&near=&cat=826&tag=&loc=&lat=&lon=&page=1&pageSize=10")
	String searchAPIEndPoint() default "https://locator.lacounty.gov/lac/Search?find=&near=&cat=826&tag=&loc=&lat=&lon=&page=1&pageSize=10";
	
	@AttributeDefinition(name = "Adobe Launch URL",description = "Adobe Launch URL")
	String launchConfigUrl() default "//assets.adobedtm.com/c70179af6cc1/689b8691df2c/launch-8cf082ecf6df-staging.min.js";
}

