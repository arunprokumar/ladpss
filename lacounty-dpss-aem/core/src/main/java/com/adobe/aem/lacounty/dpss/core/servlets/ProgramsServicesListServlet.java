
package com.adobe.aem.lacounty.dpss.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.beans.ProgramsServicesBean;
import com.adobe.aem.lacounty.dpss.core.utils.ComponentUtils;
import com.adobe.aem.lacounty.dpss.core.utils.PageMetaDataUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.google.gson.Gson;


@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "=Programs ServicesList Servlet",
		"sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.resourceTypes=" + "cq/Page",
		"sling.servlet.selectors=" + "prgm-srvs-list",
		"sling.servlet.extensions=" + "json" })
public class ProgramsServicesListServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 2L;
	private static final Logger LOG = LoggerFactory.getLogger(ProgramsServicesListServlet.class);
	private static final String ROOTPAGE_PATH_QUERYPARAMETER = "rootPagePath";
	private String programsServicesListJson=StringUtils.EMPTY;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
			throws ServletException, IOException {
		if (null != request && null != request.getParameter(ROOTPAGE_PATH_QUERYPARAMETER)) {
			String rootPagePath= request.getParameter(ROOTPAGE_PATH_QUERYPARAMETER);
			if(StringUtils.isNotBlank(rootPagePath) && StringUtils.isNotEmpty(rootPagePath)) {
			createListJson(rootPagePath,request);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter printWriter = response.getWriter();
			printWriter.write(programsServicesListJson);
			printWriter.flush();
			}
			LOG.debug("ProgramsServicesListServlet sucessfully executated");
		}
	}

	private void createListJson(String rootPagePath, SlingHttpServletRequest req) {
		if (StringUtils.isNotEmpty(rootPagePath)) {
			ResourceResolver rsourceResolver = req.getResourceResolver();
			Resource rootPageResource = rsourceResolver.getResource(rootPagePath);
			if (null != rootPageResource) {
				Page rootPage = rootPageResource.adaptTo(Page.class);
				if (null != rootPage) {
					populateProgramsServicesDetails(rootPage, req);
				}
			}
		}
	}

	private void populateProgramsServicesDetails(Page rootPage,SlingHttpServletRequest req) {
		Iterator<Page> programsServicesPages = rootPage.listChildren(new PageFilter(),true);
		List<ProgramsServicesBean> programsServicesList = new ArrayList<>();
		while (programsServicesPages.hasNext()) {
			Page programsServicesPage = programsServicesPages.next();
			if (!programsServicesPage.isHideInNav() && PageMetaDataUtils.isProgrameOrServicesPage(programsServicesPage)) {
				ProgramsServicesBean programsServicesBean = new ProgramsServicesBean();
				if (StringUtils.isNotEmpty(programsServicesPage.getPageTitle())) {
					programsServicesBean.setTitle(programsServicesPage.getPageTitle());
				} else {
					programsServicesBean.setTitle(programsServicesPage.getTitle());
				}
				programsServicesBean.setDescription(programsServicesPage.getDescription());
				programsServicesBean.setLink(ComponentUtils.getURL(req,programsServicesPage.getPageManager(),programsServicesPage.getPath()));
				programsServicesList.add(programsServicesBean);
				Collections.sort(programsServicesList);				

			}

		}
		Gson gson = new Gson();
		programsServicesListJson = gson.toJson(programsServicesList);
	}
}
