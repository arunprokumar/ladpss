/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.aem.lacounty.dpss.core.beans;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;

public class NewsListItem {

	private static final String HNF_MOBILE_TRANSFORM_CONFIGNAME = "mobileTransformedHNFImage";
	private static final String NL_MOBILE_TRANSFORM_CONFIGNAME = "mobileTransformedNLImage";
	private static final String NL_TAB_TRANSFORM_CONFIGNAME = "tabTransformedNLImage";
	private static final String PN_THUMBNAIL_IMAGE = "fileReference";
	private static final String NODE_IMAGE = "image";
	private static final String PN_NEWS_BLURB = "newsBlurb";
	
	protected SlingHttpServletRequest request;
	protected Page page;
	String title;

	public String getThumbnailVariation() {
		return ComponentUtils.getTransformImgPath(getThumbnailUrl(),HNF_MOBILE_TRANSFORM_CONFIGNAME);
	}
	public String getNewsListMobileImage() {
		return ComponentUtils.getTransformImgPath(getThumbnailUrl(),NL_MOBILE_TRANSFORM_CONFIGNAME);
	}
	public String getNewsListTabImage() {
		return ComponentUtils.getTransformImgPath(getThumbnailUrl(),NL_TAB_TRANSFORM_CONFIGNAME);
	}
	
	public String getThumbnailUrl() {
		return page.getContentResource(NODE_IMAGE).getValueMap().get(PN_THUMBNAIL_IMAGE,String.class);
	}

	public NewsListItem(@NotNull SlingHttpServletRequest request, @NotNull Page page) {
		this.request = request;
		this.page = page;
		Page redirectTarget = getRedirectTarget(page);
		if (redirectTarget != null && !redirectTarget.equals(page)) {
			this.page = redirectTarget;
		}
	}

	public String getLink() {
		return ComponentUtils.getURL(request, page.getPageManager(), page.getPath());
	}

	public String getTitle() {
		if (StringUtils.isBlank(title)) {
			title = StringUtils.defaultIfEmpty(page.getPageTitle(),page.getTitle());
		}
		return title;
	}

	public String getDescription() {
        return StringUtils.defaultIfEmpty(page.getContentResource().getValueMap().get(PN_NEWS_BLURB, String.class),page.getDescription());
	}

	private Page getRedirectTarget(@NotNull Page page) {
		Page result = page;
		String redirectTarget;
		PageManager pageManager = page.getPageManager();
		Set<String> redirectCandidates = new LinkedHashSet<>();
		redirectCandidates.add(page.getPath());
		while (result != null && StringUtils.isNotEmpty(redirectTarget = result.getProperties().get("cq:redirectTarget", String.class))) {
			result = pageManager.getPage(redirectTarget);
			if (result != null && !redirectCandidates.add(result.getPath())) {
				break;
			}
		}
		return result;
	}

}
