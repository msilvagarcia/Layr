package layr.routing.lifecycle;

import java.util.Map;
import java.util.concurrent.Callable;

import layr.api.Component;
import layr.api.RequestContext;
import layr.api.Response;
import layr.commons.Listener;

public class NaturalRouterRenderer implements Callable<Response> {

	RequestContext requestContext;
	Listener<Response> onSuccess;
	Listener<Exception> onFail;
	Component compiledWebPage;

	public NaturalRouterRenderer(
			RequestContext requestContext,
			Component compiledWebPage) {
    	this.compiledWebPage = compiledWebPage;
    	this.requestContext = requestContext;
	}

	@Override
	public Response call() throws Exception {
		populateRequestContextWithSentParamsFromRequest();
		compiledWebPage.render();
		return null;
	}

	public void populateRequestContextWithSentParamsFromRequest() {
		Map<String, String> requestParameters = requestContext.getRequestParameters();
		for ( String paramName : requestParameters.keySet() )
			requestContext.put( paramName, requestParameters.get( paramName ) );
	}

}
