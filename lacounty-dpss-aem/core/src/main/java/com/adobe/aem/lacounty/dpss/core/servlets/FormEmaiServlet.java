package com.adobe.aem.lacounty.dpss.core.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections; 
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.OptingServlet;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.auth.core.AuthUtil;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.email.EmailService;
import com.adobe.aem.lacounty.dpss.core.components.models.CustomFormContainerModel;
import com.adobe.aem.lacounty.dpss.core.utils.PageMetaDataUtils;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import com.day.cq.wcm.foundation.forms.FormsHelper;

@Component(service = Servlet.class, 
	property = { Constants.SERVICE_DESCRIPTION + "=DPSS Form Mail Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_POST,
		"sling.servlet.resourceTypes=" + "dpss/components/form/container",
		"sling.servlet.selectors=" + "mail",
		"service.ranking="+ "200",
		"sling.servlet.extensions=" + "html" })
@Designate(ocd = FormEmaiServlet.Config.class)
public class FormEmaiServlet extends SlingAllMethodsServlet
	implements OptingServlet {

	private static final long serialVersionUID = -2985313612534603774L;

	protected static final String EXTENSION = "html";
	
	protected static final String MAILTO_PROPERTY = "mailto";
    protected static final String CC_PROPERTY = "cc";
    protected static final String BCC_PROPERTY = "bcc";
    protected static final String SUBJECT_PROPERTY = "subject";
    protected static final String SENDER_EMAIL_ADDRESS_PROPERTY = "senderEmailAddress";
    protected static final String SENDER_NAME_PROPERTY = "senderName";
    
    protected static final String FORM_FIELDS_ALL = "formFields";
    protected static final String SUBMITTER_EMAIL_PARAMETER = "submitter-email";
    protected static final String REQUEST_URL = "requestURL";

    @Reference
    private transient FormStructureHelperFactory formStructureHelperFactory;
	
    @Reference 
    transient SlingSettingsService slingSettingsService;
    
	@Reference
	transient EmailService emailService;

	private static final Logger LOGGER = LoggerFactory.getLogger(FormEmaiServlet.class);
	
	private transient  Config config; 
	
	@ObjectClassDefinition(name = "DPSS Form Email Configuration", description = "Configure requirements for DPSS Custom Form Emailer to run")
	@interface Config {

		@AttributeDefinition(name = "List of paths under which servlet will reject requests.")
		String[] resource_blacklist() default "/content/usergenerated";

		@AttributeDefinition(name = "List of paths under which servlet will only accept requests.")
		String[] resource_whitelist() default "/content/dpss";

	}
	
	@Activate
    @Modified
    protected void activate(final Config config) {
		this.config = config; 
	}
	
	@Override
	/**
	 * This Servlet accepts whitelisted paths and rejects blacklisted paths
	 */
	public boolean accepts(SlingHttpServletRequest request) {
		boolean acceptable = EXTENSION.equals(request.getRequestPathInfo().getExtension());

        if (!acceptable) {
            return acceptable;
        }
        final Resource resource = request.getResource();
        LOGGER.debug("Checking for acceptance of resource {} ", resource.getPath());

        for(String path : config.resource_blacklist()) {
            if (resource.getPath().startsWith(path)) {
                return false;
            }
        }
        for(String path : config.resource_whitelist()) {
            if (resource.getPath().startsWith(path)) {
                return true;
            }
        }
        return false;
	}
	
	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response) throws IOException {

		final Resource formResource = request.getResource();
		if (ResourceUtil.isNonExistingResource(formResource)) {
			LOGGER.debug("Received fake request!");
			response.setStatus(500);
			return;
		}
		int status = 200;
		String[] mailTo = null; 
		CustomFormContainerModel containerModel = request.adaptTo(CustomFormContainerModel.class);
		if(containerModel == null) {
			LOGGER.error("Unable to adapt request to component model"); 
			status = 500;
		}
		else if(containerModel.getMailto() == null) {
			LOGGER.error("Expected Mail To property is not assigned."); 
			status = 500;
		}
		else {
			mailTo= containerModel.getMailto().split(",");
		}
		
		
		if (mailTo == null || mailTo.length == 0 || mailTo[0].length() == 0) {
			LOGGER.error("The mailto configuration is missing in the form resource at {}", formResource.getPath());
			status = 500;
		} 
		else if (emailService == null) {
			LOGGER.error("The mail service is currently not available! Unable to send form mail.");
			status = 500;
		} else {

			Map<String, String> emailParams = new HashMap<>();
			emailParams.put(SENDER_EMAIL_ADDRESS_PROPERTY,containerModel.getFrom());  
			emailParams.put(SENDER_NAME_PROPERTY,containerModel.getSenderName() == null ? containerModel.getFrom(): 
				containerModel.getSenderName());
			if(containerModel.getCc() != null) {
				final String[] ccMailTo = containerModel.getCc().split(",");
				emailParams.put(CC_PROPERTY, ccMailTo[0]);
			}
			emailParams.put(SUBJECT_PROPERTY,containerModel.getSubject());  
			

			
			// let's get all parameters first and sort them alphabetically
            final List<String> contentNamesList = new ArrayList<>();
            final Iterator<String> names = FormsHelper.getContentRequestParameterNames(request);
            while (names.hasNext()) {
                final String name = names.next();
                contentNamesList.add(name);
            }
            Collections.sort(contentNamesList);

            final List<String> namesList = new ArrayList<>();
            final Iterable<Resource> formElements = formStructureHelperFactory.
                    getFormStructureHelper(formResource).getFormElements(formResource);
            for (Resource field : formElements) {
                final FieldDescription[] descs = FieldHelper.getFieldDescriptions(request, field);
                for (final FieldDescription desc : descs) {
                    // remove from content names list
                    contentNamesList.remove(desc.getName());
                    if (!desc.isPrivate()) {
                        namesList.add(desc.getName());
                    }
                }
            }
            namesList.addAll(contentNamesList);
            
            StringBuilder message = new StringBuilder(); 
            // Add form fields to message
            for (final String name : namesList) {
                final RequestParameter rp = request.getRequestParameter(name);
                if (rp == null) {
                    //see Bug https://bugs.day.com/bugzilla/show_bug.cgi?id=35744
                    LOGGER.debug("skipping form element {} from mail content because it's not in the request", name);
                } else if (rp.isFormField()) {
                	
                	message.append(name);
                	message.append(" : \n");
                    
                    final String[] pValues = request.getParameterValues(name);
                    StringBuilder value = new StringBuilder(); 
                    for (final String v : pValues) {
                    	message.append(v);
                    	message.append("\n");
                    	value.append(v); 
                    }
                    message.append("\n");
                    emailParams.put(name, value.toString());
                } 
            }
			
            emailParams.put(FORM_FIELDS_ALL, message.toString());
            String url = populateExternalizedUrl(request);
            emailParams.put(REQUEST_URL, url);
			
			LOGGER.debug("Send form email to configured DPSS recipient with form values .. ");
			List<String> failureList = emailService.sendEmail(containerModel.getFormSubmittedValuesNotification(),
					emailParams, mailTo);
			if (!failureList.isEmpty()) {
				LOGGER.debug("Failure to send form notification to configured emails in form container component {}", failureList);
			} else {
				LOGGER.debug("Email successfully sent for Form Submission");
			}
			LOGGER.debug("Send form email confirmation to form submitter .. ");
			if (emailParams.get(SUBMITTER_EMAIL_PARAMETER) == null
					|| emailParams.get(SUBMITTER_EMAIL_PARAMETER).length() == 0) {
				LOGGER.debug("Form was submitted without user email address or in an unexpected form field. Please verify the authored"
						+ " form element for the users email has an element name of " + SUBMITTER_EMAIL_PARAMETER); 
			} else {
				List<String> failureListFormSumbitter = emailService.sendEmail(
						containerModel.getFormSubmittedUserConfirmation(), emailParams,
						emailParams.get(SUBMITTER_EMAIL_PARAMETER));
				if (!failureListFormSumbitter.isEmpty()) {
					LOGGER.debug("Failure to send form submission confirmation to user at address: {}",
							failureListFormSumbitter);
				} else {
					LOGGER.debug("Email successfully sent for Form Sumbission Confirmation");
				}
			}
		}

		// check for redirect
		String redirectTo = request.getParameter(":redirect");
		if (redirectTo != null) {
			if (AuthUtil.isRedirectValid(request, redirectTo) || redirectTo.equals(FormsHelper.getReferrer(request))) {
				int pos = redirectTo.indexOf('?');
				redirectTo = redirectTo + (pos == -1 ? '?' : '&') + "status=" + status;
				response.sendRedirect(redirectTo);
			} else {
				LOGGER.error("Invalid redirect specified: {}",  redirectTo);
				response.sendError(403);
			}
			return;
		}
		if (FormsHelper.isRedirectToReferrer(request)) {
			FormsHelper.redirectToReferrer(request, response,
					Collections.singletonMap("stats", new String[] { String.valueOf(status) }));
			return;
		}
		response.setStatus(status);

	}
	
	private String populateExternalizedUrl(final SlingHttpServletRequest request) {
		final ResourceResolver resourceResolver = request.getResourceResolver();
		PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
		if(pageManager == null) {
			return request.getRequestURL().toString();
		}
		Page page = pageManager.getContainingPage(request.getResource());
		final Externalizer externalizer = resourceResolver.adaptTo(Externalizer.class);
		String runMode = PageMetaDataUtils.getCurrentRunmode(slingSettingsService);
		runMode = StringUtils.isNotBlank(runMode) ? runMode : Externalizer.PUBLISH;
		
		String externalizedURL = null; 
		String path = page.getPath();
		if (null != externalizer && !path.isEmpty()) {
			externalizedURL = externalizer.externalLink(resourceResolver, runMode, resourceResolver.map(path));
		}
		return externalizedURL; 
	}
}
