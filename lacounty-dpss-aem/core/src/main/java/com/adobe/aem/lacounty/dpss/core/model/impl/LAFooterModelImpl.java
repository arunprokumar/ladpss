package com.adobe.aem.lacounty.dpss.core.model.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.helper.LAFooterHelper;
import com.adobe.aem.lacounty.dpss.core.models.LAFooterModel;

@Model(adaptables = SlingHttpServletRequest.class, adapters = LAFooterModel.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LAFooterModelImpl implements LAFooterModel {

	private static final Logger LOG = LoggerFactory.getLogger(LAFooterModelImpl.class);
	static final String RESOURCE_TYPE = "dpss/components/structure/footer";

	@Inject
	Resource componentResource;

	@ValueMapValue
	@Default(values = "Department of Public Social Services")
	private String title;

	@ValueMapValue
	private String address;

	@ValueMapValue
	private String phone;

	@ValueMapValue
	private String email;

	@ValueMapValue
	private String tagline;

	@ValueMapValue
	private String copyrights;

	@ValueMapValue
	private String reserved;

	@ValueMapValue
	private String logo;

	@ValueMapValue
	private String logolink;

	public String getTitle() {
		return title;
	}

	public String getAddress() {
		return address;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getTagline() {
		return tagline;
	}

	public String getCopyrights() {
		return copyrights;
	}

	public String getReserved() {
		return reserved;
	}

	public String getLogo() {
		return logo;
	}

	public String getLogolink() {
		return logolink;
	}

	@Override
	public List<LAFooterHelper> getBureausLinks() {
		List<LAFooterHelper> footerDetailsBean = new ArrayList<>();
		try {
			Resource footerDetailBean = componentResource.getChild("bureauslinkdetails");
			if (footerDetailBean != null) {
				for (Resource footerBean : footerDetailBean.getChildren()) {
					footerDetailsBean.add(new LAFooterHelper(footerBean));
				}
			}
		} catch (Exception e) {
			LOG.info("\n ERROR while getting Book Details With Bean {} ", e.getMessage());
		}
		return footerDetailsBean;
	}

	@Override
	public List<LAFooterHelper> getExternalLinks() {
		List<LAFooterHelper> footerDetailsBean = new ArrayList<>();
		try {
			Resource footerDetailBean = componentResource.getChild("externallinkdetails");
			if (footerDetailBean != null) {
				for (Resource footerBean : footerDetailBean.getChildren()) {
					footerDetailsBean.add(new LAFooterHelper(footerBean));
				}
			}
		} catch (Exception e) {
			LOG.info("\n ERROR while getting Book Details With Bean {} ", e.getMessage());
		}
		return footerDetailsBean;
	}

	@Override
	public List<LAFooterHelper> getPolicyLinks() {
		List<LAFooterHelper> footerDetailsBean = new ArrayList<>();
		try {
			Resource footerDetailBean = componentResource.getChild("policylinkdetails");
			if (footerDetailBean != null) {
				for (Resource footerBean : footerDetailBean.getChildren()) {
					footerDetailsBean.add(new LAFooterHelper(footerBean));
				}
			}
		} catch (Exception e) {
			LOG.info("\n ERROR while getting Book Details With Bean {} ", e.getMessage());
		}
		return footerDetailsBean;
	}

}
