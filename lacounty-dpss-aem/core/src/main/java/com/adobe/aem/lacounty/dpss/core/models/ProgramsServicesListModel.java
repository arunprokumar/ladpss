package com.adobe.aem.lacounty.dpss.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class ProgramsServicesListModel {
	private static final Logger LOG = LoggerFactory.getLogger(ProgramsServicesListModel.class);

	private static final String ROOTPAGE_FIELD_NAME = "pgmsSrvsRootPage";

	@ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "No resourceType")
	protected String resourceType;

	@ValueMapValue(name = ROOTPAGE_FIELD_NAME)
	@Optional
	private String pgmsSrvsRootPage;

	@PostConstruct
	protected void init() {
		if (StringUtils.isNotEmpty(pgmsSrvsRootPage)) {
			LOG.debug("Configured Root page path: {}", pgmsSrvsRootPage);
		}
	}

	public String getPgmsSrvsRootPage() {
		return pgmsSrvsRootPage;
	}

}
