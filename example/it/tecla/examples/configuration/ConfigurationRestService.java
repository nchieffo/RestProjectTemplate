package it.tecla.examples.configuration;

import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationConverter;
import org.apache.commons.configuration2.ex.ConfigurationException;

import io.swagger.annotations.Api;
import it.tecla.utils.logger.Logged;

@Api("Configuration")
@Path("/configuration")
@Produces("application/json")
@Logged
public class ConfigurationRestService {
	
	@Inject
	Configuration conf;
	
	@Inject
	Instance<Configuration> confInstance;

	@GET
	@Path("/default")
	public Map<Object, Object> getDefaultConfiguration() {
		return ConfigurationConverter.getMap(conf);
	}

	@GET
	@Path("/default/key")
	public String getDefaultConfiguration(@QueryParam("key") String key) {
		return conf.getString(key);
	}

	@GET
	@Path("/default/instance")
	public Map<Object, Object> getDefaultConfigurationFromInstance() throws ConfigurationException {
		
		System.out.println(confInstance.get() == confInstance.get());
		
		return ConfigurationConverter.getMap(confInstance.get());
	}
	
}
