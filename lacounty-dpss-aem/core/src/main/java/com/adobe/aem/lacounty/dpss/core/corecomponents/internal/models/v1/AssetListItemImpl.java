package com.adobe.aem.lacounty.dpss.core.corecomponents.internal.models.v1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import com.adobe.aem.lacounty.dpss.core.corecomponents.internal.Utils;
import com.adobe.aem.lacounty.dpss.core.corecomponents.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagConstants;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AssetListItemImpl implements ListItem {
    public static final String DATE_FORMAT = "MMMM dd, yyyy";

    protected SlingHttpServletRequest request;
    protected Asset asset;
    
    private String excerpt; 

    public AssetListItemImpl(@NotNull SlingHttpServletRequest request, @NotNull Asset asset) {
        this.request = request;
        this.asset = asset;
    }

    @Override
    public String getURL() {
        return Utils.getURL(request, asset);
    }

    @Override
    public String getTitle() {
    	String title = StringUtils.EMPTY; 
    	
		if (asset.getMetadata().get(DamConstants.DC_TITLE) != null) {
			Object[] dcTitle = (Object[]) asset.getMetadata().get(DamConstants.DC_TITLE);
			if(dcTitle.length > 0 ) {
				title = dcTitle[0].toString();
			}
    	} else if (title.equals(StringUtils.EMPTY)) {
			title = asset.getName();
		}
        return title;
    }

    @Override
    public String getDescription() {
        return getAssetMetadataPropertyAsString(DamConstants.DC_DESCRIPTION); 
    }

    @Override
    public Calendar getLastModified() {
        return (Calendar) asset.getMetadata().get(JcrConstants.JCR_LASTMODIFIED);
    }

    @Override
    public String getPath() {
        return asset.getPath();
    }
    
    @Override
    public String getTags() {    	
    	StringBuilder tagsBuilder = new StringBuilder();
    	String tags = StringUtils.EMPTY;
    	Object[] tagsObject =(Object[]) asset.getMetadata(TagConstants.PN_TAGS);
		if (tagsObject != null && tagsObject.length > 0) {
			TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
			for (Object tag : tagsObject) {
				Tag tempTag = tagManager.resolve(tag.toString());
				String tagTitle = tempTag.getTitle(); 
				tagsBuilder.append(tagTitle).append(", ");
			}
			
			tags = tagsBuilder.toString().trim(); 
			if(tags.endsWith(",")) {
				tags = tags.substring(0, tags.lastIndexOf(','));
			}
    	}
    	return tags;
    }

    @Override
    @JsonIgnore
    public String getName() {
        return asset.getName();
    }
    
    @Override
    public String getFormattedLastModifiedDate() {
        return getFormattedDate(getLastModified(), DATE_FORMAT);
    }
    
    @Override
    public String getExcerpt() {
    	return excerpt; 
    }
    @Override
    public void setExcerpt(String excerpt) {
    	this.excerpt = excerpt;
    }
    
    private String getAssetMetadataPropertyAsString(String propName) {
    	if(asset.getMetadata().containsKey(propName)) {
    		return asset.getMetadataValue(propName).toString();
    	}else {
    		return StringUtils.EMPTY;
    	}
    	
    }
    
    private String getFormattedDate(Calendar date, String format) {
        if (null == date) {
          return StringUtils.EMPTY;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        return formatter.format(date.getTime());
      }
}
