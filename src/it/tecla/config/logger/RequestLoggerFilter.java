package it.tecla.config.logger;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class RequestLoggerFilter implements Filter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggerFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			
			MDC.put("req.uuid", UUID.randomUUID().toString());
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

			String requestLogMessage = getRequestLogMessage();
			MDC.put("req.logMessage", requestLogMessage);
			
			LOGGER.debug(requestLogMessage.toString());
		}
		
		try {
			chain.doFilter(request, response);
		} finally {
			MDC.remove("req.uuid");
			MDC.remove("req.method");
			MDC.remove("req.requestURI");
			MDC.remove("req.queryString");
			MDC.remove("req.user");
			MDC.remove("req.accept");
			MDC.remove("req.referer");
		}
	}

	@Override
	public void destroy() {
		
	}
	
	protected String getRequestLogMessage() {

		String method = MDC.get("req.method");
		String requestURI = MDC.get("req.requestURI");
		String queryString = MDC.get("req.queryString");
		String uuid = MDC.get("req.uuid");
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
		logMessage.append("Request UUID = ");
		logMessage.append(uuid);

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

}
