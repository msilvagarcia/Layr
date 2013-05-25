package layr.routing.lifecycle;

import static layr.commons.StringUtil.isEmpty;
import static layr.commons.StringUtil.oneOf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import layr.api.BuiltResponse;
import layr.api.Component;
import layr.api.RequestContext;
import layr.api.Response;
import layr.engine.TemplateParser;
import layr.engine.components.TemplateParsingException;
import layr.exceptions.RoutingException;

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
			if ( response == null )
				responseNoContent();
			else
				render( response.build() );
		} catch ( IOException e ) {
			throw new RoutingException( e );
		}
	}

	public void render( BuiltResponse builtResponse ) throws IOException, RoutingException {
		if ( builtResponse == null )
			throw new NullPointerException("Built response returned null. Please check your implementation provider.");
		else if ( !isEmpty( builtResponse.redirectTo() ) )
			requestContext.redirectTo( builtResponse.redirectTo() );
		else if ( !isEmpty( builtResponse.template() ) )
			renderResponseTemplate( builtResponse );
		else
			responseNoContent();
	}

	public void renderResponseTemplate( BuiltResponse response ) throws RoutingException {
		try {
			setContentTypeAndEncoding( response );
			memorizeParameters( response );
			TemplateParser parser = new TemplateParser( requestContext );
			Component compiledTemplate = parser.compile( response.template() );
			compiledTemplate.render();
		} catch (TemplateParsingException e) {
			throw new RoutingException( "Can't to compile '" + response.template() + "' template.", e );
		} catch (IOException e) {
			throw new RoutingException( "Can't to render '" + response.template() + "' template.", e );
		}
	}

	public void setContentTypeAndEncoding( BuiltResponse response ) throws UnsupportedEncodingException {
		requestContext.setContentType( "text/html" );
		requestContext.setCharacterEncoding(
			oneOf( response.encoding(), configuration.getDefaultEncoding() ) );
	}
	
	public void memorizeParameters( BuiltResponse response) {
		TemplateParameterObjectHandler parameterHandler = new TemplateParameterObjectHandler( requestContext );
		parameterHandler.memorizeParameters(response.templateParameterObject());
		memorizeParameters( response.parameters() );
	}

	public void memorizeParameters(Map<String, Object> parameters) {
		for ( String key : parameters.keySet() )
			requestContext.put(key, parameters.get(key));
	}

	public void responseNoContent() {
		requestContext.setStatusCode( 204 );
	}
}
