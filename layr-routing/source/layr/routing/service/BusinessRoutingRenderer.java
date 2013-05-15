package layr.routing.service;

import static layr.commons.StringUtil.isEmpty;
import static layr.commons.StringUtil.oneOf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import layr.engine.RequestContext;
import layr.engine.TemplateParser;
import layr.engine.components.Component;
import layr.engine.components.TemplateParsingException;
import layr.routing.api.ApplicationContext;
import layr.routing.api.Response;
import layr.routing.exceptions.RoutingException;

public class BusinessRoutingRenderer {

	ApplicationContext configuration;
	RequestContext requestContext;

	public BusinessRoutingRenderer(
			ApplicationContext configuration,
			RequestContext requestContext) {
		this.configuration = configuration;
		this.requestContext = requestContext;
	}

	public void render( Response response ) throws RoutingException {
		try {
			if ( !isEmpty( response.redirectTo() ) )
				requestContext.redirectTo( response.redirectTo() );
			else if ( !isEmpty( response.template() ) )
				renderResponseTemplate( response );
			else
				responseNoContent();
		} catch ( IOException e ) {
			throw new RoutingException( e );
		}
	}

	public void renderResponseTemplate( Response response ) throws RoutingException {
		try {
			setContentTypeAndEncoding( response );
			TemplateParser parser = new TemplateParser( requestContext );
			Component compiledTemplate = parser.compile( response.template() );
			compiledTemplate.render();
		} catch (TemplateParsingException e) {
			throw new RoutingException( "Can't to compile '" + response.template() + "' template.", e );
		} catch (IOException e) {
			throw new RoutingException( "Can't to render '" + response.template() + "' template.", e );
		}
	}

	public void setContentTypeAndEncoding( Response response ) throws UnsupportedEncodingException {
		requestContext.setContentType( "text/html" );
		requestContext.setCharacterEncoding(
			oneOf( response.encoding(), configuration.getDefaultEncoding() ) );
	}

	public void responseNoContent() {
		requestContext.setStatusCode( 204 );
	}
}
