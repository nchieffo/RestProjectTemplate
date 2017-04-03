package it.tecla.config.rest;

import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@Provider
@Consumes({"application/json", "text/json"})
@Produces({"application/json", "text/json"})
public class JaxRsJacksonProvider extends JacksonJsonProvider {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SZ";

	public JaxRsJacksonProvider() {
		
		// READER
		configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		configure(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING, true);
		
		// WRITER
		configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
		configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, true);
		configure(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING, true);
		configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		
		// TODO sistemare con il formato data di default per swagger
		locateMapper(null, null).getSerializationConfig().setDateFormat(new SimpleDateFormat(DATE_FORMAT));
		locateMapper(null, null).getSerializationConfig().setSerializationInclusion(Inclusion.ALWAYS);
	}

}
