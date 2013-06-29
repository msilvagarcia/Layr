package layr.servlet.data;

import javax.servlet.http.HttpServletResponse;

import layr.api.DataProvider;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.exceptions.DataProviderException;
import layr.servlet.ServletRequestContext;

@Handler
public class HttpServletResponseDataProvider implements DataProvider<HttpServletResponse> {

	@Override
	public HttpServletResponse newDataInstance(RequestContext requestContext)
			throws DataProviderException {
		return ((ServletRequestContext)requestContext).getResponse();
	}

}
