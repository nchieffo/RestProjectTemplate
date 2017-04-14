package it.tecla.utils.logger;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@Logged
@Interceptor
public class LoggedInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Marker ENTER = MarkerFactory.getMarker("FLOW.ENTER");
	private static final Marker EXIT = MarkerFactory.getMarker("FLOW.EXIT");
	private static final Marker DURATION = MarkerFactory.getMarker("FLOW.DURATION");
	
	@AroundInvoke
	public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
		
		Method method = invocationContext.getMethod();
		Logger logger;
		
		if (method != null) {
			logger = LoggerFactory.getLogger(method.getDeclaringClass());
		} else {
			logger = LoggerFactory.getLogger(invocationContext.getTarget().getClass());
		}
		
		String message = null;
		long startTime = 0;
		
		if (logger.isTraceEnabled()) {
			StringBuilder sb = new StringBuilder();
			
			if (method == null) {
				sb.append("new ");
				sb.append(invocationContext.getTarget().getClass().getName());
			} else {
				sb.append(method.getDeclaringClass().getName());
				sb.append(".");
				sb.append(method.getName());
			}
			
			sb.append("(");
			for (int i=0; i<invocationContext.getParameters().length; i++) {
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(invocationContext.getParameters()[i]);
			}
			sb.append(")");
			
			message = sb.toString();
			logger.trace(ENTER, "Enter {}", message);
			
			startTime = System.currentTimeMillis();
		}

		Object result = invocationContext.proceed();
		
		if (message != null) {
			long elapsed = System.currentTimeMillis() - startTime;
			String duration = DurationFormatUtils.formatDuration(elapsed, "s's' S'ms'");
			logger.trace(DURATION, "Duration {}: {}", message, duration);
			logger.trace(EXIT, "Exit {}: {}", message, result);
		}
		
		return result;
	}

}
