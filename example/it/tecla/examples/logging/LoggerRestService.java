package it.tecla.examples.logging;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import it.tecla.utils.logger.Logged;

@Api("Logger")
@Path("/logger")
@Produces("application/json")
@Logged
public class LoggerRestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerRestService.class);
	
	@GET
	@Path("/message")
	public String message(@QueryParam("message") String message) {
		LOGGER.debug("LoggerRestService.message -> {}", message);
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
	
}
