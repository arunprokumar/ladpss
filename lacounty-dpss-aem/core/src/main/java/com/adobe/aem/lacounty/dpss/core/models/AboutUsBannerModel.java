package com.adobe.aem.lacounty.dpss.core.models;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.lacounty.dpss.core.beans.HeaderLinkBean;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

@Model(adaptables = {Resource.class,SlingHttpServletRequest.class})
public class AboutUsBannerModel {
	private static final String PN_BG_IMAGE = "bgImage";
	private static final String PN_ITEM_TEXT = "itemText";
	private static final String PN_SUB_TEXT = "subText";
	private static final String PN_ITEM_TARGET = "itemTarget";
	private static final String PN_ITEM_ACTION = "itemAction";
	private static final String PN_ITEM_ICON = "fontAwesomeKey";
	private static final String PN_YBN_TEXT = "ybnItemText";
	private static final String PN_YBN_TARGET = "ybnItemTarget";
	private static final String PN_YBN_ACTION = "ybnItemAction";
	private static final String PN_YBN_IMAGE = "ybnLinkImage";
	private static final String NODE_LINKS = "link";

	@Self
	private SlingHttpServletRequest request;

	@ScriptVariable
	private Resource resource;

	@ScriptVariable
	private PageManager pageManager;

	@ScriptVariable
	private Page currentPage;

	@ValueMapValue(name = JcrConstants.JCR_TITLE)
	@Optional
	private String title;

	@ValueMapValue(name = PN_SUB_TEXT)
	@Optional
	private String subText;

	@ValueMapValue(name = PN_BG_IMAGE)
	@Optional
	private String bgImagePath = "";
	
	@ValueMapValue(name = PN_YBN_IMAGE)
	@Optional
	private String ybnLinkImage = "";
	
	@ValueMapValue(name = PN_YBN_TEXT)
	@Optional
	private String ybnItemText;
	
	@ValueMapValue(name = PN_YBN_TARGET)
	@Optional
	private String ybnItemTarget = "";
	
	@ValueMapValue(name = PN_YBN_ACTION)
	@Optional
	private String ybnItemAction;
	
	private List<HeaderLinkBean> links;
	
	@PostConstruct
	protected void initModel() {
		if (StringUtils.isBlank(title)) {
			title = StringUtils.defaultIfEmpty(currentPage.getPageTitle(),currentPage.getTitle());
		}

		if (StringUtils.isNotEmpty(ybnItemTarget)) {
			ybnItemTarget = ComponentUtils.getURL(request, pageManager, ybnItemTarget);
		} 
		populateLinks();
	}

	public String getBgImagePath() {
		return bgImagePath;
	}

	public List<HeaderLinkBean> getLinks() {
		return new ArrayList<>(links);
	}

	public String getTitle() {
		return title;
	}

	public String getSubText() {
		return subText;
	}

	public String getYbnLinkImage() {
		return ybnLinkImage;
	}

	public String getYbnItemText() {
		return ybnItemText;
	}

	public String getYbnItemTarget() {
		return ybnItemTarget;
	}

	public String getYbnItemAction() {
		return ybnItemAction;
	}
	private void populateLinks() {
		links = new ArrayList<>();
		Resource linkNode = resource.getChild(NODE_LINKS); 
		if(linkNode!=null) {
			for(Resource link: linkNode.getChildren()) {
				links.add(new HeaderLinkBean() {
					private ValueMap properties = link.getValueMap();
					private String linkText = properties.get(PN_ITEM_TEXT,String.class);
					private String linkUrl = properties.get(PN_ITEM_TARGET,String.class);
					private String openInNewTab = properties.get(PN_ITEM_ACTION,String.class);
					private String linkIcon = properties.get(PN_ITEM_ICON,String.class);

					@Override
					public String getLinkText() {
						return linkText;
					}
					@Override
					public String getLinkUrl() {
						return ComponentUtils.getURL(request, pageManager, linkUrl);
					}
					@Override
					public String getOpenInNewTab() {
						return openInNewTab;
					}
					@Override
					public String getLinkIcon() {
						return linkIcon;
					}

				});
			}
		}
	}

}