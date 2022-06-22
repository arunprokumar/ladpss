package com.adobe.aem.lacounty.dpss.core.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolverFactory;

public class ServiceUserUtil {

	private static final String WORKFLOW_SUBSERVICE = "workflow-service";
	
	static Map<String, Object> serviceUserParam = new HashMap<>();

	public static Map<String, Object> getWorkflowServiceUserParam() {
		serviceUserParam.put(ResourceResolverFactory.SUBSERVICE, WORKFLOW_SUBSERVICE);
		return serviceUserParam; 
	}
	
	private ServiceUserUtil() {
		//Private constructor as utility will have only static method
	}

}
