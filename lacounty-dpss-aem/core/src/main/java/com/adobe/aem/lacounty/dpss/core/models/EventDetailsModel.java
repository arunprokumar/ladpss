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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.wcm.api.Page;

/**
 * Defines the {@code EventDetails} Sling Model used for the {@code dpss/components/content/event-details} component.
 * 
 */
@Model(adaptables = {Resource.class,SlingHttpServletRequest.class})
public class EventDetailsModel {

	private static final String PN_TITLE = "eventTitle";
	private static final String PN_GMAP_LINK = "googleMapLink";
	private static final String PN_GMAP_LINK_LABEL = "googleMapLinkText";
	private static final String PN_EVENT_DESC = "text";
	private static final String PN_START_DATE = "startDate";
	private static final String PN_END_DATE = "endDate";
	SimpleDateFormat df = new SimpleDateFormat("EEE MMM d");
	
	@ScriptVariable
	private Page currentPage;
	
	@Self
	private SlingHttpServletRequest request;

	@ValueMapValue(name = PN_TITLE)
	@Optional
	private String title;

	@ValueMapValue(name = PN_GMAP_LINK)
	@Optional
	private String googleMapLink;

	@ValueMapValue(name = PN_GMAP_LINK_LABEL)
	@Optional
	private String googleMapLinkLabel;

	@ValueMapValue(name = PN_EVENT_DESC)
	@Optional
	private String description;

	@ValueMapValue(name = PN_START_DATE)
	@Optional
	private Calendar startDate;

	@ValueMapValue(name = PN_END_DATE)
	@Optional
	private Calendar endDate;
	
	public boolean isSameDayEvent() {
		if(getStartDate()!=null && getEndDate()!=null) {
			return df.format(getStartDate().getTime()).compareTo(df.format(getEndDate().getTime()))==0;
		}
		return false;
	}

	public String getTitle() {
		return title;
	}

	public String getGoogleMapLink() {
		return googleMapLink;
	}

	public String getGoogleMapLinkLabel() {
		return googleMapLinkLabel;
	}

	public String getDescription() {
		return description;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	@PostConstruct
	protected void initModel() {
		if (StringUtils.isBlank(title)) {
			title = StringUtils.defaultIfEmpty(currentPage.getPageTitle(),currentPage.getTitle());
		}
	}
	public String getBackButtonUrl() {
		return request.getHeader(HttpHeaders.REFERER);
	}

}