package layr.routing.lifecycle;

import static layr.commons.StringUtil.isEmpty;
import static layr.commons.StringUtil.oneOf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import layr.api.ApplicationContext;
import layr.api.BuiltResponse;
import layr.api.OutputRenderer;
import layr.api.RequestContext;
import layr.api.Response;
import layr.api.ResponseImpl;
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

	public void render( Object response ) throws RoutingException {
		try {
			if ( response == null )
				responseNoContent();
			else if ( Response.class.isInstance(response) )
				render( (Response) response );
			else if ( BuiltResponse.class.isInstance(response) )
				render( (BuiltResponse)response );
			else
				renderJson(response);
		} catch ( Exception e ) {
			throw new RoutingException( e );
		}
	}

	public void render( Response response ) throws IOException, RoutingException, InstantiationException, IllegalAccessException {
		BuiltResponse builtResponse = response.build();
		render(builtResponse);
	}

	public void render(BuiltResponse builtResponse) throws IOException, RoutingException, InstantiationException, IllegalAccessException {
		if ( builtResponse == null )
			throw new NullPointerException("Built response returned null. Please check your implementation provider.");

		memorizeHeaders( builtResponse );

		if ( !isEmpty( builtResponse.redirectTo() ) )
			requestContext.redirectTo( builtResponse.redirectTo() );
		else if ( isEmpty( builtResponse.contentType() )
			&&    builtResponse.statusCode() != null )
			requestContext.setStatusCode(builtResponse.statusCode());
		else if ( !isEmpty( builtResponse.contentType() ) )
			renderFromContentType( builtResponse );
		else
			responseNoContent();
	}

	public void memorizeHeaders(BuiltResponse builtResponse) {
		if ( builtResponse.headers() != null )
			for ( String name : builtResponse.headers().keySet() )
				requestContext.setResponseHeader( name, builtResponse.headers().get(name) );
	}

	public void renderFromContentType(BuiltResponse builtResponse) throws InstantiationException, IllegalAccessException, IOException {
		String contentType = builtResponse.contentType();
		setEncoding(builtResponse);
		memorizeParameters(builtResponse);
		Map<String, Class<? extends OutputRenderer>> outputRenderers = configuration.getRegisteredOutputRenderers();
		Class<? extends OutputRenderer> rendererClass = outputRenderers.get(contentType);
		OutputRenderer outputRenderer = rendererClass.newInstance();
		outputRenderer.render(requestContext, builtResponse);
	}

	public void setEncoding(BuiltResponse response)
			throws UnsupportedEncodingException {
		requestContext.setCharacterEncoding(
			oneOf( response.encoding(), configuration.getDefaultEncoding() ) );
	}

	public void memorizeParameters( BuiltResponse response ) {
		TemplateParameterObjectHandler parameterHandler = new TemplateParameterObjectHandler( requestContext );
		parameterHandler.memorizeParameters(response.parameterObject());
		memorizeParameters( response.parameters() );
	}

	public void memorizeParameters(Map<String, Object> parameters) {
		for ( String key : parameters.keySet() )
			requestContext.put(key, parameters.get(key));
	}

	public void responseNoContent() {
		requestContext.setStatusCode( 204 );
	}

	public void renderJson(Object response) throws IOException, InstantiationException, IllegalAccessException {
		renderFromContentType(
			new ResponseImpl()
				.contentType("application/json")
				.parameterObject( response )
				.build());
	}
}
