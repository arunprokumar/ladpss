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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = {Resource.class,SlingHttpServletRequest.class})
public class NewsfeedModel {
	private static final String PN_TITLE = "title";
	private static final String PN_ITEM_TARGET = "viewAllLink";
	private static final String PN_ITEM_TEXT = "viewAllLabel";
	private static final String PN_ITEM_ACTION = "linkAction";
	private static final String PN_LIST_PATH = "listPath";
	private static final String PN_BG_IMAGE = "bgImage";

	@Self
	private SlingHttpServletRequest request;

	@ScriptVariable
	private PageManager pageManager;

	@ValueMapValue(name = PN_LIST_PATH)
	@Optional
	private String listPath;
	
	@ValueMapValue(name = PN_TITLE)
	@Optional
	private String title;
	
	@ValueMapValue(name = PN_ITEM_ACTION)
	@Optional
	private String openInNewTab;
	
	@ValueMapValue(name = PN_ITEM_TEXT)
	@Optional
	private String viewAllLabel;

	@ValueMapValue(name = PN_ITEM_TARGET)
	@Optional
	private String viewAllLink;
	
	@ValueMapValue(name = PN_BG_IMAGE)
	@Optional
	private String bgImagePath = "";
	
	public String getBgImagePath() {
		return bgImagePath;
	}	
	
	public String getTitle() {
		return title;
	}

	public String getViewAllLabel() {
		return viewAllLabel;
	}

	public String getViewAllLink() {
		return ComponentUtils.getURL(request, pageManager, viewAllLink);
	}

	public String getOpenInNewTab() {
		return openInNewTab;
	}
	
	public String getListPath() {
		return listPath;
	}

}