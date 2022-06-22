/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
package com.adobe.aem.lacounty.dpss.core.corecomponents.internal.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.corecomponents.internal.models.v1.AssetListItemImpl;
import com.adobe.aem.lacounty.dpss.core.corecomponents.internal.models.v1.PageListItemImpl;
import com.adobe.aem.lacounty.dpss.core.corecomponents.internal.models.v2.SearchImpl;
import com.adobe.aem.lacounty.dpss.core.corecomponents.models.ListItem;
import com.adobe.aem.lacounty.dpss.core.corecomponents.models.Search;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagConstants;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(service = Servlet.class, property = {
		"sling.servlet.selectors=" + AdvanceSearchResultServlet.DEFAULT_SELECTOR, "sling.servlet.resourceTypes=cq/Page",
		"sling.servlet.extensions=json", "sling.servlet.methods=GET" })
public class AdvanceSearchResultServlet extends SlingSafeMethodsServlet {


	private static final long serialVersionUID = -2057160959157605818L;
	
	protected static final String DEFAULT_SELECTOR = "advancesearchresults";
	protected static final String PARAM_FULLTEXT = "fulltext";
	protected static final String PARAM_SORT = "sort";
	protected static final String PARAM_ORDERBY = "orderby";
	protected static final String PARAM_TAGS = "tags";

	private static final String PARAM_RESULTS_OFFSET = "resultsOffset";
	private static final String PREDICATE_FULLTEXT = "_fulltext";
	private static final String PREDICATE_TYPE = "type";
	private static final String PREDICATE_PATH = "path";
	private static final String NN_STRUCTURE = "structure";
	private static final String PREDICATE_ORDERBY = "orderby";
	private static final String PREDICATE_SORT = "orderby.sort";
	private static final String PREDICATE_TAG = "tagid.property";
	private static final String PREDICATE_GUESS_TOTAL = "p.guessTotal";
	private static final String PREDICATE_GROUP = "_group.";
	private static final Integer PREDICATE_FULLTEXT_GROUP = 1; 
	private static final Integer PREDICATE_TYPE_GROUP = 2; 
	private static final Integer PREDICATE_PATH_GROUP = 3; 
	private static final Integer PREDICATE_NO_INDEX_TAG_GROUP = 4; 
	private static final String PREDICATE_OR = "p.or";
	private static final String PREDICATE_EXCERPT = "p.excerpt"; 
	private static final String ONE_PROPERTY = "1_property";
	private static final String OPERATION = ".operation";
	private static final String VALUE = ".value";
	private static final String PAGE_TAGS_PROPERTY = JcrConstants.JCR_CONTENT + "/" + TagConstants.PN_TAGS;

	private static final Logger LOGGER = LoggerFactory.getLogger(AdvanceSearchResultServlet.class);

	@Reference
	private transient QueryBuilder queryBuilder;

	@Reference
	private transient LanguageManager languageManager;

	@Reference
	private transient LiveRelationshipManager relationshipManager;

	@Override
	protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
			throws IOException {
		Page currentPage = getCurrentPage(request);
		if (currentPage != null) {
			Resource searchResource = getSearchContentResource(request, currentPage);
			Map<String, Object> results = getResults(request, searchResource, currentPage);
			writeJson(results, response);
		}
	}

	private Page getCurrentPage(SlingHttpServletRequest request) {
		Page currentPage = null;
		Resource currentResource = request.getResource();
		ResourceResolver resourceResolver = currentResource.getResourceResolver();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if (pageManager != null) {
			currentPage = pageManager.getContainingPage(currentResource.getPath());
		}
		return currentPage;
	}

