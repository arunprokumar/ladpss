package com.adobe.aem.lacounty.dpss.core.models.impl;

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

import com.adobe.aem.lacounty.dpss.core.helper.FooterHelper;
import com.adobe.aem.lacounty.dpss.core.models.FooterModel;


@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = FooterModel.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class FooterModelImpl implements FooterModel {
	
	private static final Logger LOG = LoggerFactory.getLogger(FooterModelImpl.class);
	
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
	public List<FooterHelper> getBureausLinks() {
		List<FooterHelper> footerDetailsBean=new ArrayList<>();
		try {
            Resource footerDetailBean=componentResource.getChild("bureauslinkdetails");
            if(footerDetailBean!=null){
                for (Resource footerBean : footerDetailBean.getChildren()) {
                    footerDetailsBean.add(new FooterHelper(footerBean));
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Book Details With Bean {} ",e.getMessage());
        }
		return footerDetailsBean;
	}

	@Override
	public List<FooterHelper> getExternalLinks() {
		List<FooterHelper> footerDetailsBean=new ArrayList<>();
		try {
            Resource footerDetailBean=componentResource.getChild("externallinkdetails");
            if(footerDetailBean!=null){
                for (Resource footerBean : footerDetailBean.getChildren()) {
                    footerDetailsBean.add(new FooterHelper(footerBean));
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Book Details With Bean {} ",e.getMessage());
        }
		return footerDetailsBean;
	}
	
	@Override
	public List<FooterHelper> getPolicyLinks() {
		List<FooterHelper> footerDetailsBean=new ArrayList<>();
		try {
            Resource footerDetailBean=componentResource.getChild("policylinkdetails");
            if(footerDetailBean!=null){
                for (Resource footerBean : footerDetailBean.getChildren()) {
                    footerDetailsBean.add(new FooterHelper(footerBean));
                }
            }
        }catch (Exception e){
            LOG.info("\n ERROR while getting Book Details With Bean {} ",e.getMessage());
        }
		return footerDetailsBean;
	}
	

}
