package layr.servlet.data;

import javax.servlet.http.HttpServletRequest;

import layr.api.ApplicationContext;
import layr.api.DataProvider;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.exceptions.DataProviderException;
import layr.servlet.ServletRequestContext;

@Handler
public class HttpServletRequestDataProvider implements DataProvider<HttpServletRequest> {

	@Override
	public HttpServletRequest newDataInstance(
			ApplicationContext applicationContext,
			RequestContext requestContext) throws DataProviderException {
		return ((ServletRequestContext)requestContext).getRequest();
	}
}
