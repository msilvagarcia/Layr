package layr.routing.lifecycle;

import layr.api.ApplicationContext;
import layr.api.DataProvider;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.exceptions.DataProviderException;

@Handler
public class RequestContextDataProvider implements DataProvider<RequestContext> {

	@Override
	public RequestContext newDataInstance(
			ApplicationContext applicationContext,
			RequestContext requestContext) throws DataProviderException {
		return requestContext;
	}
}
