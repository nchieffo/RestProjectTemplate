package it.tecla.examples.model;

import io.swagger.annotations.Api;
import it.tecla.config.logger.Logged;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api("Model")
@Path("/model")
@Produces("application/json")
@Logged
public class ModelRestService {

	@GET
	@Path("/example")
	public Model example() {
		
		Model model = new Model();
		model.setBooleanValue(true);
		model.setDateValue(new Date());
		model.setDoubleValue(1.1);
		model.setFloatValue(1.1f);
		model.setIntValue(1);
		model.setStringValue("string");

		return model;
		
	}
	
	@GET
	@Path("/example2")
	public Model example2() {
		
		Model model = new Model();
		model.setBooleanValue(true);
		model.setDateValue(new Date());
		model.setDoubleValue(2.2);
		model.setFloatValue(2.2f);
		model.setIntValue(2);
		model.setStringValue("string2");

		return model;
		
	}
	
}
