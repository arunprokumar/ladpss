package com.adobe.aem.lacounty.dpss.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;

import com.adobe.aem.lacounty.dpss.core.beans.TagsListItem;
import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.crx.JcrConstants;
import com.drew.lang.annotations.NotNull;

public class ComponentUtils {
	private ComponentUtils() {
	}

	private static final String EXTN_HTML = ".html";

	/**
	 * @param imageReference the imageReference
	 * @param transformType  the imageReference
	 * @return transformedImgPath
	 */
	public static String getTransformImgPath(String imageReference, String transformType) {
		String transformedImgPath = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(imageReference)) {
			String imgExt = FilenameUtils.getExtension(imageReference);
			if ("svg".equalsIgnoreCase(imgExt)) {
				transformedImgPath = imageReference;
			} else {
				transformedImgPath = imageReference + ".transform/" + transformType + "/img." + imgExt;
			}
		}
		return transformedImgPath;
	}

	/**
	 * Method is used to get vanity url dialog.
	 * 
	 * @param request the Request
	 * @param pageManager the PageManager
	 * @param path the inputPath
	 * @return updatedUrl
	 */
	public static String getURL(SlingHttpServletRequest request, PageManager pageManager, String path) {
		Page page = pageManager.getPage(path);
		if (page != null) {
			String vanityURL = page.getVanityUrl();
			if (StringUtils.isEmpty(vanityURL)) {
				return request.getContextPath() + page.getPath() + EXTN_HTML;
			} else {
				return request.getContextPath() + vanityURL;
			}
		}
		return (StringUtils.isNotBlank(path)) ? request.getResourceResolver().map(path) : StringUtils.EMPTY;
	}

	/**
	 * Method is used to get runmode dialog.
	 * 
	 * @param slingSettingsService the slingSettingsService
	 * @return runMode
	 */
	public static String getCurrentRunmode(SlingSettingsService slingSettingsService) {
		String runMode;
		final Set<String> runModes = slingSettingsService.getRunModes();
		final String publishMode = runModes.contains(Externalizer.PUBLISH) ? Externalizer.PUBLISH : StringUtils.EMPTY;
		runMode = runModes.contains(Externalizer.AUTHOR) ? Externalizer.AUTHOR : publishMode;
		return runMode;
	}

	/**
	 * @param page the Page
	 * @return result
	 */
	public static Page getRedirectTarget(@NotNull Page page) {
		Page result = page;
		String redirectTarget;
		PageManager pageManager = page.getPageManager();
		Set<String> redirectCandidates = new LinkedHashSet<>();
		redirectCandidates.add(page.getPath());
		while (result != null && StringUtils
				.isNotEmpty(redirectTarget = result.getProperties().get("cq:redirectTarget", String.class))) {
			result = pageManager.getPage(redirectTarget);
			if (result != null && !redirectCandidates.add(result.getPath())) {
				break;
			}
		}
		return result;
	}

	/**
	 * @param html the Html
	 * @param charcter the inputCharacter
	 * @param replacement the Replacement to  be done
	 * @return updatedHtml
	 */
	public static String getUnescapedHtml(String html, String charcter, String replacement) {
		return StringUtils.replaceAll(html, charcter, replacement);
	}

	/**
	 * @param request the Request
	 * @param page the Input Page
	 * @return locale
	 */
	public static Locale getLocale(SlingHttpServletRequest request, Page page) {
		if (page == null) {
			return request.getLocale();
		} else {
			return page.getLanguage(false);
		}
	}

	/**
	 * @param tagPropertyName the TagPropertyName
	 * @param resource the input Resource
	 * @return tagValues
	 */
	public static List<String> getTagValuesAsList(final String tagPropertyName, Resource resource) {
		final Object value = resource.getValueMap().get(tagPropertyName);
		List<String> tagValues = new ArrayList<>();
		if (value instanceof String[]) {
			tagValues = Arrays.asList((String[]) value);
		} else if (value instanceof String) {
			tagValues.add((String) value);
		}
		return tagValues;
	}

	/**
	 * @param request the Request
	 * @param tagValues the TagValues
	 * @return allTagTitles
	 */
	public static Collection<String> getTagTitles(SlingHttpServletRequest request, final List<String> tagValues) {
		final Collection<String> allTagTitles = new LinkedHashSet<>();
		PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
		if (pageManager != null) {
			Page page = pageManager.getContainingPage(request.getResource());
			final Locale locale = getLocale(request, page);
			final TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
			for (final String tagId : tagValues) {
				final Tag tag = tagManager.resolve(tagId);
				if (tag != null) {
					allTagTitles.add(tag.getTitle(locale));
				} else {
					allTagTitles.add(tagId);
				}
			}
		}
		return allTagTitles;
	}

	/**
	 * @param request the SlingRequest
	 * @param path the inputPath
	 * @return tagsList
	 */
	public static List<TagsListItem> getDpssTags(SlingHttpServletRequest request, String path) {
		ResourceResolver resolver = request.getResourceResolver();
		List<TagsListItem> tagsList = new ArrayList<>();
		Resource namespace = resolver.getResource(path);
		if (namespace != null) {
			Iterator<Resource> resIterator = namespace.listChildren();
			while (resIterator.hasNext()) {
				Resource child = resIterator.next();
				//Check for only cq:Tag type now to avoid rep:policy node 
				if (null != child && !child.getValueMap().isEmpty()
						&& child.getValueMap().containsKey(JcrConstants.JCR_PRIMARYTYPE)
						&& child.getValueMap().get(JcrConstants.JCR_PRIMARYTYPE).equals(TagConstants.NT_TAG)) {
					Tag tag = child.adaptTo(Tag.class);
					tagsList.add(new TagsListItem(request, tag));
					if (child.hasChildren()) {
						getDpssTags(request, tag.getPath());
					}
				}
			}
		}
		Collections.sort(tagsList, (tag1, tag2) -> tag1.getTagTitle().compareToIgnoreCase(tag2.getTagTitle()));
		return tagsList;
	}	
	
}