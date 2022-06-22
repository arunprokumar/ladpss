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

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;

@Model(adaptables = {Resource.class,SlingHttpServletRequest.class})
public class HomepageCalloutModel {
	private static final String PN_TOP_IMAGE = "topimage";
	private static final String PN_RTE_TEXT = "text";
	private static final String PN_ALT_TEXT = "imageAltTxt";
	private static final String  MOBILE_CARD_TRANSFORM_CONFIGNAME="mobileTransformedHCImage";
	private static final String TABLET_CARD_TRANSFORM_CONFIGNAME="tabTransformedHCImage";
	
	@ValueMapValue(name = PN_RTE_TEXT)
	@Optional
	private String text;

	@ValueMapValue(name = PN_TOP_IMAGE)
	@Optional
	private String topImagePath;
	
	@ValueMapValue(name = PN_ALT_TEXT)
	@Optional
	private String imageAltTxt;

	private String topMobileImage;

	private String topTabletImage;
	
	@PostConstruct
	protected void initModel() {
		if (StringUtils.isNotEmpty(topImagePath)) {
			topMobileImage=ComponentUtils.getTransformImgPath(topImagePath,MOBILE_CARD_TRANSFORM_CONFIGNAME);
			topTabletImage=ComponentUtils.getTransformImgPath(topImagePath, TABLET_CARD_TRANSFORM_CONFIGNAME);
		}
	}
	
	public String getImageAltTxt() {
		return imageAltTxt;
	}

	public String getTopImagePath() {
		return topImagePath;
	}

	public String getText() {
		return text;
	}

	public String getTopMobileImage() {
		return topMobileImage;
	}

	public String getTopTabletImage() {
		return topTabletImage;
	}

}