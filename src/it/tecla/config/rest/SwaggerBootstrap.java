package it.tecla.config.rest;

import io.swagger.jaxrs.config.BeanConfig;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SwaggerBootstrap extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	private String contextPath;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		contextPath = config.getServletContext().getContextPath();
		
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion(config.getInitParameter("version"));
		beanConfig.setBasePath(contextPath + config.getInitParameter("basePath"));
		beanConfig.setResourcePackage(config.getInitParameter("resourcePackages"));
		beanConfig.setTitle(config.getInitParameter("title"));
		beanConfig.setDescription(config.getInitParameter("description"));
		beanConfig.setScan(true);
		beanConfig.setPrettyPrint(true);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String swaggerUiVersion = this.getInitParameter("swagger-ui.version");
		response.sendRedirect(contextPath + "/webjars/swagger-ui/" + swaggerUiVersion + "/?url=" + URLEncoder.encode(contextPath + "/api/swagger.json", "UTF-8"));
		
	}

}
