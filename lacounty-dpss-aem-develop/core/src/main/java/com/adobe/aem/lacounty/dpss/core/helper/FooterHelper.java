package com.adobe.aem.lacounty.dpss.core.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FooterHelper {

	private static final Logger LOG = LoggerFactory.getLogger(FooterHelper.class);
	private String linktext;
	private String blinktext;
	private String plinktext;
	private String linkURL;
	private String blinkURL;
	private String plinkURL;

	public String getLinkText() {
		return linktext;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public String getBlinktext() {
		return blinktext;
	}

	public String getBlinkURL() {
		return blinkURL;
	}

	public String getPlinktext() {
		return plinktext;
	}

	public String getPlinkURL() {
		return plinkURL;
	}

	public FooterHelper(Resource resource) {
		try {
			if (StringUtils.isNotBlank(resource.getValueMap().get("linktext", String.class))) {
				this.linktext = resource.getValueMap().get("linktext", String.class);
			}
			if (StringUtils.isNotBlank(resource.getValueMap().get("linkURL", String.class))) {
				this.linkURL = resource.getValueMap().get("linkURL", String.class);
			}
			if (StringUtils.isNotBlank(resource.getValueMap().get("blinktext", String.class))) {
				this.blinktext = resource.getValueMap().get("blinktext", String.class);
			}
			if (StringUtils.isNotBlank(resource.getValueMap().get("blinkURL", String.class))) {
				this.blinkURL = resource.getValueMap().get("blinkURL", String.class);
			}
			if (StringUtils.isNotBlank(resource.getValueMap().get("plinktext", String.class))) {
				this.plinktext = resource.getValueMap().get("plinktext", String.class);
			}
			if (StringUtils.isNotBlank(resource.getValueMap().get("plinkURL", String.class))) {
				this.plinkURL = resource.getValueMap().get("plinkURL", String.class);
			}

		} catch (Exception e) {
			LOG.info("\n BEAN ERROR : {}", e.getMessage());
		}

	}

}
