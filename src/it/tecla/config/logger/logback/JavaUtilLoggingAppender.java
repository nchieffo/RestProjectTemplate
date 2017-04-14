package it.tecla.config.logger.logback;

import java.util.logging.Level;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class JavaUtilLoggingAppender<E extends ILoggingEvent> extends AppenderBase<E> {

	protected String pattern;
	protected PatternLayout patternLayout = new PatternLayout();
	
	@Override
	protected void append(E event) {
		
		java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger(event.getLoggerName());
		java.util.logging.Level julLevel = toJavaLevel(event.getLevel());
		boolean isLoggable = julLogger.isLoggable(julLevel);
		
		if (isLoggable) {
			PatternLayout patternLayout = new PatternLayout();
			patternLayout.setContext(this.context);
			patternLayout.setPattern(getPattern());
			patternLayout.start();
			String message = patternLayout.doLayout(event);
			patternLayout.stop();
			julLogger.log(julLevel, message);
		}
		
	}
	
	public String getPattern() {
		return pattern;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	private Level toJavaLevel(ch.qos.logback.classic.Level level) {
		if (level.equals(ch.qos.logback.classic.Level.ERROR)) {
			return java.util.logging.Level.SEVERE;
		} else if (level.equals(ch.qos.logback.classic.Level.WARN)) {
			return java.util.logging.Level.WARNING;
		} else if (level.equals(ch.qos.logback.classic.Level.INFO)) {
			return java.util.logging.Level.INFO;
		} else if (level.equals(ch.qos.logback.classic.Level.DEBUG)) {
			return java.util.logging.Level.FINE;
		} else if (level.equals(ch.qos.logback.classic.Level.TRACE)) {
			return java.util.logging.Level.FINEST;
		} else if (level.equals(ch.qos.logback.classic.Level.ALL)) {
			return java.util.logging.Level.ALL;
		} else if (level.equals(ch.qos.logback.classic.Level.OFF)) {
			return java.util.logging.Level.OFF;
		}
		
		return null;
	}

}
