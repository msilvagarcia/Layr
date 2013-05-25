package layr.routing.lifecycle;

import java.io.IOException;
import java.util.Map;

import layr.engine.RequestContext;
import layr.engine.TemplateParser;
import layr.engine.components.Component;
import layr.engine.components.TemplateParsingException;
import layr.routing.api.ApplicationContext;
import layr.routing.api.Response;
import layr.routing.async.Listener;

public class NaturalRoutingLifeCycle implements LifeCycle {

    ApplicationContext configuration;
	RequestContext requestContext;
	Listener<Response> onSuccess;
	Listener<Exception> onFail;
	private Component compiledWebPage;

	public NaturalRoutingLifeCycle(
			ApplicationContext configuration,
			RequestContext requestContext) {
    	this.configuration = configuration;
    	this.requestContext = requestContext;
	}
	
	@Override
	public boolean canHandleRequest() throws Exception {
		String template = measureTemplateFromRequestedURI();
		compiledWebPage = compile( template );
		return compiledWebPage != null;
	}

	public void run() {
		try {
			populateRequestContextWithSentParamsFromRequest();
			compiledWebPage.render();
		} catch (IOException e) {
			if ( onFail != null )
				onFail.listen(e);
		}
	}

	public String measureTemplateFromRequestedURI() {
		String relativePath = requestContext.getRequestURI().replaceFirst("/$", "");
		if ( !relativePath.endsWith(".xhtml") )
			relativePath += ".xhtml";
		return relativePath;
	}

	public Component compile(String templateName) throws TemplateParsingException {
		TemplateParser parser = new TemplateParser(requestContext);
		Component compiledTemplate = parser.compile(templateName);
		return compiledTemplate;
	}

	public void populateRequestContextWithSentParamsFromRequest() {
		Map<String, String> requestParameters = requestContext.getRequestParameters();
		for ( String paramName : requestParameters.keySet() )
			requestContext.put( paramName, requestParameters.get( paramName ) );
	}

	@Override
	public RequestContext getRequestContext() {
		return requestContext;
	}

	public void onFail( Listener<Exception> listener ){
		this.onFail = listener;
	}

	public void onSuccess( Listener<Response> listener ){
		this.onSuccess = listener;
	}
}
