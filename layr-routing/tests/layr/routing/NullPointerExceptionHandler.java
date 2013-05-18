package layr.routing;

import layr.routing.api.ExceptionHandler;
import layr.routing.api.Handler;
import layr.routing.api.Response;
import layr.routing.api.ResponseBuilder;

@Handler
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response render(NullPointerException exception) {
		return ResponseBuilder.redirectTo( "/fail/" );
	}

}
