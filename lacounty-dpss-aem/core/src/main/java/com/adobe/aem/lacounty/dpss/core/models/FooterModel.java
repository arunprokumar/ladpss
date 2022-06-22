package com.adobe.aem.lacounty.dpss.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
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

import com.adobe.aem.lacounty.dpss.core.beans.FooterBean;
import com.adobe.aem.lacounty.dpss.core.beans.FooterLinkBean;
import com.adobe.aem.lacounty.dpss.core.beans.FooterSocialIconsBean;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.adobe.aem.lacounty.dpss.core.utils.PageMetaDataUtils;
import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.social.community.api.CommunityConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class FooterModel {
	private static final Logger LOG = LoggerFactory.getLogger(FooterModel.class);
	private static final String FOOTER_RESOURCE_PATH = "jcr:content/root/footer";
	private static final String FOOTER_LOGO_FIELD_NAME = "logo";
	private static final String FOOTER_LOGOALTTXT_FIELD_NAME = "logoAltTxt";
	private static final String FOOTER_LOGOALINK_FIELD_NAME = "logoLink";
	private static final String FOOTER_LINKS_RESOURCE_NAME = "links";
	private static final String FOOTER_LINKFIELD_NAME = "text";
	private static final String FOOTER_LINKURL_NAME = "link";
	private static final String FOOTER_SOCIALLINKS_NAME = "socialLinks";
	private static final String FOOTER_CF_FIELDNAME = "fragmentPath";
	private static final String FOOTER_FONTAWESOMEKEY_FIELD_NAME = "fontAwesomeKey";
	private static final String FOOTER_SOCIALLINK_FIELD_NAME = "socialLinkUrl";
	private static final String FOOTER_OPENLINKIN_NAME = "openInNewTab";
	private static final String FOOTER_COPYRIGHT_YEAR_PLACEHOLDER = "{year}";

	@ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "No resourceType")
	protected String resourceType;

	@Self
	private SlingHttpServletRequest request; 
	
	@Inject
	private Page resourcePage;

	@SlingObject
	private ResourceResolver resourceResolver;

	@ScriptVariable
	PageManager pageManager;
	
	FooterBean footerConfigurations;

	@PostConstruct
	protected void init() {
		LOG.info("Footer Model executing");
		if (resourcePage == null) {
			return;
		}
		Resource footerConfigResource = getFooterConfigsResource();
		if (null == footerConfigResource) {
			LOG.info("No Footer configuration found!");
			return;
		}
		processFooterConfigs(footerConfigResource);
		LOG.info("Footer Model sucessfully executated!");
	}

	/**
	 * Process the authored content for footer
	 * 
	 * @return
	 */
	private Resource getFooterConfigsResource() {
		Resource footerConfigsResource = null;
		int currentPageDepth = resourcePage.getDepth();
		for (int index = currentPageDepth; index > 1; index--) {
			Page parentPage = resourcePage.getAbsoluteParent(index);
			if (null != parentPage) {
				Resource parentPageResource = parentPage.adaptTo(Resource.class);
				if (null != parentPageResource) {
					footerConfigsResource = parentPageResource.getChild(FOOTER_RESOURCE_PATH);
					if (null != footerConfigsResource && PageMetaDataUtils.isHomePage(parentPage)) {
						String footerConfigsPath = footerConfigsResource.getPath();
						LOG.info("Footer cofiguraions path {}", footerConfigsPath);
						return footerConfigsResource;
					}
				}

			}
		}
		return footerConfigsResource;
	}

	/**
	 * Populates the authored footer content
	 * 
	 * @param footerConfigResource
	 */
	private void processFooterConfigs(Resource footerConfigResource) {
		ValueMap footerValueMap = footerConfigResource.getValueMap();
		if (!footerValueMap.isEmpty()) {
			footerConfigurations = new FooterBean();
			if (footerValueMap.containsKey(FOOTER_LOGO_FIELD_NAME)) {
				footerConfigurations.setLogo(footerValueMap.get(FOOTER_LOGO_FIELD_NAME, String.class));
			}
			if (footerValueMap.containsKey(FOOTER_LOGOALTTXT_FIELD_NAME)) {
				footerConfigurations.setLogoAltTxt(footerValueMap.get(FOOTER_LOGOALTTXT_FIELD_NAME, "DPSS Logo"));
			}
			if (footerValueMap.containsKey(FOOTER_LOGOALINK_FIELD_NAME)) {
				footerConfigurations.setLogoLink(footerValueMap.get(FOOTER_LOGOALINK_FIELD_NAME, String.class));
			}
			if (null != resourceResolver && footerValueMap.containsKey(FOOTER_CF_FIELDNAME)) {
				populateCopyRightText(footerValueMap.get(FOOTER_CF_FIELDNAME, String.class));
			}
			if (null != footerConfigResource.getChild(FOOTER_LINKS_RESOURCE_NAME)) {
				populateLinks(footerConfigurations, footerConfigResource);
			}
			if (null != footerConfigResource.getChild(FOOTER_SOCIALLINKS_NAME)) {
				populateSocialIcons(footerConfigurations, footerConfigResource);
			}

		}

	}

	/**
	 * populates the copyright text from authored Content fragment
	 * 
	 * @param contnetfragmentPath
	 */
	private void populateCopyRightText(String contentfragmentPath) {
		LOG.info("processing content fragment");
		Resource footerCfResource = resourceResolver.getResource(contentfragmentPath);
		if (null == footerCfResource) {
			return;
		}
		ContentFragment copyrightCF = footerCfResource.adaptTo(ContentFragment.class);
		if (null == copyrightCF) {
			return;
		}
		Iterator<ContentElement> elements = copyrightCF.getElements();
		while (elements.hasNext()) {
			ContentElement content = elements.next();
			if (content != null && content.getValue() != null && content.getValue().getValue() != null) {
				String authoredContent = content.getValue().getValue().toString();
				if (StringUtils.contains(authoredContent, FOOTER_COPYRIGHT_YEAR_PLACEHOLDER)) {
					authoredContent = StringUtils.replace(authoredContent, FOOTER_COPYRIGHT_YEAR_PLACEHOLDER,
							String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
				}
				footerConfigurations.setCopyRightTxt(authoredContent);
			}

		}
	}

	/**
	 * Populates the authored links
	 * 
	 * @param footerBean
	 * @param footerConfigResource
	 */
	private void populateLinks(FooterBean footerBean, Resource footerConfigResource) {
		Resource linksResouce = footerConfigResource.getChild(FOOTER_LINKS_RESOURCE_NAME);
		if (null != linksResouce) {
			List<FooterLinkBean> footerLinks = new ArrayList<>();
			Iterator<Resource> links = linksResouce.listChildren();
			while (links.hasNext()) {
				Resource link = links.next();
				ValueMap linksMap = link.getValueMap();
				if (!linksMap.isEmpty() && linksMap.containsKey(FOOTER_LINKFIELD_NAME)) {
					FooterLinkBean footerLinkBean = new FooterLinkBean();
					footerLinkBean.setLinkText(linksMap.get(FOOTER_LINKFIELD_NAME, String.class));
					footerLinkBean.setLinkUrl(ComponentUtils.getURL(request, pageManager, linksMap.get(FOOTER_LINKURL_NAME, String.class)));
					footerLinkBean.setOpenInNewTab(linksMap.get(FOOTER_OPENLINKIN_NAME, String.class));
					footerLinks.add(footerLinkBean);
				}

			}
			footerBean.setLinks(footerLinks);
		}

	}

	/**
	 * Populates the authored social icons
	 * 
	 * @param footerBean
	 * @param footerConfigResource
	 */
	private void populateSocialIcons(FooterBean footerBean, Resource footerConfigResource) {
		Resource linksResouce = footerConfigResource.getChild(FOOTER_SOCIALLINKS_NAME);
		if (null != linksResouce) {
			List<FooterSocialIconsBean> footerSocialIcons = new ArrayList<>();
			Iterator<Resource> links = linksResouce.listChildren();
			while (links.hasNext()) {
				Resource link = links.next();
				ValueMap linksMap = link.getValueMap();
				if (!linksMap.isEmpty() && linksMap.containsKey(FOOTER_FONTAWESOMEKEY_FIELD_NAME)) {
					FooterSocialIconsBean footerSocialIconsBean = new FooterSocialIconsBean();
					footerSocialIconsBean
							.setFontAwesomeKey(linksMap.get(FOOTER_FONTAWESOMEKEY_FIELD_NAME, String.class));
					String socialUrl = linksMap.get(FOOTER_SOCIALLINK_FIELD_NAME, String.class); 
					if(socialUrl.startsWith(CommunityConstants.CONTENT_ROOT_PATH)) {
						footerSocialIconsBean.setSocialLinkUrl(socialUrl + com.day.cq.wcm.foundation.List.URL_EXTENSION); 
					} else {
						footerSocialIconsBean.setSocialLinkUrl(socialUrl);
					}
					footerSocialIconsBean.setOpenInNewTab(linksMap.get(FOOTER_OPENLINKIN_NAME, String.class));
					footerSocialIconsBean
							.setAccessbilityLabel(createAccessbilityLabel(footerSocialIconsBean.getFontAwesomeKey()));
					footerSocialIcons.add(footerSocialIconsBean);
				}

			}
			footerBean.setSocialIcons(footerSocialIcons);
		}
	}

	/**
	 * Creates the Accessibility text from Fontawesome key
	 * 
	 * @param fontAwesomeKey
	 * @return accessbiliytText
	 */
	private String createAccessbilityLabel(String fontAwesomeKey) {
		String accessbiliytText = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(fontAwesomeKey)) {
			String[] tokens = StringUtils.split(fontAwesomeKey, "-");
			if (tokens != null && tokens.length > 0) {
				accessbiliytText = tokens[tokens.length - 1];
			}
		}
		return accessbiliytText;

	}

	public FooterBean getFooterConfigurations() {
		return footerConfigurations;
	}

}
