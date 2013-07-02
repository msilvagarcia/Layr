package layr.routing.sample;

import layr.api.ApplicationContext;
import layr.api.ExceptionHandler;
import layr.api.Handler;
import layr.api.RequestContext;
import layr.api.Response;
import layr.api.ResponseBuilder;

@Handler
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response render(ApplicationContext applicationContext,
			 RequestContext requestContext, NullPointerException exception ) {
		return ResponseBuilder.redirectTo( "/fail/" );
	}
}
