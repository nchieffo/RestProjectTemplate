package it.tecla.examples.logging;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import io.swagger.annotations.Api;
import it.tecla.utils.logger.Logged;

@Api("Logger")
@Path("/logger")
@Produces("application/json")
@Logged(includeRequest=true)
public class LoggerRestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerRestService.class);
	
	@GET
	@Path("/message")
	public String message(@QueryParam("message") String message, @QueryParam("message2") String message2) {
		LOGGER.debug("LoggerRestService.message -> {} {}", message, message2);
		return "OK";
	}

	@GET
	@Path("/debug")
	public String debug() {
		LOGGER.debug("LoggerRestService.debug -> {} {} {} {}", 10, 20, 30, 40);
		return "OK";
	}

	@GET
	@Path("/info")
	public String info() {
		LOGGER.info("LoggerRestService.info -> {} {} {} {}", 10, 20, 30, 40);
		return "OK";
	}

	@GET
	@Path("/warn")
	public String warn() {
		LOGGER.warn("LoggerRestService.warn -> {} {} {} {}", 10, 20, 30, 40);
		return "OK";
	}

	@GET
	@Path("/error")
	public String error() {
		LOGGER.error("LoggerRestService.error -> {}", 10, new Exception("my exception"));
		return "OK";
	}

	@GET
	@Path("/all")
	public String all() {
		debug();
		info();
		warn();
		error();
		return "OK";
	}
	
	@GET
	@Path("/trace.log")
	@Produces("text/plain")
	@Logged(includeEntry=false, includeExit=false)
	public String traceLog() throws IOException {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger logger = lc.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
		FileAppender<ILoggingEvent> appender = (FileAppender<ILoggingEvent>) logger.getAppender("TRACE_FILE");
		String fileName = appender.getFile();
		
		String logContent = FileUtils.readFileToString(FileUtils.getFile(fileName), "UTF-8");
		
		return logContent;
	}
	
}
