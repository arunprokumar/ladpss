package com.adobe.aem.lacounty.dpss.core.utils;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.settings.SlingSettingsService;

import com.adobe.aem.lacounty.dpss.core.constants.Constants;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;

public class PageMetaDataUtils {
	private PageMetaDataUtils() {
	}

	/**
	 * This method is used to return the current run mode
	 * 
	 * @param slingSettingsService SlingSettingsService
	 *
	 * @return runmode
	 */
	public static String getCurrentRunmode(SlingSettingsService slingSettingsService) {
		String runMode;
		final Set<String> runModes = slingSettingsService.getRunModes();
		final String publishMode = runModes.contains(Externalizer.PUBLISH) ? Externalizer.PUBLISH : StringUtils.EMPTY;
		runMode = runModes.contains(Externalizer.AUTHOR) ? Externalizer.AUTHOR : publishMode;
		return runMode;
	}
	
	public static boolean isHomePage(Page inputPage) {
		boolean isHomePage=Boolean.FALSE;
		Template currentPageTemplate = inputPage.getTemplate();
		if (null != currentPageTemplate) {
			String templateName = currentPageTemplate.getName();
			if (null != templateName && templateName.equals(Constants.HOMEPAGE_TEMPLATE_NAME)) {
				isHomePage=Boolean.TRUE;
			}
		}
		return isHomePage;
	}
	
	public static boolean isProgrameOrServicesPage(Page inputPage) {
		boolean isProgramsOrServicesPage=Boolean.FALSE;
		Template currentPageTemplate = inputPage.getTemplate();
		if (null != currentPageTemplate) {
			String templateName = currentPageTemplate.getName();
			if (null != templateName && templateName.equals(Constants.PROGRAMES_SERVICES_TEMPLATE_NAME)) {
				isProgramsOrServicesPage=Boolean.TRUE;
			}
		}
		return isProgramsOrServicesPage;
	}

}