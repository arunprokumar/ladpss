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
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = {Resource.class,SlingHttpServletRequest.class})
public class ButtonModel {
	private static final String PN_LINK = "link";
	
	@ScriptVariable
	SlingHttpServletRequest request;
	
	@ScriptVariable
	PageManager pageManager;
	
	@ValueMapValue(name = PN_LINK)
	@Optional
	private String link;

	@ValueMapValue(name = JcrConstants.JCR_TITLE)
	@Optional
	private String text;

	public String getLink() {
		return ComponentUtils.getURL(request, pageManager,link);
	}
	
	public String getText() {
		return text;
	}

}