package layr.org.codehaus.jackson;

import java.io.IOException;

import layr.api.BuiltResponse;
import layr.api.ContentType;
import layr.api.OutputRenderer;
import layr.api.RequestContext;

@ContentType(JSONOutputRenderer.APPLICATION_JSON)
public class JSONOutputRenderer implements OutputRenderer {

	public static final String APPLICATION_JSON = "application/json";

	@Override
	public void render(RequestContext requestContext, BuiltResponse response)
			throws IOException {
		ConverterFactory converter = new ConverterFactory();
		converter.encode(requestContext.getWriter(), response.object());
		requestContext.setContentType(APPLICATION_JSON);
	}

}
