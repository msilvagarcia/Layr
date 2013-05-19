package layr.routing.impl;

import layr.engine.RequestContext;
import layr.routing.api.ApplicationContext;
import layr.routing.api.DataProvider;
import layr.routing.api.Handler;
import layr.routing.exceptions.DataProviderException;

@Handler
public class RequestContextDataProvider implements DataProvider<RequestContext> {

	@Override
	public RequestContext newDataInstance(
			ApplicationContext applicationContext, RequestContext requestContext)
				throws DataProviderException {
		return requestContext;
	}

}
