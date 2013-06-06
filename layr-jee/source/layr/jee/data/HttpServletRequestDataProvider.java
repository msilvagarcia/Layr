package layr.jee.data;

import javax.servlet.http.HttpServletRequest;

import layr.api.DataProvider;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.exceptions.DataProviderException;
import layr.jee.JEERequestContext;

@Handler
public class HttpServletRequestDataProvider implements DataProvider<HttpServletRequest> {

	@Override
	public HttpServletRequest newDataInstance(RequestContext requestContext)
			throws DataProviderException {
		return ((JEERequestContext)requestContext).getRequest();
	}

}
