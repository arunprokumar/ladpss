package com.adobe.aem.lacounty.dpss.core.workflow.adobe.support.replication;

import java.util.Collection;

import org.apache.sling.api.resource.ResourceResolver;

/**
* See https://github.com/cqsupport/replication-workflow-process for original
* code from Adobe Support. Upgraded here to OSGI R7. 
*/
public interface ActivationReferenceSearch {

	Collection<String> search(String[] paths, ResourceResolver resolver);

}
