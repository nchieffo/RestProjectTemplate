package it.tecla.utils.web;

import java.io.IOException;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class RequestMDCFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMDCFilter.class);

	public static final Marker RESPONSE = MarkerFactory.getMarker("FLOW.RESPONSE");
	
	private String earName;
	private String warName;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		try {
			earName = (String) InitialContext.doLookup("java:app/AppName");
			warName = (String) InitialContext.doLookup("java:module/ModuleName");
			
			LOGGER.info("earName: {}", earName);
			LOGGER.info("warName: {}", warName);
			
		} catch (NamingException ex) {
			throw new RuntimeException(ex);
		}
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			
			String reqLogId = request.getParameter("req.logId");
			if (reqLogId == null) {
				Cookie cookie = getCookie(httpServletRequest, "req.logId");
				if (cookie != null) {
					reqLogId = cookie.getValue();
				}
			}
			if (reqLogId == null) {
				reqLogId = UUID.randomUUID().toString();
			}
			
			MDC.put("earName", earName);
			MDC.put("warName", warName);
			
			MDC.put("req.logId", reqLogId);
			MDC.put("req.method", httpServletRequest.getMethod());
			MDC.put("req.requestURI", httpServletRequest.getRequestURI());
			
			if (httpServletRequest.getQueryString() != null) {
				MDC.put("req.queryString", httpServletRequest.getQueryString());
			}
			
			if (httpServletRequest.getRemoteUser() != null) {
				MDC.put("req.user", httpServletRequest.getRemoteUser());
			}
			String accept = httpServletRequest.getHeader("Accept");
			if (accept != null && !accept.startsWith("text/html")) {
				MDC.put("req.accept", accept);
			}
			String referer = httpServletRequest.getHeader("Referer");
			if (referer != null) {
				MDC.put("req.referer", referer);
			}
			
			// TODO loggare il request body

			String requestLogMessage = getRequestLogMessage();
			MDC.put("req.logMessage", requestLogMessage);

			request.setAttribute("reqMDC", MDC.getCopyOfContextMap());
		}
		
		try {
			chain.doFilter(request, response);
			
			if ("true".equals(MDC.get("resp.doLog"))) {
				Logger logger = LoggerFactory.getLogger(MDC.get("resp.doLog.logger"));
				
				// TODO loggare la response
				logger.trace(RESPONSE, "Response {}", response);
			}
			
		} catch (Throwable t) {
			
			String requestLogMessage = MDC.get("req.logMessage");
			
			LOGGER.error("Uncaught exception while executing request {}", requestLogMessage, t);
			
			throw new ServletException(t);
			
		} finally {
			MDC.remove("req.logId");
			MDC.remove("req.method");
			MDC.remove("req.requestURI");
			MDC.remove("req.queryString");
			MDC.remove("req.user");
			MDC.remove("req.accept");
			MDC.remove("req.referer");
			MDC.remove("req.logMessage");
			MDC.remove("resp.doLog");
			MDC.remove("resp.doLog.logger");
		}
	}

	@Override
	public void destroy() {
		
	}
	
	protected String getRequestLogMessage() {

		String method = MDC.get("req.method");
		String requestURI = MDC.get("req.requestURI");
		String queryString = MDC.get("req.queryString");
		String logId = MDC.get("req.logId");
		String user = MDC.get("req.user");
		String accept = MDC.get("req.accept");
		String referer = MDC.get("req.referer");

		StringBuilder logMessage = new StringBuilder();
		
		logMessage.append("\n");
		logMessage.append(method);
		logMessage.append(" ");
		logMessage.append(requestURI);
		
		if (queryString != null) {
			logMessage.append("?");
			logMessage.append(queryString);
		}

		logMessage.append("\n");
		logMessage.append("Request log ID = ");
		logMessage.append(logId);

		if (user != null) {
			logMessage.append("\n");
			logMessage.append("Request user = ");
			logMessage.append(user);
		}
		
		if (accept != null) {
			logMessage.append("\n");
			logMessage.append("Accept = ");
			logMessage.append(accept);
		}
	
		if (referer != null) {
			logMessage.append("\n");
			logMessage.append("Referer = ");
			logMessage.append(referer);
		}
		
		return logMessage.toString();
	}
	
	protected Cookie getCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return cookie;
				}
			}
		}
		return null;
	}

}
