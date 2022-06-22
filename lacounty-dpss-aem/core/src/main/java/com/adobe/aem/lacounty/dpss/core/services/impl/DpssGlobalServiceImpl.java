package com.adobe.aem.lacounty.dpss.core.services.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;

import com.adobe.aem.lacounty.dpss.core.config.DpssGlobalConfig;
import com.adobe.aem.lacounty.dpss.core.services.DpssGlobalService;

@Component(service = DpssGlobalService.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = DpssGlobalConfig.class)
public class DpssGlobalServiceImpl implements DpssGlobalService {

	private DpssGlobalConfig config;

	@Activate
	public void activate(DpssGlobalConfig config) {
		this.config = config;
	}

	@Override
	public String getSearchAPIEndPoint() {
		return config.searchAPIEndPoint();
	}
	
	@Override
	public String getLaunchConfigUrl() {
		return config.launchConfigUrl();
	}
}

