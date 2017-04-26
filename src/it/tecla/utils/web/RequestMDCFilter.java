package it.tecla.utils.web;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import it.tecla.utils.logger.LoggedInterceptor;

public class RequestMDCFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestMDCFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
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
			
			WebAppDataListener.setupMDC();
			
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
			
			// per loggare la request/response devo avere un wrapper che mi salva il body in modo tale che possa poi essere riletto normalmente
			MultiReadHttpServletRequest multiReadRequest = new MultiReadHttpServletRequest(httpServletRequest);
			MultiReadHttpServletResponse multiReadResponse = new MultiReadHttpServletResponse((HttpServletResponse) response);
			request = multiReadRequest;
			response = multiReadResponse;
			
			try {
				String body = multiReadRequest.getCopiedInput();
				MDC.put("req.body", body);
			} catch (Throwable t) {
				LOGGER.warn("Error while copying the request input", t);
			}

			String requestLogMessage = getRequestLogMessage();
			MDC.put("req.logMessage", requestLogMessage);

			request.setAttribute("reqMDC", MDC.getCopyOfContextMap());
		}
		
		try {
			chain.doFilter(request, response);
			
			if ("true".equals(MDC.get("resp.doLog"))) {
				if (response instanceof MultiReadHttpServletResponse) {
					MultiReadHttpServletResponse multiReadResponse = (MultiReadHttpServletResponse) response;
					try {
						String responseBody = multiReadResponse.getCopiedOutput();
						LoggedInterceptor.logResponse(responseBody);
					} catch (Throwable t) {
						LOGGER.warn("Error while copying the response output", t);
					}
				}
			}
			
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
	
	public static String getRequestLogMessage() {

		String method = MDC.get("req.method");
		String requestURI = MDC.get("req.requestURI");
		String queryString = MDC.get("req.queryString");
		String logId = MDC.get("req.logId");
		String user = MDC.get("req.user");
		String accept = MDC.get("req.accept");
		String referer = MDC.get("req.referer");
		String body = MDC.get("req.body");

		StringBuilder logMessage = new StringBuilder();
		
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
	
		if (body != null && !body.isEmpty()) {
			logMessage.append("\n");
			logMessage.append("Body = ");
			logMessage.append(body);
		}
		
		return logMessage.toString();
	}
	
	public static Cookie getCookie(HttpServletRequest request, String name) {
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
