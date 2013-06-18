package layr.routing.lifecycle;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import layr.api.BuiltResponse;
import layr.api.Component;
import layr.api.ContentType;
import layr.api.OutputRenderer;
import layr.api.RequestContext;
import layr.engine.TemplateParser;
import layr.engine.components.TemplateParsingException;
import layr.exceptions.RoutingException;

@ContentType(XHTMLOutputRenderer.TEXT_HTML)
public class XHTMLOutputRenderer implements OutputRenderer {

	public static final String TEXT_HTML = "text/html";
	RequestContext requestContext;
	BuiltResponse response;

	@Override
	public void render(RequestContext requestContext, BuiltResponse response) throws IOException {
		this.requestContext = requestContext;
		this.response = response;
		render();
	}

	public void render() throws RoutingException {
		String template = (String)response.object();
		try {
			renderResponseTemplate(template);
		} catch (TemplateParsingException e) {
			throw new RoutingException( "Can't to compile '" + template + "' template.", e );
		} catch (IOException e) {
			throw new RoutingException( "Can't to render '" + template + "' template.", e );
		}
	}

	public void renderResponseTemplate(String template)
			throws UnsupportedEncodingException, TemplateParsingException,
			IOException {
		setDefaultStatusAndCodeContentTypeAndEncoding();
		TemplateParser parser = new TemplateParser( requestContext );
		Component compiledTemplate = parser.compile( template );
		compiledTemplate.render();
	}

	public void setDefaultStatusAndCodeContentTypeAndEncoding() throws UnsupportedEncodingException {
		requestContext.setContentType( TEXT_HTML );
	}
}
