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
package com.adobe.aem.lacounty.dpss.core.corecomponents.internal.models.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.corecomponents.models.ListItem;
import com.adobe.aem.lacounty.dpss.core.corecomponents.models.Search;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.form.OptionItem;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { Search.class,
		ComponentExporter.class }, resourceType = { SearchImpl.RESOURCE_TYPE })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SearchImpl implements Search {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchImpl.class);
	
	protected static final String RESOURCE_TYPE = "dpss/components/content/futurecorecomponents/searchresult/v1/searchresult";
	
	public static final int PROP_RESULTS_SIZE_DEFAULT = 10;
	public static final int PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT = 3;
	public static final String PROP_SEARCH_TERM_NO_INDEX_DEFAULT = "dpss-general:noIndex";
	public static final String PROP_SEARCH_ROOT_DEFAULT = "/content";
	public static final String PROP_SEARCH_ROOT_ASSET_DEFAULT = "/content/dam";
	public static final String PN_TEXT = "text";
	public static final String PN_VALUE = "value";
	public static final String NN_ITEMS = "sortItems";
	public static final String PN_LOAD_MORE_TEXT = "loadMoreText";
	public static final String LOAD_MORE_TEXT_DEFAULT_VALUE = "Load More";
	public static final String PN_NO_RESULT_TEXT = "noResultText";
	public static final String NO_RESULT_TEXT_DEFAULT_VALUE = "No more results";
	

	public static final String PN_RESULT_COUNT_FORMAT= "resultCountFormat";
	public static final String PN_RESULT_COUNT_FORMAT_DEFAULT_VALUE= "Displaying {count} of {total} results";

	@Self
	private SlingHttpServletRequest request;

	@ScriptVariable
	private Page currentPage;

	@ScriptVariable
	private ValueMap properties;
	protected java.util.List<OptionItem> sortOptions;

	@ScriptVariable
	private Style currentStyle;

	private String relativePath;
	private int resultsSize;
	private int searchTermMinimumLength;
	private String searchTermNoIndexTag;
	private int guessTotal;

	protected java.util.List<ListItem> tags;
	
	protected java.util.Map<String, String> searchRootPaths; 

	@SlingObject
	private Resource resource;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("loadMoreText")
	private String loadMoreText;
	
	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("resultCountFormat")
	private String resultCountFormat;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("facetTitle")
	private String facetTitle;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("sortTitle")
	private String sortTitle;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("enableSort")
	private boolean enableSort;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("enableFacet")
	private boolean enableFacet;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private String tagProperty;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	private String noResultText;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("ascendingLabel")
	@Default(values = "ASC")
	private String ascLabel;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("descendingLabel")
	@Default(values = "DESC")
	private String descLabel;

	@ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
	@Named("showResultCount")
	private boolean showResultCount;
	
	@ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
	private Resource searchRoots;
	

	@PostConstruct
	private void initModel() {
		
		ValueMap contentPolicyMap = getContentPolicyProperties();
		resultsSize = properties.get(Search.PN_RESULTS_SIZE,
				contentPolicyMap.get(Search.PN_RESULTS_SIZE, PROP_RESULTS_SIZE_DEFAULT));
		
		searchTermMinimumLength = properties.get(Search.PN_SEARCH_TERM_MINIMUM_LENGTH, contentPolicyMap
				.get(Search.PN_SEARCH_TERM_MINIMUM_LENGTH, SearchImpl.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT));
		
		searchTermNoIndexTag = properties.get(Search.PN_SEARCH_TERM_NO_INDEX_TAG, contentPolicyMap
				.get(Search.PN_SEARCH_TERM_NO_INDEX_TAG, SearchImpl.PROP_SEARCH_TERM_NO_INDEX_DEFAULT));
	
		loadMoreText = properties.get(PN_LOAD_MORE_TEXT, LOAD_MORE_TEXT_DEFAULT_VALUE);
		noResultText = properties.get(PN_NO_RESULT_TEXT, NO_RESULT_TEXT_DEFAULT_VALUE);
		resultCountFormat = properties.get(PN_RESULT_COUNT_FORMAT, PN_RESULT_COUNT_FORMAT_DEFAULT_VALUE);
		populateSearchRootPaths();
		PageManager pageManager = currentPage.getPageManager();
		Resource currentResource = request.getResource();
		if (pageManager != null) {
			Page containingPage = pageManager.getContainingPage(currentResource);
			if (containingPage != null) {
				relativePath = StringUtils.substringAfter(currentResource.getPath(), containingPage.getPath());
			}
		}
	}


	public void populateTags() {
		final TagManager tagManager = resource.getResourceResolver().adaptTo(TagManager.class);
		tags = new ArrayList<>();
		if (tagManager != null) {
			final Tag[] tagList = tagManager.getTags(resource);
			if (tagList != null) {
				for (final Tag tag : tagList) {
					tags.add(new ListItem() {
						private String tagName = tag.getTagID();
						private String tagTitle = tag.getTitle();

						@Nullable
						@Override
						public String getName() {
							return tagName;
						}

						@Nullable
						@Override
						public String getTitle() {
							return tagTitle;
						}
					});
				}
			}
		}
	}
	
	public void populateSearchRootPaths() {
		searchRootPaths = new HashMap<>(); 
		if(searchRoots != null) {
			Iterator<Resource> iterator = searchRoots.listChildren();
			while (iterator.hasNext()) {
				Resource itemsResource = iterator.next();
				if (itemsResource.getValueMap().get("searchpath") != null) {
					String path = itemsResource.getValueMap().get("searchpath").toString();
					String type = path.startsWith(PROP_SEARCH_ROOT_ASSET_DEFAULT) ? DamConstants.NT_DAM_ASSET : NameConstants.NT_PAGE;
					searchRootPaths.put(path, type);
					LOGGER.debug("Search path added: {}, : {}", path, type);
				}
			}
		}
	}

	private ValueMap getContentPolicyProperties() {
		ValueMap contentPolicyProperties = new ValueMapDecorator(new HashMap<>());
		ResourceResolver resourceResolver = request.getResourceResolver();
		ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
		if (contentPolicyManager != null) {
			ContentPolicy policy = contentPolicyManager.getPolicy(request.getResource());
			if (policy != null) {
				contentPolicyProperties = policy.getProperties();
			}
		}
		return contentPolicyProperties;
	}
	
	public void populateItems() {
		sortOptions = new ArrayList<>();
		Resource childItem = resource.getChild(NN_ITEMS);
		if (childItem != null) {
			childItem.getChildren().forEach(this::addItems);
		}
	}

	private void addItems(Resource res) {
		ValueMap childProperties = res.getValueMap();
		sortOptions.add(new OptionItem() {
			private String text = childProperties.get(PN_TEXT, String.class);
			private String value = childProperties.get(PN_VALUE, String.class);

			@Nullable
			@Override
			public String getText() {
				return text;
			}

			@Nullable
			@Override
			public String getValue() {
				return value;
			}
		});
	}

	@Override
	public List<OptionItem> getSortOptions() {
		if (sortOptions == null) {
			populateItems();
		}
		return sortOptions;
	}

	@Override
	public String getAscLabel() {
		return ascLabel;
	}

	@Override
	public String getDescLabel() {
		return descLabel;
	}

	@Override
	public List<ListItem> getTags() {
		if (tags == null) {
			populateTags();
		}
		return tags;
	}
	
	@Override
	public Map<String, String> getSearchRootPaths () {
		if (searchRootPaths == null) {
			populateSearchRootPaths();
		}
		return searchRootPaths; 
	}

	@Override
	public String getFacetTitle() {
		return facetTitle;
	}

	@Override
	public String getSortTitle() {
		return sortTitle;
	}

	@Override
	public String getTagProperty() {
		return tagProperty;
	}

	@Override
	public int getResultsSize() {
		return resultsSize;
	}

	@Override
	public String getLoadMoreText() {
		return loadMoreText;
	}
	
	@Override
	public String getResultCountFormat() {
		return resultCountFormat;
	}

	@Override
	public int getSearchTermMinimumLength() {
		return searchTermMinimumLength;
	}
	
	@Override
	public String getSearchTermNoIndexTag() {
		return searchTermNoIndexTag;
	}

	@Override
	public String getNoResultText() {
		return noResultText;
	}

	@NotNull
	@Override
	public String getRelativePath() {
		return relativePath;
	}

	@NotNull
	@Override
	public boolean isSortEnabled() {
		return enableSort;
	}

	@NotNull
	@Override
	public boolean isFacetEnabled() {
		return enableFacet;
	}

	@NotNull
	@Override
	public boolean getShowResultCount() {
		return showResultCount;
	}

	@NotNull
	@Override
	public int getGuessTotal() {
		return guessTotal;
	}
	
	@NotNull
	public Resource getSearchRoots() {
		return searchRoots; 
	}

}
