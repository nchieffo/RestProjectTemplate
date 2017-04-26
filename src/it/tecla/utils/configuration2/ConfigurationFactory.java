package it.tecla.utils.configuration2;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import javax.enterprise.inject.Produces;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigurationFactory {
	
	@Produces
	public static ConfigurationBuilder<Configuration> createConfigurationBuilder() {
		return createConfigurationBuilderFromResourcePath("/application.properties");
	}

	@SuppressWarnings("unchecked")
	public static <T extends Configuration> ConfigurationBuilder<T> createConfigurationBuilderFromResourcePath(String path) {

		try {
			URL url = ConfigurationFactory.class.getResource(path);
			
			if (url == null) {
				throw new IllegalArgumentException("ConfigurationFactory: cant load " + path);
			}
			
			String absolutePath = new File(url.toURI()).getAbsolutePath();
			
			Parameters params = new Parameters();
			FileBasedConfigurationBuilder<PropertiesConfiguration> builder = new FileBasedConfigurationBuilder<PropertiesConfiguration>(PropertiesConfiguration.class);
			
			builder.configure(params.properties().setFileName(absolutePath));
			
			return (ConfigurationBuilder<T>) builder;
			
		} catch (URISyntaxException ex) {
			throw new RuntimeException(ex);
		}

	}
	
	@Produces
	public static Configuration createConfiguration() {
		return createConfigurationFromResourcePath("/application.properties");
	}

	public static Configuration createConfigurationFromResourcePath(String path) {

		try {
			return createConfigurationBuilderFromResourcePath(path).getConfiguration();
		} catch (ConfigurationException ex) {
			throw new RuntimeException(ex);
		}

	}

}
