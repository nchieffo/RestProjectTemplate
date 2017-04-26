package it.tecla.utils.web;

import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class WebAppDataListener implements ServletContextListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WebAppDataListener.class);
	
	private static String earName;
	private static String warName;
	private static String contextPath;
	private static String cellName;
	private static String nodeName;
	private static String serverName;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			earName = (String) InitialContext.doLookup("java:app/AppName");
			MDC.put("earName", earName);
			
			warName = (String) InitialContext.doLookup("java:module/ModuleName");
			MDC.put("warName", warName);
			
			contextPath = arg0.getServletContext().getContextPath();
			MDC.put("contextPath", contextPath);
			
			LOGGER.info("earName: {}", earName);
			LOGGER.info("warName: {}", warName);
			LOGGER.info("contextPath: {}", contextPath);
			
		} catch (Throwable t) {
			LOGGER.error("Error during RequestMDCFilter initialization", t);
		}
		
		try {
			cellName = (String) InitialContext.doLookup("thisNode/cell/cellname");
			MDC.put("cellName", cellName);
			
			nodeName = (String) InitialContext.doLookup("thisNode/nodename");
			MDC.put("nodeName", nodeName);
			
			serverName = (String) InitialContext.doLookup("servername");
			MDC.put("serverName", serverName);

			LOGGER.info("cellName: {}", cellName);
			LOGGER.info("nodeName: {}", nodeName);
			LOGGER.info("serverName: {}", serverName);
			
		} catch (Throwable t) {
			// Liberty non ha questi valori di default, per cui informo che vanno creati
			LOGGER.error("Error during RequestMDCFilter initialization.\n"
					+ "If you are running in WebSphere Liberty please add the following values to your server.xml\n"
					+ "<jndiEntry value=\"yourCellName\" jndiName=\"thisNode/cell/cellname\" id=\"cellname\"></jndiEntry>\n"
					+ "<jndiEntry value=\"yourServerName\" jndiName=\"thisNode/nodename\" id=\"nodename\"></jndiEntry>", t);
		}
	}
	
	public static void setupMDC() {
		MDC.put("earName", earName);
		MDC.put("warName", warName);
		MDC.put("contextPath", contextPath);
		MDC.put("cellName", cellName);
		MDC.put("nodeName", nodeName);
		MDC.put("serverName", serverName);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	public static String getEarName() {
		return earName;
	}

	public static String getWarName() {
		return warName;
	}

	public static String getContextPath() {
		return contextPath;
	}

	public static String getNodeName() {
		return nodeName;
	}

	public static String getCellName() {
		return cellName;
	}
	
	public static String getServerName() {
		return serverName;
	}

}
