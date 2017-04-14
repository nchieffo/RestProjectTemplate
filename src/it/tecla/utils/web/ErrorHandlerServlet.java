package it.tecla.utils.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ErrorHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Throwable t = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		
		String requestData = getRequestData(t, statusCode, servletName, request);

		response.setContentType("text/plain");

		PrintWriter out = response.getWriter();
		try {
			out.append(requestData);
			out.append(ExceptionUtils.getStackTrace(t));
		} finally {
			out.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String getRequestData(Throwable t, Integer statusCode, String servletName, HttpServletRequest request) {
		
		Map<String, String> reqMdc = (Map<String, String>) request.getAttribute("reqMDC");
		String logMessage = reqMdc.get("req.logMessage");

		StringBuilder sb = new StringBuilder();

		sb.append(new Date().toString() + "\n");
		sb.append("\n");
		sb.append("Servlet Name: " + servletName + "\n");
		sb.append("Status Code: " + statusCode + "\n");
		sb.append("Exception Name: " + t.getClass().getName() + "\n");
		sb.append(logMessage);
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
