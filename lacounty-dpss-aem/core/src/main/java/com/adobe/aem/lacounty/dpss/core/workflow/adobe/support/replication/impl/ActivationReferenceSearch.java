package com.adobe.aem.lacounty.dpss.core.workflow.adobe.support.replication.impl;

import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;

/**
 * See https://github.com/cqsupport/replication-workflow-process for original
 * code from Adobe Support. Upgraded here to OSGI R7. 
 */
public interface ActivationReferenceSearch {

	List<String> search(String[] paths, ResourceResolver resolver);

}
