package layr.routing.impl;

import layr.engine.RequestContext;
import layr.routing.api.ApplicationContext;
import layr.routing.api.ExceptionHandler;
import layr.routing.api.Handler;
import layr.routing.api.Response;
import layr.routing.api.ResponseBuilder;

@Handler
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response render(NullPointerException exception,
			ApplicationContext appContext, RequestContext reqContext) {
		return ResponseBuilder.redirectTo( "/fail/" );
	}

}