	private void writeJson(Map<String, Object> results, SlingHttpServletResponse response) {
		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(response.getWriter(), results);
		} catch (IOException e) {
			LOGGER.error("Error while writing the Json {}",e.getMessage());
		}
	}

	private Resource getSearchContentResource(SlingHttpServletRequest request, Page currentPage) {
		Resource searchContentResource = null;
		RequestPathInfo requestPathInfo = request.getRequestPathInfo();
		Resource resource = request.getResource();
		String relativeContentResource = requestPathInfo.getSuffix();
		if (StringUtils.startsWith(relativeContentResource, "/")) {
			relativeContentResource = StringUtils.substring(relativeContentResource, 1);
		}
		if (StringUtils.isNotEmpty(relativeContentResource)) {
			searchContentResource = resource.getChild(relativeContentResource);
			if (searchContentResource == null) {
				PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
				if (pageManager != null) {
					Template template = currentPage.getTemplate();
					if (template != null) {
						Resource templateResource = request.getResourceResolver().getResource(template.getPath());
						if (templateResource != null) {
							searchContentResource = templateResource
									.getChild(NN_STRUCTURE + "/" + relativeContentResource);
						}
					}
				}
			}
		}
		return searchContentResource;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getResults(SlingHttpServletRequest request, Resource searchResource, Page currentPage) {
		int searchTermMinimumLength = SearchImpl.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT;
		String searchTermNoIndexTag = SearchImpl.PROP_SEARCH_TERM_NO_INDEX_DEFAULT;
		int resultsSize = SearchImpl.PROP_RESULTS_SIZE_DEFAULT;
		Map<String, Object> resultsMap = new HashMap<>();
		
		String sort = StringUtils.EMPTY;
		String orderBy = StringUtils.EMPTY;
		String[] tags = ArrayUtils.EMPTY_STRING_ARRAY;
		String tagProperty = StringUtils.EMPTY;
		boolean showResultCount = false;
		Map<String, String> multipaths = new HashMap<>();
		String guessTotal = "false";
		if (searchResource != null) {
			Resource searchRootsMultifield = searchResource.getChild(Search.PN_SEARCH_ROOTS);
			if(searchRootsMultifield != null) {
				multipaths = getSearchRootPaths(searchRootsMultifield, currentPage); 
			} else {
				multipaths.put(SearchImpl.PROP_SEARCH_ROOT_DEFAULT, NameConstants.NT_PAGE);
				multipaths.put(SearchImpl.PROP_SEARCH_ROOT_ASSET_DEFAULT, DamConstants.NT_DAM_ASSET);
			}
			
			ValueMap valueMap = searchResource.getValueMap();
			ValueMap contentPolicyMap = getContentPolicyProperties(searchResource);
			searchTermMinimumLength = valueMap.get(Search.PN_SEARCH_TERM_MINIMUM_LENGTH, contentPolicyMap
					.get(Search.PN_SEARCH_TERM_MINIMUM_LENGTH, SearchImpl.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT));
			searchTermNoIndexTag = valueMap.get(Search.PN_SEARCH_TERM_NO_INDEX_TAG, contentPolicyMap
					.get(Search.PN_SEARCH_TERM_NO_INDEX_TAG, SearchImpl.PROP_SEARCH_TERM_NO_INDEX_DEFAULT));
			resultsSize = valueMap.get(Search.PN_RESULTS_SIZE,
					contentPolicyMap.get(Search.PN_RESULTS_SIZE, SearchImpl.PROP_RESULTS_SIZE_DEFAULT));
			sort = request.getParameter(PARAM_SORT) != null ? request.getParameter(PARAM_SORT)
					: valueMap.get(Search.PN_DEFAULT_SORT_DIRECTION, String.class);
			orderBy = request.getParameter(PARAM_ORDERBY) != null ? request.getParameter(PARAM_ORDERBY)
					: valueMap.get(Search.PN_DEFAULT_SORT, String.class);
			tags = request.getParameter(PARAM_TAGS) != null ? request.getParameter(PARAM_TAGS).split(",") : tags;
			tagProperty = valueMap.get("tagProperty", String.class);
			showResultCount = valueMap.get(Search.PN_SHOW_RESULT_COUNT, false);
			if (showResultCount) {
				guessTotal = valueMap.get(Search.PN_GUESS_TOTAL, String.class) != null
						? valueMap.get(Search.PN_GUESS_TOTAL, String.class)
						: "false";
			}
		} else {
			String languageRoot = languageManager.getLanguageRoot(currentPage.getContentResource()).getPath();
			String searchRootPagePath = getSearchRootPagePath(languageRoot, currentPage);
			multipaths.put(searchRootPagePath, NameConstants.NT_PAGE);
		}
		if (multipaths.isEmpty()) {
			String searchRootPagePath = currentPage.getPath();
			multipaths.put(searchRootPagePath, NameConstants.NT_PAGE);
			
		}
		
		
		List<ListItem> results = new ArrayList<>();
		String userInput = request.getParameter(PARAM_FULLTEXT);
		Map<String, String> predicatesMap = new HashMap<>();
		if (userInput == null || userInput.trim().length() < searchTermMinimumLength) {
			return resultsMap;
		}else {
			
			int fulltextCounter = 0;
			String[] userInputArray = userInput.trim().split(" "); 
			List<String> userInputList = Arrays.asList(userInputArray);
			
			if(userInputList.size() > 0) {
				for(String userInputItem : userInputList) {
					userInputItem.trim();
					if(! ( (userInputItem == null) || (userInputItem.length() < searchTermMinimumLength) || 
							(predicatesMap.containsValue(userInputItem)) )) {
						fulltextCounter ++;
						predicatesMap.put(groupPredicateBuilder(PREDICATE_FULLTEXT_GROUP, fulltextCounter, 
								PREDICATE_FULLTEXT).toString(), userInputItem);
					}
				}
				
				if(fulltextCounter > 1) {
					predicatesMap.put(new StringBuilder().append(PREDICATE_FULLTEXT_GROUP).append(PREDICATE_GROUP)
							.append(PREDICATE_OR).toString() , Boolean.TRUE.toString());
				}
			}
			
			if(fulltextCounter == 0) {
				return resultsMap;
			}
		}
		
		
		long resultsOffset = 0;
		if (request.getParameter(PARAM_RESULTS_OFFSET) != null) {
			resultsOffset = Long.parseLong(request.getParameter(PARAM_RESULTS_OFFSET));
		}
				
		int searchPathCounter = 0; 
		
		if(multipaths.size() > 1) {
			// Complex predicates for path/type 
			Iterator<?> entries = multipaths.entrySet().iterator(); 
			Map.Entry<String,String> entry = null; 
			while (entries.hasNext()) {
				searchPathCounter ++; 
				entry = (Entry<String, String>) entries.next();
				predicatesMap.put(PREDICATE_PATH_GROUP + PREDICATE_GROUP + searchPathCounter + PREDICATE_GROUP + PREDICATE_PATH, entry.getKey()); 
				predicatesMap.put(PREDICATE_TYPE_GROUP + PREDICATE_GROUP + searchPathCounter + PREDICATE_GROUP + PREDICATE_TYPE, entry.getValue()); 
			}
			if(searchPathCounter > 1) {
				predicatesMap.put(new StringBuilder().append(PREDICATE_PATH_GROUP).append(PREDICATE_GROUP).append(
						PREDICATE_OR).toString(), Boolean.TRUE.toString());
				predicatesMap.put(new StringBuilder().append(PREDICATE_TYPE_GROUP).append(PREDICATE_GROUP).append(
						PREDICATE_OR).toString(), Boolean.TRUE.toString());
			}
			
		} else if (multipaths.size() == 1){
			// Simple predicates for path/type 
			Iterator<?> entries = multipaths.entrySet().iterator(); 
			if(entries.hasNext()) {
				Map.Entry<String,String> entry = (Entry<String, String>) entries.next();
				predicatesMap.put(PREDICATE_PATH, entry.getKey());
				predicatesMap.put(PREDICATE_TYPE, entry.getValue());
			}

		}
		
		// filter out pages tagged with No Index
		String group4 = PREDICATE_NO_INDEX_TAG_GROUP + PREDICATE_GROUP; 
		predicatesMap.put(group4 + "p.not", Boolean.TRUE.toString());
		predicatesMap.put(group4 + ONE_PROPERTY, PAGE_TAGS_PROPERTY);
		predicatesMap.put(group4 + ONE_PROPERTY + VALUE, searchTermNoIndexTag);
		predicatesMap.put(group4 + ONE_PROPERTY + OPERATION, "equals");
	
		predicatesMap.put(PREDICATE_ORDERBY, orderBy);
		predicatesMap.put(PREDICATE_SORT, sort);
		predicatesMap.put(PREDICATE_GUESS_TOTAL, guessTotal);
		predicatesMap.put(PREDICATE_EXCERPT, Boolean.TRUE.toString());
		if (tags.length > 0) {
			predicatesMap.put(PREDICATE_TAG, tagProperty);
			for (int i = 0; i < tags.length; i++) {
				predicatesMap.put("tagid." + i + "_value", tags[i]);
			}
		}
		PredicateGroup predicates = PredicateConverter.createPredicates(predicatesMap);
		ResourceResolver resourceResolver = request.getResource().getResourceResolver();
		Query query = queryBuilder.createQuery(predicates, resourceResolver.adaptTo(Session.class));
		if (resultsSize != 0) {
			query.setHitsPerPage(resultsSize);
		}
		if (resultsOffset != 0) {
			query.setStart(resultsOffset);
		}

		SearchResult searchResult = query.getResult();

		long totalRecords = searchResult.getTotalMatches();
		boolean isLastPage = false;
		if (searchResult.getStartIndex() + resultsSize >= totalRecords) {
			isLastPage = true;
		}
		List<Hit> hits = searchResult.getHits();
		if (hits != null) {
			for (Hit hit : hits) {
				try {
					Resource hitRes = hit.getResource();
					if(hitRes.getResourceType().equals(NameConstants.NT_PAGE)) {
						Page page = getPage(hitRes);
						if (page != null) {
							ListItem listItem = new PageListItemImpl(request, page);
							String exerpt = hit.getExcerpt(); 
							listItem.setExcerpt(exerpt);
							results.add(listItem);
						}
					} 
					else if(hitRes.getResourceType().equals(DamConstants.NT_DAM_ASSET)) {
						Asset asset = hitRes.adaptTo(Asset.class);
						if (asset != null) {
							ListItem listItem = new AssetListItemImpl(request, asset);
							String exerpt = hit.getExcerpt(); 
							listItem.setExcerpt(exerpt);
							results.add(listItem);
						}
					}
				} catch (RepositoryException e) {
					LOGGER.error("Unable to retrieve search results for query.", e);
				}
			}
		}
		resultsMap.put("data", results);
		resultsMap.put("totalRecords", totalRecords);
		resultsMap.put("hasMore", searchResult.hasMore());
		resultsMap.put("isLastPage", isLastPage);
		return resultsMap;
	}
	
	private StringBuilder groupPredicateBuilder(int groupCount, int count, String predicate) {
		return new StringBuilder().append(groupCount).append(PREDICATE_GROUP)
				.append(count).append(predicate); 
	}

	private String getSearchRootPagePath(String searchRoot, Page currentPage) {
		String searchRootPagePath = null;
		PageManager pageManager = currentPage.getPageManager();
		if (StringUtils.isNotEmpty(searchRoot) && pageManager != null) {
			Page rootPage = pageManager.getPage(searchRoot);
			if (rootPage != null) {
				Page searchRootLanguageRoot = languageManager.getLanguageRoot(rootPage.getContentResource());
				Page currentPageLanguageRoot = languageManager.getLanguageRoot(currentPage.getContentResource());
				RangeIterator liveCopiesIterator = null;
				try {
					liveCopiesIterator = relationshipManager.getLiveRelationships(currentPage.adaptTo(Resource.class),
							null, null);
				} catch (WCMException e) {
					// ignore it
				}
				if (searchRootLanguageRoot != null && currentPageLanguageRoot != null
						&& !searchRootLanguageRoot.equals(currentPageLanguageRoot)) {
					// check if there's a language copy of the search root
					Page languageCopySearchRoot = pageManager
							.getPage(ResourceUtil.normalize(currentPageLanguageRoot.getPath() + "/"
									+ getRelativePath(searchRootLanguageRoot, rootPage)));
					if (languageCopySearchRoot != null) {
						rootPage = languageCopySearchRoot;
					}
				} else if (liveCopiesIterator != null) {
					while (liveCopiesIterator.hasNext()) {
						LiveRelationship relationship = (LiveRelationship) liveCopiesIterator.next();
						if (currentPage.getPath().startsWith(relationship.getTargetPath() + "/")) {
							Page liveCopySearchRoot = pageManager.getPage(relationship.getTargetPath());
							if (liveCopySearchRoot != null) {
								rootPage = liveCopySearchRoot;
								break;
							}
						}
					}
				}
				searchRootPagePath = rootPage.getPath();
			}
		}
		return searchRootPagePath;
	}

	private ValueMap getContentPolicyProperties(Resource searchResource) {
		ValueMap contentPolicyProperties = new ValueMapDecorator(new HashMap<>());
		ResourceResolver resourceResolver = searchResource.getResourceResolver();
		ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
		if (contentPolicyManager != null) {
			ContentPolicy policy = contentPolicyManager.getPolicy(searchResource);
			if (policy != null) {
				contentPolicyProperties = policy.getProperties();
			}
		}
		return contentPolicyProperties;
	}

	@Nullable
	private String getRelativePath(@NotNull Page root, @NotNull Page child) {
		if (child.equals(root)) {
			return ".";
		} else if ((child.getPath() + "/").startsWith(root.getPath())) {
			return child.getPath().substring(root.getPath().length() + 1);
		}
		return null;
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
	
	private Map<String, String> getSearchRootPaths(Resource searchRootsMultifield, Page currentPage) {
		Map<String, String> searchRootPaths = new HashMap<>(); 
		Iterator<Resource> iterator = searchRootsMultifield.listChildren();
		while(iterator.hasNext()) {
			Resource itemsResource = iterator.next();
			if(itemsResource.getValueMap().get("searchpath") !=  null) {
				String path = itemsResource.getValueMap().get("searchpath").toString();
				String type = path.startsWith("/content/dam") ?  DamConstants.NT_DAM_ASSET: NameConstants.NT_PAGE;
				if(type.equals(NameConstants.NT_PAGE)) {
					path = getSearchRootPagePath(path, currentPage);
				}
				searchRootPaths.put(path, type);
				LOGGER.debug("Search path added: {}, : {}", path, type);
			}
		}
		return searchRootPaths; 
	}
}
