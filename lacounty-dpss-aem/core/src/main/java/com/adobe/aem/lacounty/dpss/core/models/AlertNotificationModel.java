package com.adobe.aem.lacounty.dpss.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;

@Model(adaptables = {Resource.class,SlingHttpServletRequest.class},
adapters = {AlertNotification.class, ComponentExporter.class },
resourceType = {AlertNotificationModel.ALERT_NOTIFICATION_RT})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class AlertNotificationModel implements AlertNotification {
	private static final Logger LOG = LoggerFactory.getLogger(AlertNotificationModel.class);
	private static final String ALERT_OR_NOTIFICATION = "alertOrNotification";
	private static final String ALERT_NOTIFICATION_CONTENT = "alertNotificationContent";
	public static final String ALERT_NOTIFICATION_RT = "dpss/components/content/alert-notification/v1/alert-notification";


	@ScriptVariable
	private Resource resource;
	
	@ValueMapValue(name = PROPERTY_RESOURCE_TYPE, injectionStrategy = InjectionStrategy.OPTIONAL)
	@Default(values = "No resourceType")
	protected String resourceType;

	@ValueMapValue(name = ALERT_OR_NOTIFICATION)
	@Default(values = "alert")
	@Optional
	private String alertOrNotification;

	@ValueMapValue(name = ALERT_NOTIFICATION_CONTENT)
	@Optional
	private String alertNotificationContent;

	@PostConstruct
	protected void init() {
		LOG.info("Alert and Notification Model sucessfully executed!");
	}

	@Override
	public String getExportedType() {
		return resource.getResourceType();
	}
	@Override
	public String getAlertOrNotification() {
		return alertOrNotification;
	}
	@Override
	public String getAlertNotificationContent() {
		return alertNotificationContent;
	}
}
