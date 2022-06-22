package com.adobe.aem.lacounty.dpss.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.beans.NewsListItem;
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
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.drew.lang.annotations.NotNull;

@Component(service = Servlet.class, property = { "sling.servlet.selectors=" + NewsListServlet.DEFAULT_SELECTOR,
		"sling.servlet.resourceTypes=cq/Page", "sling.servlet.extensions=json", "sling.servlet.methods=GET" })
public class NewsListServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 8935939993480361385L;
	protected static final String DEFAULT_SELECTOR = "newslist";
	private static final String COMP_PATH = "dpss/components/structure/news-detail-page";
	private static final String JSON_KEY_TOTAL_RESULTS = "totalResults";
	private static final String JSON_KEY_PAGES = "pages";
	private static final String JSON_KEY_HAS_MORE = "hasMore";
	

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsListServlet.class);

	@Reference
	transient QueryBuilder queryBuilder;

	transient JSONObject resultsJson;

	@Override
	protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
			throws IOException {
		if (StringUtils.isNotBlank(request.getParameter(PathPredicateEvaluator.PATH))) {
			List<NewsListItem> results = getMostRecentlyPublishedNews(request.getResourceResolver(),
					request.getParameter(PathPredicateEvaluator.PATH), request);
			writeJson(results, response);
		}
	}

	private List<NewsListItem> getMostRecentlyPublishedNews(ResourceResolver resourceResolver, String path,
			SlingHttpServletRequest request) {

		final Map<String, String> map = new HashMap<>();
		final List<NewsListItem> resources = new ArrayList<>();
		String limit = "10";
		String offset = "0";

		if (StringUtils.isNotBlank(request.getParameter(Predicate.PARAM_LIMIT))) {
			limit = request.getParameter(Predicate.PARAM_LIMIT);
		}
		if (StringUtils.isNotBlank(request.getParameter(Predicate.PARAM_OFFSET))) {
			offset = request.getParameter(Predicate.PARAM_OFFSET);
		}
		map.put(PathPredicateEvaluator.PATH, path);
		map.put(TypePredicateEvaluator.TYPE, NameConstants.NT_PAGE);
		map.put(JcrBoolPropertyPredicateEvaluator.BOOLPROPERTY,
				JcrConstants.JCR_CONTENT + Constants.FORWARD_SLASH + NameConstants.PN_HIDE_IN_NAV);
		map.put(JcrBoolPropertyPredicateEvaluator.BOOLPROPERTY + Constants.DOT + JcrPropertyPredicateEvaluator.VALUE,
				Constants.STRING_FALSE);
		map.put(JcrPropertyPredicateEvaluator.PROPERTY,
				JcrConstants.JCR_CONTENT + Constants.FORWARD_SLASH + JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
		map.put(JcrPropertyPredicateEvaluator.PROPERTY + Constants.DOT + JcrPropertyPredicateEvaluator.VALUE,
				COMP_PATH);
		// add only incase of search
		if (StringUtils.isNotBlank(request.getParameter("q"))) {
			StringBuilder keywordsWithOrOperator = new StringBuilder();
			String searchKeywords = request.getParameter("q").trim();
			String[] fulltextArray = searchKeywords.split(" "); 
			List<String> fulltextList = Arrays.asList(fulltextArray);
			 
			final String OR_OPERATOR = " OR ";
			for(String keyword : fulltextList) {
				if (! (keyword == null || keyword.length() < 3)) {
					keywordsWithOrOperator.append(keyword).append(OR_OPERATOR); 
				}
			}
			if (keywordsWithOrOperator.toString().endsWith(OR_OPERATOR)) {
				keywordsWithOrOperator.setLength(keywordsWithOrOperator.toString().length() - OR_OPERATOR.length());
			}
			map.put(FulltextPredicateEvaluator.FULLTEXT, keywordsWithOrOperator.toString());
		}
		// Order By
		map.put(Predicate.ORDER_BY, Constants.PN_JCR_NEWS_PUBLICATION_DATE);
		map.put(Predicate.ORDER_BY + Constants.DOT + Predicate.PARAM_SORT, Predicate.SORT_DESCENDING);
		// Offsets and Limits; usually used for pagination
		map.put(PredicateConverter.GROUP_PARAMETER_PREFIX + Constants.DOT + Predicate.PARAM_OFFSET, offset);
		map.put(PredicateConverter.GROUP_PARAMETER_PREFIX + Constants.DOT + Predicate.PARAM_LIMIT, limit);
		map.put(PredicateConverter.GROUP_PARAMETER_PREFIX + Constants.DOT + Predicate.PARAM_GUESS_TOTAL,
				Constants.STRING_TRUE);

		Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
		query.setStart(Long.parseLong(offset));
		query.setHitsPerPage(Long.parseLong(limit));

		SearchResult result = query.getResult();
		resultsJson = new JSONObject();
		try {
			resultsJson.put(JSON_KEY_TOTAL_RESULTS, result.getHits().size());
			resultsJson.put(JSON_KEY_HAS_MORE, result.hasMore());
		} catch (JSONException e1) {
			LOGGER.error("JSONException in getMostRecentNews {}", e1.getMessage(), e1);
		}
		List<Hit> hits = result.getHits();
		if (hits != null) {
			for (Hit hit : hits) {
				try {
					Resource hitRes = hit.getResource();
					Page page = getPage(hitRes);
					if (page != null) {
						resources.add(new NewsListItem(request, page));
					}
				} catch (RepositoryException e) {
					LOGGER.error("Exception in getMostRecentNews {}", e.getMessage(), e);
				}
			}
		}
		return resources;
	}

	private void writeJson(List<NewsListItem> results, SlingHttpServletResponse response) {
		response.setContentType(Constants.CT_JSON);
		response.setCharacterEncoding(Constants.UTF8_ENCODING);
		try {
			resultsJson.put(JSON_KEY_PAGES, results);
			response.getWriter().write(resultsJson.toString());
		} catch (IOException | JSONException e) {
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
}