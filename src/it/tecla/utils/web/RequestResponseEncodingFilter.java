package it.tecla.utils.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class RequestResponseEncodingFilter implements Filter {

	protected String encoding;

	@Override
	public void init(FilterConfig config) throws ServletException {

		encoding = config.getInitParameter("characterEncoding");

		if (encoding == null) {
			encoding = "UTF-8";
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (response instanceof HttpServletResponse) {
			
			// Respect the client-specified character encoding
			// (see HTTP specification section 3.4.1)
			if (request.getCharacterEncoding() == null) {
				request.setCharacterEncoding(encoding);
			}
			
			HttpServletResponse wrappedResponse = new HttpServletResponseWrapper((HttpServletResponse) response) {
	
				@Override
				public void setContentType(String contentType) {
					if (contentType != null && contentType.toLowerCase().indexOf("charset=") < 0) {
						contentType += "; charset=" + encoding;
					}
					super.setContentType(contentType);
				}
	
			};
	
			chain.doFilter(request, wrappedResponse);
			
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		
	}

}
