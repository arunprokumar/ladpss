package com.adobe.aem.lacounty.dpss.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.beans.HomePageBannerBean;
import com.adobe.aem.lacounty.dpss.core.beans.HomePageBannerButtonBean;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class HomePageBannerModel {
	private static final Logger LOG = LoggerFactory.getLogger(HomePageBannerModel.class);

	private static final String BANNER_BUTTONS_FIELD_NAME = "buttons";
	private static final String BANNER_TITLE_FIELDNAME = "bannerTitle";
	private static final String BANNER_SERACH_PLACEHOLDERTEXT_FIELDNAME = "searchPlaceholderText";
	private static final String BANNER_BUTTON_TEXT_FIELDNAME = "text";
	private static final String BANNER_BUTTON_LINK_FIELDNAME = "link";
	private static final String BANNER_BUTTON_OPENINFLAG_FIELDNAME = "openInNewTab";
	private static final String BANNER_BUTTON_FONTAWOESOMEKEY_FIELDNAME = "fontAwesomeKey";
	private static final String BANNER_YBNBUTTON_TEXT_FIELDNAME = "ybnText";
	private static final String BANNER_YBNBUTTON_LINK_FIELDNAME = "ybnLink";
	private static final String BANNER_YBNBUTTON_OPENINFLAG_FIELDNAME = "ybnButtonOpenInNewTab";
	private static final String BANNER_YBNBUTTON_IMAGE_FILEDNAME = "ybnButtonImage";
	private static final String BANNER_YBNBUTTON_IMAGE_ALTTEXT_FIELDNAME = "ybnButtonImageAltTxt";
	private static final String PN_BG_IMAGE = "bgImage";
	
	@ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "No resourceType")
	protected String resourceType;

	@ChildResource(name = BANNER_BUTTONS_FIELD_NAME)
	@Optional
	private Resource buttons;

	@ValueMapValue(name = BANNER_TITLE_FIELDNAME)
	@Optional
	private String bannerTitle;

	@ValueMapValue(name = BANNER_SERACH_PLACEHOLDERTEXT_FIELDNAME)
	@Optional
	private String searchPlaceholderText;

	@ValueMapValue(name = BANNER_YBNBUTTON_TEXT_FIELDNAME)
	@Optional
	private String ybnText;

	@ValueMapValue(name = BANNER_YBNBUTTON_LINK_FIELDNAME)
	@Optional
	private String ybnLink;

	@ValueMapValue(name = BANNER_YBNBUTTON_OPENINFLAG_FIELDNAME)
	@Optional
	private String ybnButtonOpenInNewTab;

	@ValueMapValue(name = BANNER_YBNBUTTON_IMAGE_FILEDNAME)
	@Optional
	private String ybnButtonImage;

	@ValueMapValue(name = BANNER_YBNBUTTON_IMAGE_ALTTEXT_FIELDNAME)
	@Optional
	private String ybnButtonImageAltTxt;

	@ScriptVariable
	private PageManager pageManager;
	
	@ScriptVariable
	private Resource resource;

	@Self
	private SlingHttpServletRequest request;
	
	@ValueMapValue(name = PN_BG_IMAGE)
	@Optional
	private String bgImagePath = "";
	
	public String getBgImagePath() {
		return bgImagePath;
	}

	HomePageBannerBean homePageBannerBean;

	@PostConstruct
	protected void init() {
		LOG.trace("Home page banner Model executing");
		homePageBannerBean = new HomePageBannerBean();
		homePageBannerBean.setTitle(bannerTitle);
		if (StringUtils.isNotBlank(searchPlaceholderText)) {
			homePageBannerBean.setSeachPlaceHolder(searchPlaceholderText);
		} else {
			homePageBannerBean.setSeachPlaceHolder(getSearchPlaceHolderText());
		}

		if (null != buttons) {
			processConfiguredButtons();
		}
		if (StringUtils.isNotEmpty(ybnText) || StringUtils.isNotEmpty(ybnButtonImage)) {
			processConfiguredYbnButton();
		}
		LOG.trace("Home page banner Model sucessfully executated!");
	}

	private void processConfiguredButtons() {
		Iterator<Resource> configuredButtons = buttons.listChildren();
		List<HomePageBannerButtonBean> homePageBannerButtonBeanList = new ArrayList<>();
		while (configuredButtons.hasNext()) {
			Resource buttonResource = configuredButtons.next();
			if (null != buttonResource) {
				ValueMap valueMap = buttonResource.getValueMap();
				HomePageBannerButtonBean homePageBannerButtonBean = new HomePageBannerButtonBean();
				populateButtonConfigs(valueMap, homePageBannerButtonBean);
				homePageBannerButtonBeanList.add(homePageBannerButtonBean);
			}
		}
		homePageBannerBean.setBannerButtons(homePageBannerButtonBeanList);
	}

	private void populateButtonConfigs(ValueMap valueMap, HomePageBannerButtonBean homePageBannerButtonBean) {
		if (null != valueMap && valueMap.containsKey(BANNER_BUTTON_TEXT_FIELDNAME)) {
			homePageBannerButtonBean.setLabel(valueMap.get(BANNER_BUTTON_TEXT_FIELDNAME, String.class));
		}
		if (null != valueMap && valueMap.containsKey(BANNER_BUTTON_LINK_FIELDNAME) && null != pageManager
				&& null != request) {
			homePageBannerButtonBean.setInkUrl(ComponentUtils.getURL(request, pageManager,
					valueMap.get(BANNER_BUTTON_LINK_FIELDNAME, String.class)));
		}
		if (null != valueMap && valueMap.containsKey(BANNER_BUTTON_OPENINFLAG_FIELDNAME)) {
			homePageBannerButtonBean.setOpenInNewTab(valueMap.get(BANNER_BUTTON_OPENINFLAG_FIELDNAME, String.class));
		}
		if (null != valueMap && valueMap.containsKey(BANNER_BUTTON_FONTAWOESOMEKEY_FIELDNAME)) {
			homePageBannerButtonBean
					.setFontAwesomeKey(valueMap.get(BANNER_BUTTON_FONTAWOESOMEKEY_FIELDNAME, String.class));
		}
	}

	private void processConfiguredYbnButton() {
		List<HomePageBannerButtonBean> existingButtons = homePageBannerBean.getBannerButtons();
		if (null != existingButtons && !existingButtons.isEmpty()) {
			existingButtons.add(getYbnButtonConfigs());
		} else {
			HomePageBannerButtonBean homePageBannerButtonBean = getYbnButtonConfigs();
			List<HomePageBannerButtonBean> homePageBannerButtonBeanList = new ArrayList<>();
			homePageBannerButtonBeanList.add(homePageBannerButtonBean);
			homePageBannerBean.setBannerButtons(homePageBannerButtonBeanList);
		}

	}

	private HomePageBannerButtonBean getYbnButtonConfigs() {
		HomePageBannerButtonBean homePageBannerButtonBean = new HomePageBannerButtonBean();
		if (StringUtils.isNotEmpty(ybnText) || StringUtils.isNotEmpty(ybnButtonImage)) {
			homePageBannerButtonBean.setIsYbnButton(String.valueOf(Boolean.TRUE));
			homePageBannerButtonBean.setLabel(ybnText);
			homePageBannerButtonBean.setYbnImage(ybnButtonImage);
			homePageBannerButtonBean.setYbnImageAltText(ybnButtonImageAltTxt);
			homePageBannerButtonBean.setOpenInNewTab(ybnButtonOpenInNewTab);
			if (StringUtils.isNoneEmpty(ybnLink) && null != pageManager && null != request) {
				homePageBannerButtonBean.setInkUrl(ComponentUtils.getURL(request, pageManager, ybnLink));
			}
		}
		return homePageBannerButtonBean;
	}

	private String getSearchPlaceHolderText() {
		String fetchedPlaceHolderText = searchPlaceholderText;
		if (null != resource && StringUtils.contains(resource.getPath(), "/searchbar")) {
			Resource homePageBanner = resource.getParent();
			if (null != homePageBanner && StringUtils.contains(homePageBanner.getPath(), "/homepage_banner")
					&& !homePageBanner.getValueMap().isEmpty()
					&& homePageBanner.getValueMap().containsKey(BANNER_SERACH_PLACEHOLDERTEXT_FIELDNAME)) {
				LOG.debug("Fetched the search place holder text from parent Home Page Banner component {} ",
						homePageBanner.getPath());
				fetchedPlaceHolderText = homePageBanner.getValueMap().get(BANNER_SERACH_PLACEHOLDERTEXT_FIELDNAME,
						String.class);
			}
		}
		return fetchedPlaceHolderText;
	}

	public HomePageBannerBean getHomePageBannerBean() {
		return homePageBannerBean;
	}

}
