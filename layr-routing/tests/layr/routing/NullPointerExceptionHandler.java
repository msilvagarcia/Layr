package layr.routing;

import layr.routing.annotations.Handler;
import layr.routing.api.ExceptionHandler;
import layr.routing.api.Response;
import layr.routing.service.ResponseBuilder;

@Handler
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response render(NullPointerException exception) {
		return ResponseBuilder.redirectTo( "/fail/" );
	}

}
