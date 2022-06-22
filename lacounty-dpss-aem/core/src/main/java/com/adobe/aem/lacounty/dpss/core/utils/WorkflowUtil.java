package com.adobe.aem.lacounty.dpss.core.utils;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.exec.WorkflowData;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class WorkflowUtil {
	public static final String SECTION_HEAD_APPROVER_PROPERTY = "sectionHead";
	public static final String ADMINISTRATORS_DEFAULT_SECTION_HEAD= "administrators";

	private static final Logger log = LoggerFactory.getLogger(WorkflowUtil.class);
	
	/**
	 * @param resourceResolver
	 * @param workflowData
	 * @return the principal ID set in the page properties for section head approval
	 *         process. If this is not set, check the parent page.
	 */
	
	private WorkflowUtil () {
		
	}
	
	public static String getSectionHeadApproverPrincipalFromPageProperties(ResourceResolver resourceResolver,
			WorkflowData workflowData) {
		String payloadPath = workflowData.getPayload().toString();
		
		String sectionHeadApproverPrincipal = null;
		String path = resourceResolver.resolve(payloadPath).getPath();
		PageManager pageMangaer = resourceResolver.adaptTo(PageManager.class);
		if(pageMangaer == null) {
			log.debug("Unable to get pageManager. Set to default of Admin approver.");
			sectionHeadApproverPrincipal = ADMINISTRATORS_DEFAULT_SECTION_HEAD;
			return sectionHeadApproverPrincipal;
		}
		Page currentPage = pageMangaer.getPage(path);
		while(sectionHeadApproverPrincipal == null) {
			
			if(currentPage.getDepth() > 2) {
				if (currentPage.getProperties().get(WorkflowUtil.SECTION_HEAD_APPROVER_PROPERTY) != null) {
					sectionHeadApproverPrincipal = currentPage.getProperties().get(WorkflowUtil.SECTION_HEAD_APPROVER_PROPERTY)
							.toString();
					log.debug("Found Section head approver at {} as: {}", currentPage.getPath(), sectionHeadApproverPrincipal);
				} else {
					currentPage = currentPage.getParent(); 
					log.debug("Check parent page propertise for section head approver @ {}", currentPage.getPath());
				}
			} else {
				log.debug("No section head set in ancestry of page. Set to default of Admin approver.");
				sectionHeadApproverPrincipal = ADMINISTRATORS_DEFAULT_SECTION_HEAD;
			}
		}
		
		return sectionHeadApproverPrincipal;
	}
}
