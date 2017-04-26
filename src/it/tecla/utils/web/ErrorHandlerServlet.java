package it.tecla.utils.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorHandlerServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Throwable t = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		
		String requestData = getRequestData(t, statusCode, servletName, request);
		
		LOGGER.error("Uncaught exception while executing request {}", requestData, t);

		response.setContentType("text/plain");

		OutputStream os = response.getOutputStream();
		try {
			IOUtils.write(requestData, os, "UTF-8");
			IOUtils.write(ExceptionUtils.getStackTrace(t), os, "UTF-8");
		} finally {
			os.flush();
			os.close();
			response.flushBuffer();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String getRequestData(Throwable t, Integer statusCode, String servletName, HttpServletRequest request) {
		
		Map<String, String> reqMdc = (Map<String, String>) request.getAttribute("reqMDC");
		
		String logMessage = null;
		if (reqMdc != null) {
			logMessage = reqMdc.get("req.logMessage");
		}

		StringBuilder sb = new StringBuilder();

		sb.append(new Date().toString() + "\n");
		sb.append("\n");
		sb.append("Server Info: " + WebAppDataListener.getCellName() + "/" + WebAppDataListener.getNodeName() + "/" + WebAppDataListener.getServerName() + "\n");
		sb.append("Package Info: " + WebAppDataListener.getEarName() + "/" + WebAppDataListener.getWarName() + "\n");
		sb.append("Servlet Name: " + servletName + "\n");
		sb.append("Status Code: " + statusCode + "\n");
		sb.append("Exception Name: " + t.getClass().getName() + "\n");
		if (logMessage != null) {
			sb.append(logMessage);
		}
		sb.append("\n\n");
		
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
