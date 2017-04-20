package it.tecla.utils.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.TimeZone;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;

@Provider
@Consumes({"application/json", "text/json"})
@Produces({"application/json", "text/json"})
public class JaxRsJacksonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public JaxRsJacksonProvider() {
		
		// READER
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.READ_ENUMS_USING_TO_STRING, true);
		
		// WRITER
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, true);
		mapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.ALWAYS);
		
		StdDateFormat dateFormat = new StdDateFormat();
		dateFormat.setTimeZone(TimeZone.getDefault());
		mapper.setDateFormat(dateFormat);
	}

	@Override
	public long getSize(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1L;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return isJsonType(mediaType);
	}

	@Override
	public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
		mapper.writeValue(entityStream, value);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return isJsonType(mediaType);
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
		return mapper.readValue(entityStream, type);
	}
	
	protected boolean isJsonType(MediaType mediaType) {
		if (mediaType != null) {
			String subtype = mediaType.getSubtype();
			return (("json".equalsIgnoreCase(subtype)) || (subtype.endsWith("+json")));
		}

		return true;
	}

}
