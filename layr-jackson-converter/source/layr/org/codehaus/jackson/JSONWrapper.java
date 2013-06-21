package layr.org.codehaus.jackson;

import java.io.IOException;
import java.io.InputStream;

import layr.api.BuiltResponse;
import layr.api.ContentType;
import layr.api.InputConverter;
import layr.api.OutputRenderer;
import layr.api.RequestContext;
import layr.exceptions.ConversionException;

import org.codehaus.jackson.map.ObjectMapper;

@ContentType(JSONWrapper.APPLICATION_JSON)
public class JSONWrapper implements OutputRenderer, InputConverter {

	public static final String APPLICATION_JSON = "application/json";
	private ObjectMapper mapper;
	
	public JSONWrapper() {
		mapper = new ObjectMapper();
	}

	@Override
	public void render(RequestContext requestContext, BuiltResponse response)
			throws ConversionException {
		try {
			mapper.writeValue(requestContext.getWriter(), response.object());
			requestContext.setContentType(APPLICATION_JSON);
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public <T> T convert(String value, Class<T> targetClass)
			throws ConversionException {
		try {
			if ( value == null || value.isEmpty() )
				return null;
			return mapper.readValue( value, targetClass );
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

	@Override
	public <T> T convert(InputStream value, Class<T> targetClass)
			throws ConversionException {
		try {
			if ( value == null )
				return null;
			return mapper.readValue( value, targetClass );
		} catch (IOException e) {
			throw new ConversionException(e);
		}
	}

}
