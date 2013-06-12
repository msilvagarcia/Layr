package layr.org.codehaus.jackson;

import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.map.ObjectMapper;

public class JacksonConverter extends Converter<Object> {

	@Override
	public Object convert(String value, Class<Object> targetClass) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue( value, targetClass );
	}
	
	@Override
	public Object convert(InputStream value, Class<Object> targetClass) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue( value, targetClass );
	}

}