package com.adobe.aem.lacounty.dpss.core.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.lacounty.dpss.core.constants.Constants;

import net.fortuna.ical4j.data.FoldingWriter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;

@Component(service = Servlet.class, property = { "sling.servlet.selectors=" + ExportEventServlet.DEFAULT_SELECTOR,
		"sling.servlet.resourceTypes=cq/Page", "sling.servlet.extensions=html", "sling.servlet.methods=GET" })
public class ExportEventServlet extends SlingSafeMethodsServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ExportEventServlet.class);
	protected static final String DEFAULT_SELECTOR = "exportics";
	protected static final String LOCATION_STRING = "location";
	protected static final String CONTENT_TYPE_CALENDAR = "text/calendar";
	protected static final String CALENDAR_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	protected static final String PROD_ID_STRING = "-//DPSS//Calendar//EN";

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
			handleQuery(request, response);
	}

	private void handleQuery(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

		String eventName = String.valueOf(request.getRequestParameter(Constants.PN_EVENT_TITLE));
		String startDate = String.valueOf(request.getRequestParameter(Constants.START_DATE));
		String endDate = String.valueOf(request.getRequestParameter(Constants.END_DATE));
		String location = String.valueOf(request.getRequestParameter(LOCATION_STRING));

		response.setContentType(CONTENT_TYPE_CALENDAR);
		response.setCharacterEncoding(Constants.UTF8_ENCODING);
		response.setHeader("Content-Disposition", "attachment;filename=" + eventName.replace(" ", "_") + ".ics");

		FoldingWriter foldingWriter = new FoldingWriter(response.getWriter(), FoldingWriter.REDUCED_FOLD_LENGTH);

		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		TimeZone timezone = registry.getTimeZone(Constants.TIMEZONE_PST);

		SimpleDateFormat sdf = new SimpleDateFormat(CALENDAR_FORMAT);
		sdf.setTimeZone(timezone);
		DateTime startDt;
		try {
			startDt = new DateTime(sdf.parse(startDate));

			DateTime endDt = new DateTime(sdf.parse(endDate));
			startDt.setTimeZone(timezone);
			endDt.setTimeZone(timezone);

			VEvent event = new VEvent(startDt, endDt, eventName);
			event.getProperties().add(new Location(location));
			event.getStartDate().setTimeZone(timezone);
			event.getEndDate().setTimeZone(timezone);

			Calendar iCalendar = new Calendar();
			iCalendar.getProperties().add(new ProdId(PROD_ID_STRING));
			iCalendar.getProperties().add(CalScale.GREGORIAN);
			iCalendar.getComponents().add(event);

			foldingWriter.write(iCalendar.toString());
		} catch (ParseException e) {
			LOG.error("Exception in handleQuery {}", e.getMessage(), e);
		} finally {
			foldingWriter.close();
		}
	}
}