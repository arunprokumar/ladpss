package com.adobe.aem.lacounty.dpss.core.models;

import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.constants.Constants;
import com.adobe.aem.lacounty.dpss.core.services.DpssGlobalService;
import com.adobe.aem.lacounty.dpss.core.utils.PageMetaDataUtils;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;

/**
 * Sling Model for Base Page.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class BasePageModel {

	private String externalizedURL;
	private static final Logger LOGGER = LoggerFactory.getLogger(BasePageModel.class);

	@Self
	SlingHttpServletRequest request;

	@Inject
	private Page currentPage;

	@Inject
	DpssGlobalService launchService;

	@Inject
	SlingSettingsService slingSettingsService;

	@RequestAttribute
	@Optional
	private String path;

	private String langAttribute;
	private String domainName;
	private String siteSection = "home";

	/**
	 * Post construct method to be initialized on model call
	 */
	@PostConstruct
	public void init() {
		LOGGER.debug("Inside BasePageModel Component {} ", path);
		if (null != request) {
			populateDomainName();
		}
		if (null != currentPage) {
			populatePagelanguage();
			populateSiteSection();
		}
		if (null != request) {
			populateExternalizedUrl();
		}

	}

	private void populateSiteSection() {
		if (StringUtils.isNotBlank(currentPage.getLanguage().getLanguage())) {
			String language = currentPage.getLanguage().getLanguage();
			if (!currentPage.getPath().equalsIgnoreCase(Constants.DPSS_CONTENT_ROOT_PATH + language)) {
				String subSection = currentPage.getPath()
						.substring((Constants.DPSS_CONTENT_ROOT_PATH + language).length() + 1);
				if(subSection.indexOf(Constants.FORWARD_SLASH)>=0) {
					subSection = subSection.substring(0, subSection.indexOf(Constants.FORWARD_SLASH));
					if (!language.equalsIgnoreCase(subSection)) {
						siteSection = subSection;
					}
				}else {
					siteSection = subSection;
				}
			}
		}
	}

	/**
	 * populates the current page language
	 */
	private void populatePagelanguage() {
		Locale locale = currentPage.getLanguage();
		if (null != locale) {
			langAttribute = locale.getLanguage();
		}
	}

	/**
	 * creates the externalized url
	 */
	private void populateExternalizedUrl() {
		final ResourceResolver resourceResolver = request.getResourceResolver();
		final Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
		String runMode = PageMetaDataUtils.getCurrentRunmode(slingSettingsService);
		runMode = StringUtils.isNotBlank(runMode) ? runMode : Externalizer.PUBLISH;
		try {
			if (null != externalizer && !path.isEmpty()) {
				externalizedURL = externalizer.externalLink(resourceResolver, runMode, resourceResolver.map(path));
			}
		} catch (IllegalArgumentException e) {
			LOGGER.error("Exception in populateExternalizedUrl {}", e.getMessage(), e);
		}
	}

	/**
	 * Fetch the domain name form the request
	 */
	private void populateDomainName() {
		domainName = request.getServerName();
	}

	/**
	 * This method returns externalized url
	 *
	 * @return externalizedURL String
	 */
	public String getExternalizedURL() {
		return externalizedURL;
	}

	/**
	 * This method returns the language attribute
	 *
	 * @return langAttribute String
	 */
	public String getLangAttribute() {
		return langAttribute;
	}

	public String getDomainName() {
		return domainName;
	}

	public String getLaunchConfigUrl() {
		return launchService.getLaunchConfigUrl();
	}

	public String getSiteSection() {
		return siteSection;
	}
}