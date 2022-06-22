
package com.adobe.aem.lacounty.dpss.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.lacounty.dpss.core.beans.TagsListItem;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class CalendarModel {

	private static final String PN_LIST_PATH = "listPath";
	private static final String PN_EVENT_CATEGORIES_PATH = "eventCategories";
	
	@Self
	private SlingHttpServletRequest request;
	
	@ValueMapValue(name = PN_LIST_PATH)
	@Optional
	private String listPath;
	
	@ValueMapValue(name = PN_EVENT_CATEGORIES_PATH)
	@Optional
	private String eventCategoriesPath;
	
	List<TagsListItem> tagsList = new ArrayList<>();

	@PostConstruct
	protected void initModel() {
		if(eventCategoriesPath!=null) {
			tagsList = ComponentUtils.getDpssTags(request,eventCategoriesPath);
		}

	}
	
	public String getListPath() {
		return listPath;
	}

	public String getEventCategoriesPath() {
		return eventCategoriesPath;
	}

	public List<TagsListItem> getTagsList() {
		return new ArrayList<>(tagsList);
	}

}