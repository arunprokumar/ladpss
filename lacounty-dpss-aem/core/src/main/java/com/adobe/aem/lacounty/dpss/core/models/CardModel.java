
package com.adobe.aem.lacounty.dpss.core.models;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;

@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class CardModel {

	private static final Logger LOG = LoggerFactory.getLogger(CardModel.class);
	private static final String  MOBILE_CARD_TRANSFORM_CONFIGNAME="mobileTransformedCardImage";
	private static final String TABLET_CARD_TRANSFORM_CONFIGNAME="tabTransformedCardImage";
	
	@ValueMapValue(name = "cardImage")
	@Optional
	private String cardImage;

	@ValueMapValue(name = "cardImageAltTxt")
	@Optional
	private String cardImageAltTxt;

	@ValueMapValue(name = "expandLabel")
	@Optional
	@Default(values = "More")
	private String expandLabel;

	@ValueMapValue(name = "collapseLabel")
	@Optional
	@Default(values = "Less")
	private String collapseLabel;

	@ValueMapValue(name = "description")
	@Optional
	private String description;

	@ValueMapValue(name = "cardTitle")
	@Optional
	private String cardTitle;

	@ValueMapValue(name = "cardSubtitle")
	@Optional
	private String cardSubtitle;
	
	@ValueMapValue(name = "hideMore")
	@Optional
	private boolean hideDescription;

	public boolean isHideDescription() {
		return hideDescription;
	}

	private String cardMobileImage;
	private String cardTabletImage;

	@PostConstruct
	protected void initModel() {
		if (StringUtils.isNoneEmpty(cardImage)) {
			LOG.info("Executing Card Model");
			cardMobileImage=ComponentUtils.getTransformImgPath(cardImage,MOBILE_CARD_TRANSFORM_CONFIGNAME);
			cardTabletImage=ComponentUtils.getTransformImgPath(cardImage, TABLET_CARD_TRANSFORM_CONFIGNAME);
			LOG.info("Card Model execution sucessfull");
		}

	}

	public String getCardImage() {
		return cardImage;
	}

	public String getCardImageAltTxt() {
		return cardImageAltTxt;
	}

	public String getExpandLabel() {
		return expandLabel;
	}

	public String getCollapseLabel() {
		return collapseLabel;
	}

	public String getDescription() {
		return description;
	}

	public String getCardTitle() {
		return cardTitle;
	}

	public String getCardSubtitle() {
		return cardSubtitle;
	}

	public String getCardMobileImage() {
		return cardMobileImage;
	}

	public String getCardTabletImage() {
		return cardTabletImage;
	}

}