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

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;

public class TagsListItem {

	protected Tag tag;
	protected SlingHttpServletRequest request;
	protected Page page;
	protected List<TagsListItem> childTags;
	

	public TagsListItem(@NotNull SlingHttpServletRequest request,@NotNull Tag tag) {
		this.request = request;
		this.tag = tag;
		PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
		if (pageManager != null) {
			this.page = pageManager.getContainingPage(request.getResource());
		}
		childTags = ComponentUtils.getDpssTags(request,tag.getPath());
	}
	public List<TagsListItem> getChildTags() {
		return childTags;
	}
	public String getTagPath() {
		return tag.getPath();
	}

	public String getTagTitle() {
		return tag.getTitle(ComponentUtils.getLocale(request,this.page));

	}

	public String getTagId() {
		return tag.getTagID();
	}
	
	public String getTagName() {
		return tag.getName();
	}
	
}
