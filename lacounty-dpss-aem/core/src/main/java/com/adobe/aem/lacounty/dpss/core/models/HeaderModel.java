package com.adobe.aem.lacounty.dpss.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.beans.HeaderBean;
import com.adobe.aem.lacounty.dpss.core.beans.HeaderLanguageBean;
import com.adobe.aem.lacounty.dpss.core.beans.HeaderLinkBean;
import com.adobe.aem.lacounty.dpss.core.constants.Constants;
import com.adobe.aem.lacounty.dpss.core.corecomponents.internal.Utils;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.adobe.aem.lacounty.dpss.core.utils.PageMetaDataUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class HeaderModel {
	private static final Logger LOG = LoggerFactory.getLogger(HeaderModel.class);
	private static final String HEADER_RESOURCE_PATH = "jcr:content/root/header";
	private static final String HEADER_LOGO_FIELD_NAME = "logo";
	private static final String HEADER_LOGOALTTXT_FIELD_NAME = "logoAltTxt";
	private static final String HEADER_LOGOALINK_FIELD_NAME = "logoLink";
	private static final String HEADER_LOGOTXT_FIELD_NAME = "logoTxt";
	private static final String HEADER_LINKS_RESOURCE_NAME = "links";
	private static final String HEADER_LINKFIELD_NAME = "text";
	private static final String HEADER_LINKURL_NAME = "link";
	private static final String HEADER_SEARCHLABELFIELD_NAME = "searchPlaceholder";
	private static final String HEADER_MENULABELFIELD_NAME = "menulabel";
	private static final String HEADER_OPENLINKIN_NAME = "openInNewTab";
	private static final String HEADER_LOGOTEXLINKFIELD_NAME = "logoTextLink";
	private static final String HEADER_LANGUAGES_RESOURCE_NAME = "languages";
	private static final String HEADER_LANGFIELD_NAME = "langName";
	private static final String HEADER_LANG_HOMEPAGEURL_NAME = "langHomePagePath";
	private static final String HEADER_LANG_DROPDOWN_PLACEHOLDER_FIELDNAME = "langListInfoText";
	private static final String ALERT_NOTIFICATION_FLAG = "isAlertNotificationOn";
	private static final String ALERT_NOTIFICATION_EXFPATH = "alertNotificationExfPath";
	private static final String HEADER_SEARCHRESULT_PAGE = "resultpage";
	@ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "No resourceType")
	protected String resourceType;

	@Self
	private SlingHttpServletRequest request; 
	
	@Inject
	private Page currentPage;

	@SlingObject
	private ResourceResolver resourceResolver;

	@ScriptVariable
	PageManager pageManager;
	
	HeaderBean headerConfigurations;
	Page homePage;

	@PostConstruct
	protected void init() {
		LOG.debug("Header Model executing");
		if (currentPage == null) {
			return;
		}
		Resource headerConfigResource = getHeaderConfigsResource();
		if (null == headerConfigResource) {
			LOG.debug("No Header configuration found!");
			return;
		}
		processHeaderConfigs(headerConfigResource);
		processAlertConfigs(headerConfigResource);
		LOG.debug("Header Model sucessfully executated!");
	}

	private Resource getHeaderConfigsResource() {
		Resource headerConfigsResource = null;
		int currentPageDepth = currentPage.getDepth();
		for (int index = currentPageDepth; index > 1; index--) {
			Page parentPage = currentPage.getAbsoluteParent(index);
			if (null != parentPage) {
				Resource parentPageResource = parentPage.adaptTo(Resource.class);
				if (null != parentPageResource) {
					headerConfigsResource = parentPageResource.getChild(HEADER_RESOURCE_PATH);
					if (null != headerConfigsResource && PageMetaDataUtils.isHomePage(parentPage)) {
						homePage = parentPage;
						String headerConfigsPath = headerConfigsResource.getPath();
						LOG.debug("Header cofiguraions path {}", headerConfigsPath);
						return headerConfigsResource;
					}
				}

			}
		}
		return headerConfigsResource;
	}

	private void processHeaderConfigs(Resource headerConfigResource) {
		ValueMap headerValueMap = headerConfigResource.getValueMap();
		if (null != headerValueMap) {
			headerConfigurations = new HeaderBean();
			if (headerValueMap.containsKey(HEADER_LOGO_FIELD_NAME)) {
				headerConfigurations.setLogo(headerValueMap.get(HEADER_LOGO_FIELD_NAME, String.class));
			}
			if (headerValueMap.containsKey(HEADER_LOGOALTTXT_FIELD_NAME)) {
				headerConfigurations.setLogoAltTxt(headerValueMap.get(HEADER_LOGOALTTXT_FIELD_NAME, "DPSS Logo"));
			}
			if (headerValueMap.containsKey(HEADER_LOGOALINK_FIELD_NAME)) {
				headerConfigurations.setLogoLink(ComponentUtils.getURL(request, pageManager,headerValueMap.get(HEADER_LOGOALINK_FIELD_NAME, String.class)));
			}
			if (headerValueMap.containsKey(HEADER_LOGOTXT_FIELD_NAME)) {
				headerConfigurations.setLogoTxt(headerValueMap.get(HEADER_LOGOTXT_FIELD_NAME, "LACOUNTY.GOV"));
			}
			if (headerValueMap.containsKey(HEADER_SEARCHLABELFIELD_NAME)) {
				headerConfigurations.setSearchPlaceholder(headerValueMap.get(HEADER_SEARCHLABELFIELD_NAME, "Search"));
			}
			if(headerValueMap.containsKey(HEADER_SEARCHRESULT_PAGE)) {
				String resultPage = headerValueMap.get(HEADER_SEARCHRESULT_PAGE, "/content/dpss");
				if (StringUtils.isNotEmpty(resultPage)) {
				    resultPage = Utils.getURL(request, currentPage.getPageManager(), resultPage);
				}
				headerConfigurations.setResultpage(resultPage);
			}
			if (headerValueMap.containsKey(HEADER_MENULABELFIELD_NAME)) {
				headerConfigurations.setMenulabel(headerValueMap.get(HEADER_MENULABELFIELD_NAME, "Menu"));
			}
			if (headerValueMap.containsKey(HEADER_LOGOTEXLINKFIELD_NAME)) {
				headerConfigurations.setLogoTextLink(ComponentUtils.getURL(request, pageManager,headerValueMap.get(HEADER_LOGOTEXLINKFIELD_NAME, String.class)));
			}
			if (null != headerConfigResource.getChild(HEADER_LINKS_RESOURCE_NAME)) {
				populateLinks(headerConfigurations, headerConfigResource);
			}
			if (null != currentPage) {
				populateSearchFlag();
			}
			if (null != homePage) {
				populateLanguageSelector(headerConfigResource);
			}
		}

	}

	private void populateLinks(HeaderBean headerBean, Resource headerConfigResource) {
		Resource linksResouce = headerConfigResource.getChild(HEADER_LINKS_RESOURCE_NAME);
		if (null != linksResouce) {
			List<HeaderLinkBean> headerLinks = new ArrayList<>();
			Iterator<Resource> links = linksResouce.listChildren();
			while (links.hasNext()) {
				Resource link = links.next();
				ValueMap linksMap = link.getValueMap();
				if (linksMap.containsKey(HEADER_LINKFIELD_NAME)) {
					HeaderLinkBean headerLinkBean = new HeaderLinkBean();
					headerLinkBean.setLinkText(linksMap.get(HEADER_LINKFIELD_NAME, String.class));
					headerLinkBean.setLinkUrl(ComponentUtils.getURL(request, pageManager, linksMap.get(HEADER_LINKURL_NAME, String.class)));
					headerLinkBean.setOpenInNewTab(linksMap.get(HEADER_OPENLINKIN_NAME, String.class));
					headerLinks.add(headerLinkBean);
				}

			}
			headerBean.setLinks(headerLinks);
		}

	}

	private void populateSearchFlag() {
		if (!PageMetaDataUtils.isHomePage(currentPage)) {
			headerConfigurations.setShowSearch(String.valueOf(Boolean.TRUE));
		}
	}

	private void populateLanguageSelector(final Resource headerConfigResource) {
		populateCurrentLanguageName();
		ValueMap headerValueMap = headerConfigResource.getValueMap();
		if (headerValueMap.containsKey(HEADER_LANG_DROPDOWN_PLACEHOLDER_FIELDNAME)) {
			headerConfigurations
					.setLangListInfoText(headerValueMap.get(HEADER_LANG_DROPDOWN_PLACEHOLDER_FIELDNAME, String.class));
		}
		populateAvailableLanguages(headerConfigurations, headerConfigResource);
	}

	private void populateCurrentLanguageName() {
		headerConfigurations.setCurrentLangName(homePage.getPageTitle());
	}

	private void populateAvailableLanguages(HeaderBean headerBean, Resource headerConfigResource) {
		Resource languageResource = headerConfigResource.getChild(HEADER_LANGUAGES_RESOURCE_NAME);
		if (null != languageResource) {
			List<HeaderLanguageBean> headerLanguages = new ArrayList<>();
			Iterator<Resource> links = languageResource.listChildren();
			while (links.hasNext()) {
				Resource link = links.next();
				ValueMap linksMap = link.getValueMap();
				if (linksMap.containsKey(HEADER_LANGFIELD_NAME)
						&& linksMap.containsKey(HEADER_LANG_HOMEPAGEURL_NAME)) {
					String langName = linksMap.get(HEADER_LANGFIELD_NAME, String.class);
					String langHomePagePath = linksMap.get(HEADER_LANG_HOMEPAGEURL_NAME, String.class);
					HeaderLanguageBean headerLanguageBean = new HeaderLanguageBean();
					if (StringUtils.equals(homePage.getPath(), langHomePagePath)) {
						// In case the home page title and the authored language display name are
						// different use the authored one
						// Skip current language getting added to the language selector list
						headerBean.setCurrentLangName(langName);
					} else {
						headerLanguageBean.setLangName(langName);
						headerLanguageBean.setLangHomePageUrl(langHomePagePath);
						populateLanguageSpecificLink(headerLanguageBean, langHomePagePath);
						headerLanguages.add(headerLanguageBean);
					}
				}

			}
			headerBean.setLanguages(headerLanguages);
		}

	}

	private void populateLanguageSpecificLink(HeaderLanguageBean headerLanguageBean, String langHomePagePath) {
		// Set to language specific home page by default
		headerLanguageBean.setOtherLangPageLink(langHomePagePath);
		String langSpecificPageRelativePath = StringUtils.replace(currentPage.getPath(), homePage.getPath(),
				langHomePagePath);
		if (StringUtils.isNotEmpty(langSpecificPageRelativePath)) {
			Resource langSpecificPageResourcelang = resourceResolver.getResource(langSpecificPageRelativePath);
			if (langSpecificPageResourcelang != null) {
				// Current page has translated page hence link to it
				headerLanguageBean.setOtherLangPageLink(langSpecificPageRelativePath);
			}
		}
	}

	private void processAlertConfigs(Resource headerConfigResource) {
		if (null != headerConfigResource && StringUtils.isNoneEmpty(headerConfigResource.getPath())
				&& StringUtils.contains(headerConfigResource.getPath(),
						Constants.FORWARD_SLASH + Constants.ENGLISH_HOMEPAGE_ISO_LANG + Constants.FORWARD_SLASH)) {
			// Current page is English page
			String alertNotificationExf = getEnglishAlertNotificationExf(headerConfigResource);
			if (StringUtils.isNotEmpty(alertNotificationExf)) {
				headerConfigurations.setAlertNotificationExfPath(alertNotificationExf);
			}
		} else if (null != headerConfigResource && StringUtils.isNoneEmpty(headerConfigResource.getPath())) {
			// Current page is not English hence refer alert/notification Exf from English
			// home page
			Resource englishHomepageHeaderResource = getEnglishHomePageHeaderResource(headerConfigResource);
			if (null != englishHomepageHeaderResource) {
				String englishAlertNotificationExf = getEnglishAlertNotificationExf(englishHomepageHeaderResource);
				if (StringUtils.isNotEmpty(englishAlertNotificationExf)) {
					// Generate other lang specific Exf path
					// Set english exf as default
					headerConfigurations.setAlertNotificationExfPath(englishAlertNotificationExf);
					String langSpecificExf = StringUtils.replace(englishAlertNotificationExf,
							Constants.FORWARD_SLASH + Constants.ENGLISH_HOMEPAGE_ISO_LANG + Constants.FORWARD_SLASH,
							Constants.FORWARD_SLASH + homePage.getLanguage().getLanguage() + Constants.FORWARD_SLASH);
					if (null != resourceResolver.getResource(langSpecificExf)) {
						// Set language specific exf if found
						headerConfigurations
								.setAlertNotificationExfPath(StringUtils.replace(englishAlertNotificationExf,
										Constants.FORWARD_SLASH + Constants.ENGLISH_HOMEPAGE_ISO_LANG
												+ Constants.FORWARD_SLASH,
										Constants.FORWARD_SLASH + homePage.getLanguage().getLanguage()
												+ Constants.FORWARD_SLASH));
					}
				}
			}
		}

	}

	/**
	 * @param headerConfigResource Header component resource
	 * @return alertNotificationExf Authored Exf
	 */
	private String getEnglishAlertNotificationExf(Resource headerConfigResource) {
		String alertNotificationExf = StringUtils.EMPTY;
		ValueMap headerValueMap = headerConfigResource.getValueMap();
		if (headerValueMap.containsKey(ALERT_NOTIFICATION_FLAG)
				&& headerValueMap.containsKey(ALERT_NOTIFICATION_EXFPATH)) {
			String alertFlag = headerValueMap.get(ALERT_NOTIFICATION_FLAG, String.class);
			if (null != alertFlag && StringUtils.isNotEmpty(alertFlag)
					&& StringUtils.equalsIgnoreCase(alertFlag, "true")) {
				alertNotificationExf = headerValueMap.get(ALERT_NOTIFICATION_EXFPATH, String.class);
				LOG.debug("Configured alert or notification Exf {}", alertNotificationExf);
			}
		}
		return alertNotificationExf;
	}

	/**
	 * @param otherLanheaderConfigResource Any language Header config apart from English
	 * @return englishHomepageHeaderResource English language Header config 
	 * Used only for Aert and notification as we need to refer the configs only from Englsih page
	 */
	private Resource getEnglishHomePageHeaderResource(Resource otherLanheaderConfigResource) {
		Resource englishHomepageHeaderResource = null;
		String[] tokens = StringUtils.splitByWholeSeparator(otherLanheaderConfigResource.getPath(),
				HEADER_RESOURCE_PATH);
		if (null != tokens) {
			String[] homepagePathTokens = StringUtils.split(tokens[0], Constants.FORWARD_SLASH);
			homepagePathTokens[homepagePathTokens.length - 1] = Constants.ENGLISH_HOMEPAGE_ISO_LANG;
			StringBuilder builder = new StringBuilder();
			builder.append(Constants.FORWARD_SLASH);
			for (String s : homepagePathTokens) {
				builder.append(s);
				builder.append(Constants.FORWARD_SLASH);
			}
			String englishHomePageHeaderResource = builder.append(HEADER_RESOURCE_PATH).toString();
			LOG.debug("else part english page {}", englishHomePageHeaderResource);
			englishHomepageHeaderResource = resourceResolver.getResource(englishHomePageHeaderResource);
		}
		return englishHomepageHeaderResource;
	}

	public HeaderBean getHeaderConfigurations() {
		return headerConfigurations;
	}
}
