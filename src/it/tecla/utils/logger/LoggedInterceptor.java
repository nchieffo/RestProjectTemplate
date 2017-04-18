package it.tecla.utils.logger;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

@Logged
@Interceptor
public class LoggedInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Marker REQUEST = MarkerFactory.getMarker("FLOW.REQUEST");
	private static final Marker ENTER = MarkerFactory.getMarker("FLOW.ENTER");
	private static final Marker EXIT = MarkerFactory.getMarker("FLOW.EXIT");
	private static final Marker DURATION = MarkerFactory.getMarker("FLOW.DURATION");
	
	private static final CachingParanamer PARANAMER = new CachingParanamer(new BytecodeReadingParanamer());
	
	@AroundInvoke
	public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
		
		Method method = invocationContext.getMethod();
		Logger logger;
		
		boolean isRestService = false;
		
		if (method != null) {
			Class<?> beanClass = method.getDeclaringClass();
			logger = LoggerFactory.getLogger(beanClass);
			
			// vedere se il metodo ha la annotation @Path
			if (method.getAnnotation(GET.class) != null || 
					method.getAnnotation(POST.class) != null|| 
					method.getAnnotation(PUT.class) != null|| 
					method.getAnnotation(DELETE.class) != null|| 
					method.getAnnotation(OPTIONS.class) != null) {
				
				isRestService = true;
			}
		} else {
			Class<?> beanClass = invocationContext.getTarget().getClass();
			logger = LoggerFactory.getLogger(beanClass);
			
			if (beanClass.getAnnotation(Path.class) != null) {
				isRestService = true;
			}
		}
		
		String message = null;
		long startTime = 0;
		
		if (logger.isTraceEnabled()) {
			
			if (isRestService) {
				// loggo la request
				logger.trace(REQUEST, "Request {}", MDC.get("req.logMessage"));
			}
			
			StringBuilder sb = new StringBuilder();
			
			if (method == null) {
				sb.append("unknown method ");
				sb.append(invocationContext.getTarget().getClass().getName());
			} else {
				sb.append(method.getDeclaringClass().getName());
				sb.append(".");
				sb.append(method.getName());
			}
			
			String[] parameterNames = PARANAMER.lookupParameterNames(method);
			
			sb.append("(");
			for (int i=0; i<invocationContext.getParameters().length; i++) {
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(parameterNames[i]);
				sb.append("=");
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
			
			if (isRestService) {
				// TODO lascio il mark per loggare anche la response sulla MDC
				MDC.put("resp.doLog", "true");
			}
		}
		
		return result;
	}

}
