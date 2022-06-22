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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.lacounty.dpss.core.beans.HeaderLinkBean;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = {Resource.class,SlingHttpServletRequest.class})
public class ContentPageBannerModel {
	private static final String PN_BG_IMAGE = "globalBgImage";
	private static final String PN_ITEM_TEXT = "itemText";
	private static final String PN_ITEM_TARGET = "itemTarget";
	private static final String PN_ITEM_ACTION = "itemAction";
	private static final String NODE_LINKS = "link";

	@Self
	private SlingHttpServletRequest request;

	@ScriptVariable
	private Resource resource;

	@ScriptVariable
	private PageManager pageManager;

	@ScriptVariable
	private Page currentPage;
	
	@ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
	protected ValueMap inheritedPageProperties;

	@ValueMapValue(name = JcrConstants.JCR_TITLE)
	@Optional
	private String title;

	@ValueMapValue(name = "subText")
	@Optional
	private String subText;

	@ValueMapValue(name = "linkURL")
	@Optional
	private String linkURL;

	private String bgImagePath = "";

	private List<HeaderLinkBean> links;

	private String description;
	
	@ValueMapValue(name = "hideDesc")
	@Optional
	private boolean hideDescription;

	public boolean isHideDescription() {
		return hideDescription;
	}

	@PostConstruct
	protected void initModel() {
		if (StringUtils.isBlank(title)) {
			title = StringUtils.defaultIfEmpty(currentPage.getPageTitle(),currentPage.getPageTitle());
		}

		if (StringUtils.isNotEmpty(linkURL)) {
			linkURL = ComponentUtils.getURL(request, pageManager, linkURL);
		} else {
			linkURL = null;
		}
		if(inheritedPageProperties!=null) {
			bgImagePath = inheritedPageProperties.get(PN_BG_IMAGE,String.class);
		}
		if(currentPage.getDescription() != null) {
			description =  currentPage.getDescription();
		}
		populateLinks();
	}

	public String getText() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public String getBgImagePath() {
		return bgImagePath;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public List<HeaderLinkBean> getLinks() {
		return new ArrayList<>(links);
	}

	public String getSubText() {
		return subText;
	}

	private void populateLinks() {
		links = new ArrayList<>();
		Resource linkNode = resource.getChild(NODE_LINKS); 
		if(linkNode!=null) {
			for(Resource link: linkNode.getChildren()) {
				links.add(new HeaderLinkBean() {
					private ValueMap properties = link.getValueMap();
					private String linkText = properties.get(PN_ITEM_TEXT,String.class);
					private String linkUrl = properties.get(PN_ITEM_TARGET,String.class);
					private String openInNewTab = properties.get(PN_ITEM_ACTION,String.class);

					@Override
					public String getLinkText() {
						return linkText;
					}
					@Override
					public String getLinkUrl() {
						return ComponentUtils.getURL(request, pageManager, linkUrl);
					}
					@Override
					public String getOpenInNewTab() {
						return openInNewTab;
					}

				});
			}
		}
	}

}