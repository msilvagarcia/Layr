package layr.routing.impl;

import layr.api.ExceptionHandler;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.api.Response;
import layr.api.ResponseBuilder;

@Handler
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response render(NullPointerException exception, RequestContext reqContext) {
		return ResponseBuilder.redirectTo( "/fail/" );
	}

}
