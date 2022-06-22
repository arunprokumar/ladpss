package com.adobe.aem.lacounty.dpss.core.workflow.impl.process;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.utils.ServiceUserUtil;
import com.adobe.aem.lacounty.dpss.core.utils.WorkflowUtil;
import com.adobe.granite.workflow.PayloadMap;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(service = ParticipantStepChooser.class, property = {
		"chooser.label=DPSS Section Head Approver for Dynamic Participant Step Chooser" })
public class SectionHeadParticipantStepChooser implements ParticipantStepChooser
{
	private static final Logger log = LoggerFactory.getLogger(SectionHeadParticipantStepChooser.class);

	@Reference
	private ResourceResolverFactory resourceResolverFactory;

	@Override
	public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args)
			throws WorkflowException {
		String departmentApproverPrincipal = null;
		WorkflowData workflowData = workItem.getWorkflowData();
		if (workflowData.getPayloadType().equals(PayloadMap.TYPE_JCR_PATH)) {
			ResourceResolver resourceResolver = null;
			try {
				resourceResolver = resourceResolverFactory.getServiceResourceResolver(ServiceUserUtil.getWorkflowServiceUserParam());
				departmentApproverPrincipal = WorkflowUtil.getSectionHeadApproverPrincipalFromPageProperties(resourceResolver, workflowData);
			} catch (LoginException e) {
				log.error("Unable to login with service user. {} ", e);
			} finally {
				if(resourceResolver != null && resourceResolver.isLive()) {
					resourceResolver.close();
	            }
			}
		}
		return departmentApproverPrincipal;
	}

}
