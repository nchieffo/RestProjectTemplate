package it.tecla.examples.model;

import io.swagger.annotations.Api;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Api
@Path("/model")
@Produces("application/json")
public class ModelRestService {

	@GET
	@Path("/example")
	public Model get() {
		
		Model model = new Model();
		model.setBooleanValue(true);
		model.setDateValue(new Date());
		model.setDoubleValue(1.1);
		model.setFloatValue(1.1f);
		model.setIntValue(1);
		model.setStringValue("string");

		return model;
		
	}
	
}
