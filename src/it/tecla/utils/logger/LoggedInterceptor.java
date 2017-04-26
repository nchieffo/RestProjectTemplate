package it.tecla.utils.logger;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

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

	public static final Marker REQUEST = MarkerFactory.getMarker("FLOW.REQUEST");
	public static final Marker RESPONSE = MarkerFactory.getMarker("FLOW.RESPONSE");
	public static final Marker ENTER = MarkerFactory.getMarker("FLOW.ENTER");
	public static final Marker EXIT = MarkerFactory.getMarker("FLOW.EXIT");
	public static final Marker DURATION = MarkerFactory.getMarker("FLOW.DURATION");
	
	private static final CachingParanamer PARANAMER = new CachingParanamer(new BytecodeReadingParanamer());
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggedInterceptor.class);
	
	private static Logged DEFAULT_LOGGED_INSTANCE = new Logged() {
		
		@Override
		public Class<? extends Annotation> annotationType() {
			return Logged.class;
		}
		
		@Override
		public boolean includeResponse() {
			return false;
		}
		
		@Override
		public boolean includeRequest() {
			return false;
		}
		
		@Override
		public boolean includeDuration() {
			return true;
		}
		
		@Override
		public boolean includeEntry() {
			return true;
		}
		
		@Override
		public boolean includeExit() {
			return true;
		}
	};
	
	@AroundInvoke
	public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
		
		Method method = null;
		Logger logger = null;
		Logged logged = null;
		
		String message = null;
		long startTime = 0;
		
		try {
			method = invocationContext.getMethod();
			logger = getLogger(invocationContext);
			logged = getLogged(invocationContext);
			
			if (logger.isTraceEnabled()) {
				
				if (logged.includeRequest()) {
					// loggo anche la request
					logger.trace(REQUEST, "Request:\n{}", MDC.get("req.logMessage"));
				}
				
				StringBuilder sb = new StringBuilder();
				
				if (method == null) {
					sb.append(invocationContext.getTarget().getClass().getName());
					sb.append(".<UNKNOWN_METHOD>");
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
				
				if (logged.includeEntry()) {
					logger.trace(ENTER, "Enter {}", message);
				}
				
				if (logged.includeDuration()) {
					startTime = System.currentTimeMillis();
				}
			}
		} catch (Throwable t) {
			LOGGER.warn("error in Logged Interceptor before invoking real method", t);
		}

		// invocazione del metodo reale
		Object result = invocationContext.proceed();
		
		try {
			if (message != null) {
				
				if (logged.includeDuration()) {
					long elapsed = System.currentTimeMillis() - startTime;
					String duration = DurationFormatUtils.formatDuration(elapsed, "s's' S'ms'");
					logger.trace(DURATION, "Duration {}: {}", message, duration);
				}
				
				if (logged.includeExit()) {
					logger.trace(EXIT, "Exit {}: {}", message, result);
				}
				
				if (logged.includeResponse()) {
					// in questo punto non ho ancora la response, per cui
					// lascio un flag che sarà letto dal RequestMDCFilter
					MDC.put("resp.doLog", "true");
					MDC.put("resp.doLog.logger", logger.getName());
				}
			}
		} catch (Throwable t) {
			LOGGER.warn("error in Logged Interceptor after invoking real method", t);
		}
		
		return result;
	}
	
	public static void logResponse(String responseBody) {
		Logger logger = LoggerFactory.getLogger(MDC.get("resp.doLog.logger"));
		logger.trace(LoggedInterceptor.RESPONSE, "Response: {}", responseBody);
	}
	
//	public boolean isRestService(InvocationContext invocationContext) {
//		
//		boolean isRestService = false;
//		
//		Method method = invocationContext.getMethod();
//		
//		if (method != null) {
//			
//			// vedere se il metodo ha la annotation @Path
//			if (method.getAnnotation(GET.class) != null || 
//					method.getAnnotation(POST.class) != null|| 
//					method.getAnnotation(PUT.class) != null|| 
//					method.getAnnotation(DELETE.class) != null|| 
//					method.getAnnotation(OPTIONS.class) != null) {
//				
//				isRestService = true;
//			}
//		} else {
//			// se non ho un Method assumo che non sia un REST service
//		}
//		
//		return isRestService;
//	}
	
	public Logged getLogged(InvocationContext invocationContext) {
		
		Logged logged = null;
		Method method = invocationContext.getMethod();
		
		if (method != null) {
			logged = method.getAnnotation(Logged.class);
			
			if (logged == null) {
				logged = method.getDeclaringClass().getAnnotation(Logged.class);
			}
		}
		
		if (logged == null) {
			logged = invocationContext.getTarget().getClass().getAnnotation(Logged.class);
		}
		
		if (logged == null) {
			logged = DEFAULT_LOGGED_INSTANCE;
		}
		
		return logged;
	}
	
	public Logger getLogger(InvocationContext invocationContext) {
		
		Logger logger;
		
		Method method = invocationContext.getMethod();
		
		if (method != null) {
			Class<?> beanClass = method.getDeclaringClass();
			logger = LoggerFactory.getLogger(beanClass);
		} else {
			Class<?> beanClass = invocationContext.getTarget().getClass();
			logger = LoggerFactory.getLogger(beanClass);
		}
		
		return logger;
	}

}
