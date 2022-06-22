/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.adobe.aem.lacounty.dpss.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.lacounty.dpss.core.beans.FooterSocialIconsBean;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = {Resource.class,SlingHttpServletRequest.class})
public class FeedbackModel {
	private static final String PN_YES_LINK_TEXT = "yesText";
	private static final String PN_NO_LINK_TEXT = "noText";
	private static final String PN_YES_MESSAGE = "yesMessage";
	private static final String PN_NO_MESSAGE = "noMessage";
	private static final String PN_TITLE_TEXT = "titleText";
	private static final String PN_BUTTON_TEXT = "ItemText";
	private static final String PN_BUTTON_TARGET = "ItemTarget";
	private static final String PN_BUTTON_ACTION = "ItemAction";
	private static final String PN_BUTTON_TYPE = "FontAwesomeKey";

	private static final String NODE_LINKS = "link";

	@ScriptVariable private PageManager pageManager;

	@Self private SlingHttpServletRequest request;

	@ScriptVariable private Resource resource;

	@ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
	protected ValueMap inheritedPageProperties;

	@ValueMapValue(name = PN_YES_LINK_TEXT)
	@Optional
	private String yesText;

	@ValueMapValue(name = PN_NO_LINK_TEXT)
	@Optional
	private String noText;

	@ValueMapValue(name = PN_YES_MESSAGE)
	@Optional
	private String yesMessage;

	@ValueMapValue(name = PN_NO_MESSAGE)
	@Optional
	private String noMessage;

	@ValueMapValue(name = PN_TITLE_TEXT)
	@Optional
	private String titleText;

	private List<FooterSocialIconsBean> yeslinks;
	
	public List<FooterSocialIconsBean> getYeslinks() {
		return new ArrayList<>(yeslinks);
	}

	private List<FooterSocialIconsBean> nolinks;

	public List<FooterSocialIconsBean> getNolinks() {
		return new ArrayList<>(nolinks);
	}

	public String getTitleText() {
		return titleText;
	}

	@PostConstruct
	protected void initModel() {
		yeslinks = new ArrayList<>();
		nolinks =new ArrayList<>();
		populateButtons("yes",yeslinks);
		populateButtons("no",nolinks);
	}

	public String getYesText() {
		return yesText;
	}

	public String getNoText() {
		return noText;
	}

	public String getYesMessage() {
		return yesMessage;
	}

	public String getNoMessage() {
		return noMessage;
	}

	private void populateButtons(String type,List<FooterSocialIconsBean> links) {
		Resource linkNode = resource.getChild(type+NODE_LINKS); 
		if(linkNode!=null) {
			for(Resource link: linkNode.getChildren()) {
				links.add(new FooterSocialIconsBean() {
					private ValueMap properties = link.getValueMap();
					private String accessbilityLabel = properties.get(type+PN_BUTTON_TEXT,String.class);
					private String socialLinkUrl = properties.get(type+PN_BUTTON_TARGET,String.class);
					private String openInNewTab = properties.get(type+PN_BUTTON_ACTION,String.class);
					private String fontAwesomeKey = properties.get(type+PN_BUTTON_TYPE,String.class);

					@Override
					public String getAccessbilityLabel() {
						return accessbilityLabel;
					}
					@Override
					public String getSocialLinkUrl() {
						return ComponentUtils.getURL(request, pageManager, socialLinkUrl);
					}
					@Override
					public String getOpenInNewTab() {
						return openInNewTab;
					}
					@Override
					public String getFontAwesomeKey() {
						return fontAwesomeKey;
					}

				});
			}
		}
	}
}