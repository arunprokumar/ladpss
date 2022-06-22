package com.adobe.aem.lacounty.dpss.core.components.models;

import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Required;
import org.apache.sling.models.annotations.Via;

@Model(adaptables = { SlingHttpServletRequest.class,
		Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CustomFormContainerModel {

	@Inject @Via("resource")
	@Required
	private String mailto; // note: multifield

	@Inject @Via("resource")
	@Required
	private String subject;

	@Inject @Via("resource")
	@Required
	private String from;

	@Inject @Via("resource")
	private String cc; // note: multifield

	@Inject @Via("resource")
	private String redirect;

	@Inject @Via("resource")
	@Default(values = "/etc/notification/email/dpss/forms/en-form-submitted-user-confirmation.html")
	private String formSubmittedUserConfirmation;

	@Inject @Via("resource")
	@Default(values = "/etc/notification/email/dpss/forms/en-form-submitted-values-notification.html")
	private String formSubmittedValuesNotification;
	
	@Inject @Via("resource")
	private String senderName; 


	public String getMailto() {
		return mailto;
	}

	public String getSubject() {
		return subject;
	}

	public String getFormSubmittedUserConfirmation() {
		return formSubmittedUserConfirmation;
	}

	public String getFormSubmittedValuesNotification() {
		return formSubmittedValuesNotification;
	}

	public String getFrom() {
		return from;
	}

	public String getCc() {
		return cc;
	}

	public String getRedirect() {
		return redirect;
	}

	public String getSenderName() {
		return senderName;
	}
}
