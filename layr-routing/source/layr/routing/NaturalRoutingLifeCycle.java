package layr.routing;

import java.io.IOException;
import java.util.Map;

import layr.engine.RequestContext;
import layr.engine.TemplateParser;
import layr.engine.components.Component;
import layr.engine.components.TemplateParsingException;
import layr.routing.exceptions.NotFoundException;

public class NaturalRoutingLifeCycle implements LifeCycle {

    Configuration configuration;
	RequestContext requestContext;

	public NaturalRoutingLifeCycle(Configuration configuration) {
    	this.configuration = configuration;
    	this.requestContext = configuration.createContext();
	}

	public void run() throws TemplateParsingException, NotFoundException, IOException {
		String template = measureTemplateFromRequestedURI();
		Component webpage = compile( template );
		if ( webpage == null )
			throw new NotFoundException( "No template found" );
		populateRequestContextWithSentParamsFromRequest();
		webpage.render();
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
}
