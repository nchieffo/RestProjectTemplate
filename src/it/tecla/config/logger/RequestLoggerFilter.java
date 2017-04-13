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
			
			String uuid = UUID.randomUUID().toString();
			String user = httpServletRequest.getRemoteUser();
			
			StringBuilder logMessage = new StringBuilder();
			logMessage.append("\n");
			
			MDC.put("req.uuid", uuid);
			MDC.put("req.method", httpServletRequest.getMethod());
			MDC.put("req.requestURI", httpServletRequest.getRequestURI());
			
			logMessage.append(httpServletRequest.getMethod());
			logMessage.append(" ");
			logMessage.append(httpServletRequest.getRequestURI());
			
			if (httpServletRequest.getQueryString() != null) {
				MDC.put("req.queryString", httpServletRequest.getQueryString());
				logMessage.append("?");
				logMessage.append(httpServletRequest.getQueryString());
			}
			logMessage.append("\n");
			
			logMessage.append("Request UUID = ");
			logMessage.append(uuid);
			logMessage.append("\n");
			
			if (user != null) {
				MDC.put("req.user", user);
				logMessage.append("request user = ");
				logMessage.append(user);
				logMessage.append("\n");
			}
			String accept = httpServletRequest.getHeader("Accept");
			if (accept != null && !accept.startsWith("text/html")) {
				MDC.put("req.accept", accept);
				logMessage.append("Accept = ");
				logMessage.append(accept);
				logMessage.append("\n");
			}
			String referer = httpServletRequest.getHeader("Referer");
			if (referer != null) {
				MDC.put("req.referer", referer);
				logMessage.append("Referer = ");
				logMessage.append(referer);
				logMessage.append("\n");
			}
			
			LOGGER.debug(logMessage.toString());
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

}
