package com.adobe.aem.lacounty.dpss.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.services.DpssGlobalService;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class HowToApplyCardModel {
	private static final Logger LOG = LoggerFactory.getLogger(HowToApplyCardModel.class);

	// Apply on line tab
	private static final String APPLYCARD_ONLINE_TITLE_FIELDNAME = "applyOnlineTitle";
	private static final String APPLYCARD_ONLINE_DESCRIPITON_FIELDNAME = "applyOnlineDescription";
	private static final String APPLYCARD_ONLINE_BUTTON_TEXT_FIELDNAME = "applyOnlineButtonText";
	private static final String APPLYCARD_ONLINE_BUTTON_LINK_FIELDNAME = "applyOnlineLink";
	private static final String APPLYCARD_ONLINE_BUTTON_LINKOPENIN_FIELDNAME = "applyOnlineOpenInNewTab";
	// Apply by phone tab
	private static final String APPLYCARD_PHONE_TITLE_FIELDNAME = "applyByPhoneTitle";
	private static final String APPLYCARD_PHONE_DESCRIPTION_FIELDNAME = "applyByPhoneDescription";
	private static final String APPLYCARD_PHONE_BUTTON_TEXT_FIELDNAME = "applyByPhoneButtonText";
	private static final String APPLYCARD_PHONE_BUTTON_LINK_FIELDNAME = "applyByPhoneLink";
	// Apply in person tab
	private static final String APPLYCARD_PERSON_TITLE_FIELDNAME = "applyInPersonTitle";
	private static final String APPLYCARD_PERSON_DESCRIPTION_FIELDNAME = "applyInPersonDescription";
	private static final String APPLYCARD_PERSON_FINDSRVS_FIELDNAME = "applyInPersonFindSrvsLabel";

	@Inject
	DpssGlobalService searchService;
	
	@ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "No resourceType")
	protected String resourceType;

	// Apply on line tab
	@ValueMapValue(name = APPLYCARD_ONLINE_TITLE_FIELDNAME)
	@Optional
	private String applyOnlineTitle;

	@ValueMapValue(name = APPLYCARD_ONLINE_DESCRIPITON_FIELDNAME)
	@Optional
	private String applyOnlineDescription;

	@ValueMapValue(name = APPLYCARD_ONLINE_BUTTON_TEXT_FIELDNAME)
	@Optional
	private String applyOnlineButtonText;

	@ValueMapValue(name = APPLYCARD_ONLINE_BUTTON_LINK_FIELDNAME)
	@Optional
	private String applyOnlineLink;

	@ValueMapValue(name = APPLYCARD_ONLINE_BUTTON_LINKOPENIN_FIELDNAME)
	@Optional
	private String applyOnlineOpenInNewTab;

	// Apply by phone tab
	@ValueMapValue(name = APPLYCARD_PHONE_TITLE_FIELDNAME)
	@Optional
	private String applyByPhoneTitle;

	@ValueMapValue(name = APPLYCARD_PHONE_DESCRIPTION_FIELDNAME)
	@Optional
	private String applyByPhoneDescription;

	@ValueMapValue(name = APPLYCARD_PHONE_BUTTON_TEXT_FIELDNAME)
	@Optional
	private String applyByPhoneButtonText;

	@ValueMapValue(name = APPLYCARD_PHONE_BUTTON_LINK_FIELDNAME)
	@Optional
	private String applyByPhoneLink;

	// Apply on line tab
	@ValueMapValue(name = APPLYCARD_PERSON_TITLE_FIELDNAME)
	@Optional
	private String applyInPersonTitle;

	@ValueMapValue(name = APPLYCARD_PERSON_DESCRIPTION_FIELDNAME)
	@Optional
	private String applyInPersonDescription;

	@ValueMapValue(name = APPLYCARD_PERSON_FINDSRVS_FIELDNAME)
	@Optional
	private String applyInPersonFindSrvsLabel;

	@ScriptVariable
	private PageManager pageManager;

	@Self
	private SlingHttpServletRequest request;
	
	String searchAPIEndPoint="";

	@PostConstruct
	protected void init() {
		LOG.trace("How to apply Card Model executing");
		if (StringUtils.isNotEmpty(applyByPhoneLink)) {
			applyByPhoneLink = ComponentUtils.getURL(request, pageManager, applyByPhoneLink);
		}
		if (StringUtils.isNotEmpty(applyOnlineLink)) {
			applyOnlineLink = ComponentUtils.getURL(request, pageManager, applyOnlineLink);
		}
		
		
		LOG.trace("How to apply Card Model sucessfully executated!");
	}

	public String getApplyOnlineTitle() {
		return applyOnlineTitle;
	}

	public String getApplyOnlineDescription() {
		return applyOnlineDescription;
	}

	public String getApplyOnlineButtonText() {
		return applyOnlineButtonText;
	}

	public String getApplyOnlineLink() {
		return applyOnlineLink;
	}

	public String getApplyOnlineOpenInNewTab() {
		return applyOnlineOpenInNewTab;
	}

	public String getApplyByPhoneTitle() {
		return applyByPhoneTitle;
	}

	public String getApplyByPhoneDescription() {
		return applyByPhoneDescription;
	}

	public String getApplyByPhoneButtonText() {
		return applyByPhoneButtonText;
	}

	public String getApplyByPhoneLink() {
		return applyByPhoneLink;
	}

	public String getApplyInPersonTitle() {
		return applyInPersonTitle;
	}

	public String getApplyInPersonDescription() {
		return applyInPersonDescription;
	}

	public String getApplyInPersonFindSrvsLabel() {
		return applyInPersonFindSrvsLabel;
	}
	
	public String getSearchAPIEndPoint() {
		return searchService.getSearchAPIEndPoint();
	}

}
