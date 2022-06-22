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
package com.adobe.aem.lacounty.dpss.core.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.beans.EventsListItem;
import com.adobe.aem.lacounty.dpss.core.constants.Constants;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.Predicate;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.FulltextPredicateEvaluator;
import com.day.cq.search.eval.JcrBoolPropertyPredicateEvaluator;
import com.day.cq.search.eval.JcrPropertyPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.RangePropertyPredicateEvaluator;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = Servlet.class, property = { "sling.servlet.selectors=" + EventsListServlet.DEFAULT_SELECTOR,
		"sling.servlet.resourceTypes=cq/Page", "sling.servlet.extensions=json", "sling.servlet.methods=GET" })
public class EventsListServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 8935939993480361385L;
	protected static final String DEFAULT_SELECTOR = "eventslist";
	private static final String EVENT_DETAILS_RESOURCETYPE = "dpss/components/content/event-details";
	private static final String DPSS_NAMESPACE = "dpss:";
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	

	private static final Logger LOGGER = LoggerFactory.getLogger(EventsListServlet.class);
	private Map<String, String> map = new HashMap<>();
	@Reference
	transient QueryBuilder queryBuilder;

	@Override
	protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
			throws IOException {
		if (StringUtils.isNotBlank(request.getParameter(PathPredicateEvaluator.PATH))) {
			map.clear();
			List<EventsListItem> results = getMostRecentEvents(request.getResourceResolver(),
					request.getParameter(PathPredicateEvaluator.PATH), request);
			writeJson(results, response);
		}
	}

	private List<EventsListItem> getMostRecentEvents(ResourceResolver resourceResolver, String path,
			SlingHttpServletRequest request) {

		final List<EventsListItem> resources = new ArrayList<>();
		String limit = "4";
		map.put(PathPredicateEvaluator.PATH, path);
		map.put(JcrBoolPropertyPredicateEvaluator.BOOLPROPERTY,
				JcrConstants.JCR_CONTENT + Constants.FORWARD_SLASH + NameConstants.PN_HIDE_IN_NAV);
		map.put(JcrBoolPropertyPredicateEvaluator.BOOLPROPERTY + Constants.DOT + JcrPropertyPredicateEvaluator.VALUE,
				Constants.STRING_FALSE);
		map.put(JcrPropertyPredicateEvaluator.PROPERTY, JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
		map.put(JcrPropertyPredicateEvaluator.PROPERTY + Constants.DOT + JcrPropertyPredicateEvaluator.DEPTH, "2");
		map.put(JcrPropertyPredicateEvaluator.PROPERTY + Constants.DOT + JcrPropertyPredicateEvaluator.VALUE,
				EVENT_DETAILS_RESOURCETYPE);
		// Order By
		map.put(Predicate.ORDER_BY, "@" + Constants.START_DATE);
		map.put(Predicate.ORDER_BY + Constants.DOT + Predicate.PARAM_SORT, Predicate.SORT_ASCENDING);
		map.put(PredicateConverter.GROUP_PARAMETER_PREFIX + Constants.DOT +Predicate.PARAM_OFFSET, "0");
		// add only incase of search
		if (StringUtils.isNotBlank(request.getParameter("q"))) {
			map.put(FulltextPredicateEvaluator.FULLTEXT + Constants.DOT + FulltextPredicateEvaluator.REL_PATH,
					"@" + Constants.PN_EVENT_TITLE);
			map.put(FulltextPredicateEvaluator.FULLTEXT, request.getParameter("q").trim());
		}
		// for calendar only. By default set to 4 for community events
		if (StringUtils.isNotBlank(request.getParameter(Predicate.PARAM_LIMIT))) {
			limit = request.getParameter(Predicate.PARAM_LIMIT).trim();
		} else {
			// For Events Around the Community only
			TimeZone timezone = TimeZone.getTimeZone(Constants.TIMEZONE_PST);
			Calendar now = Calendar.getInstance(timezone);
			map.put(Constants.PREDICATE_DATERANGE + Constants.DOT + RangePropertyPredicateEvaluator.PROPERTY, "@" + Constants.START_DATE);
			map.put(Constants.PREDICATE_DATERANGE + Constants.DOT + RangePropertyPredicateEvaluator.LOWER_BOUND, dateFormat.format(now.getTime()));
		}
		
		map.put(PredicateConverter.GROUP_PARAMETER_PREFIX + Constants.DOT +Predicate.PARAM_LIMIT, limit);
		// enable filtering by tags/filters in calendar
		getTagsPredicate(request);	
		Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
		SearchResult result = query.getResult();
		List<Hit> hits = result.getHits();
		if (hits != null) {
			for (Hit hit : hits) {
				try {
					Resource hitRes = hit.getResource();
					Page page = getPage(hitRes);
					if (page != null) {
						resources.add(new EventsListItem(request, page, hitRes));
					}
				} catch (RepositoryException e) {
					LOGGER.error("Exception in getMostRecentEvents {}", e.getMessage(), e);
				} finally {
					map.clear();
				}
			}
		}
		return resources;
	}

	private void writeJson(List<EventsListItem> results, SlingHttpServletResponse response) {
		response.setContentType(Constants.CT_JSON);
		response.setCharacterEncoding(Constants.UTF8_ENCODING);
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getWriter(), results);
		} catch (IOException e) {
			LOGGER.error("Exception in writeJson {}", e.getMessage(), e);
		}
	}

	private Page getPage(Resource resource) {
		if (resource != null) {
			ResourceResolver resourceResolver = resource.getResourceResolver();
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			if (pageManager != null) {
				return pageManager.getContainingPage(resource);
			}
		}
		return null;
	}

	private void getTagsPredicate(SlingHttpServletRequest request) {
		if (StringUtils.isNotBlank(request.getParameter(Constants.PARAM_TAGS))) {
			map.put("tagid.property", TagConstants.PN_TAGS);
			String[] tags = request.getParameter(Constants.PARAM_TAGS).split(",");
			for (int i = 0; i < tags.length; i++) {
				map.put("tagid." + Integer.valueOf(i + 1) + "_value", DPSS_NAMESPACE + tags[i].trim());
			}
			map.put("tagid.or", Constants.STRING_TRUE);
		}
	}

}