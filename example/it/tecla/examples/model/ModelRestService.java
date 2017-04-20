package it.tecla.examples.model;

import io.swagger.annotations.Api;
import it.tecla.utils.logger.Logged;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
	
	@POST
	@Path("/post/form")
	@Consumes("application/x-www-form-urlencoded")
	public Model postForm(@FormParam("booleanValue") boolean booleanValue,
			@FormParam("dateValue") Date dateValue,
			@FormParam("doubleValue") Double doubleValue,
			@FormParam("floatValue") Float floatValue,
			@FormParam("intValue") Integer intValue,
			@FormParam("stringValue") String stringValue
			) {
		
		Model model = new Model();
		model.setBooleanValue(booleanValue);
		model.setDateValue(dateValue);
		model.setDoubleValue(doubleValue);
		model.setFloatValue(floatValue);
		model.setIntValue(intValue);
		model.setStringValue(stringValue);
		
		return model;
	}
	
	@POST
	@Path("/post/json")
	@Consumes("application/json")
	public Model postJson(Model model) {
		return model;
	}
	
	@GET
	@Path("/exception")
	public void exception() {
		throw new RuntimeException("error");
	}
	
}
