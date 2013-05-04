package layr.org.codehaus.jackson;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

public class JacksonConverter extends Converter<Object> {

	@Override
	public Object convert(String value, Class<Object> targetClass) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue( value, targetClass );
	}

}