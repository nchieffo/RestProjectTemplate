package it.tecla.config.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ErrorHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlerServlet.class);
	private static final Marker UNCAUGHT_MARKER = MarkerFactory.getMarker("UNCAUGHT");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Throwable t = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		
		String requestData = getRequestData(t, statusCode, servletName, request);

		LOGGER.error(UNCAUGHT_MARKER, "uncaught exception\n" + requestData, t);

		response.setContentType("text/plain");

		PrintWriter out = response.getWriter();
		try {
			out.append(requestData);
			out.append(ExceptionUtils.getStackTrace(t));
		} finally {
			out.close();
		}
	}
	
	public static String getRequestData(Throwable t, Integer statusCode, String servletName, HttpServletRequest request) {
		
		String logMessage = MDC.get("req.logMessage");

		StringBuilder sb = new StringBuilder();

		sb.append(new Date().toString() + "\n");
		sb.append("\n");
		sb.append("Servlet Name: " + servletName + "\n");
		sb.append("Status Code: " + statusCode + "\n");
		sb.append("Exception Name: " + t.getClass().getName() + "\n");
		sb.append(logMessage);
		sb.append("\n");
		
		return sb.toString();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}
