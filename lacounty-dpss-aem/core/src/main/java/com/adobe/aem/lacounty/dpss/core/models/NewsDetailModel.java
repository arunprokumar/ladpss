package com.adobe.aem.lacounty.dpss.core.models;

import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.day.cq.wcm.api.Page;

@Model(adaptables = {
    Resource.class,
    SlingHttpServletRequest.class
})
public class NewsDetailModel {

    private static final String MOBILE_NEWS_TRANSFORM_CONFIGNAME = "mobileTransformedNDImage";
    private static final String TABLET_NEWS_TRANSFORM_CONFIGNAME = "tabTransformedNDImage";
    private static final String PN_IMAGE_ALIGN = "imageAlign";
    private static final String PN_TITLE = "newsTitle";
    private static final String PN_ARTICLE_IMAGE = "articleImage";

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue(name = PN_TITLE)
    @Optional
    private String newsTitle;

    @ValueMapValue(name = PN_IMAGE_ALIGN)
    @Optional
    private String imageAlign;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String articleDesc;

    @ValueMapValue(name = PN_ARTICLE_IMAGE)
    @Optional
    private String articleImage;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String imageAltText;

    private String articleMobileImage;
    private String articleTabletImage;

    @PostConstruct
    protected void initModel() {
        if (StringUtils.isNotEmpty(articleImage)) {
            articleMobileImage = ComponentUtils.getTransformImgPath(articleImage, MOBILE_NEWS_TRANSFORM_CONFIGNAME);
            articleTabletImage = ComponentUtils.getTransformImgPath(articleImage, TABLET_NEWS_TRANSFORM_CONFIGNAME);
        }
        if (StringUtils.isBlank(newsTitle)) {
            newsTitle = StringUtils.defaultIfEmpty(currentPage.getPageTitle(), currentPage.getTitle());
        }
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public String getArticleDesc() {
        return articleDesc;
    }

    public String getArticleImage() {
        return articleImage;
    }

    public String getImageAltText() {
        return imageAltText;
    }

    public String getArticleMobileImage() {
        return articleMobileImage;
    }

    public String getArticleTabletImage() {
        return articleTabletImage;
    }

    public String getImageAlign() {
        return imageAlign;
    }
}