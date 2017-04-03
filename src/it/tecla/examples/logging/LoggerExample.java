package it.tecla.examples.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerExample {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerExample.class);
	
	public static void main(String[] args) {
	
		LOGGER.info("test {} {} {} {}", 1, 2, 3, 4);
		
	}
	
}
