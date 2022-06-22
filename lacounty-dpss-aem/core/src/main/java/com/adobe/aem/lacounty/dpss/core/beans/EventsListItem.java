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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.adobe.aem.lacounty.dpss.core.constants.Constants;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.Page;
import com.drew.lang.annotations.NotNull;

public class EventsListItem {

	protected SlingHttpServletRequest request;
	protected Page page;
	protected Resource resource;
	String title;
	SimpleDateFormat df = new SimpleDateFormat("EEE MMM d");
	SimpleDateFormat tf = new SimpleDateFormat("h:mm a");
	TimeZone timezone = TimeZone.getTimeZone(Constants.TIMEZONE_PST);
	SimpleDateFormat isoFormat = new SimpleDateFormat(Constants.ISO8601_DATE);
	
	
	
	public EventsListItem(@NotNull SlingHttpServletRequest request, @NotNull Page page, @NotNull Resource res) {
		this.request = request;
		this.page = page;
		this.resource = res;
		Page redirectTarget = ComponentUtils.getRedirectTarget(page);
		if (redirectTarget != null && !redirectTarget.equals(page)) {
			this.page = redirectTarget;
		}
	}

	public String getUrl() {
		return ComponentUtils.getURL(request, page.getPageManager(), page.getPath());
	}

	public String getTitle() {
		if (StringUtils.isBlank(title)) {
			title = StringUtils.defaultIfEmpty(resource.getValueMap().get(Constants.PN_EVENT_TITLE, String.class),page.getPageTitle());
		}
		if (title == null) {
			title = page.getTitle();
		}
		return title;
	}

	public String getStartDate() {
		Calendar cal = resource.getValueMap().get(Constants.START_DATE, Calendar.class);
		if(cal!=null && cal.getTime()!=null) {
			df.setTimeZone(timezone);
			return df.format(cal.getTimeInMillis());
		}
		return null;
	}

	public String getEndDate() {
		Calendar cal = resource.getValueMap().get(Constants.END_DATE, Calendar.class);
		if(cal!=null && cal.getTime()!=null) {
			df.setTimeZone(timezone);
			return df.format(cal.getTimeInMillis());
		}
		return null;
	}

	public String getStartTime() {
		Calendar cal = resource.getValueMap().get(Constants.START_DATE, Calendar.class);
		if(cal!=null && cal.getTime()!=null) {
			tf.setTimeZone(timezone);
			return tf.format(cal.getTimeInMillis());
		}
		return null;
	}

	public String getEnd() {
		Calendar cal = resource.getValueMap().get(Constants.END_DATE, Calendar.class);
		if(cal!=null && cal.getTime()!=null) {
			isoFormat.setTimeZone(timezone);
			return isoFormat.format(cal.getTimeInMillis());
		}
		return null;
	}
	
	public String getStart() {
		Calendar cal = resource.getValueMap().get(Constants.START_DATE, Calendar.class);
		if(cal!=null && cal.getTime()!=null) {
			isoFormat.setTimeZone(timezone);
			return isoFormat.format(cal.getTimeInMillis());
		}
		return null;
	}

	public String getEndTime() {
		Calendar cal = resource.getValueMap().get(Constants.END_DATE, Calendar.class);
		if(cal!=null && cal.getTime()!=null) {
			tf.setTimeZone(timezone);
			return tf.format(cal.getTimeInMillis());
		}
		return null;
	}
	
	public String getgMapLinkLabel() {
		return  StringUtils.defaultIfEmpty(resource.getValueMap().get("googleMapLinkText", String.class),page.getPageTitle());
	}

	public String getGmapLink() {
		return resource.getValueMap().get("googleMapLink", String.class);
	}

	public List<String> getEventCategories() {
		return new ArrayList<>(ComponentUtils.getTagTitles(request,ComponentUtils.getTagValuesAsList(TagConstants.PN_TAGS,resource)));
	}

}
