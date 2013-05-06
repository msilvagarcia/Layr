package layr.routing;

import layr.routing.ExceptionHandler;
import layr.routing.Response;
import layr.routing.ResponseBuilder;
import layr.routing.annotations.Handler;

@Handler
public class NullPointerExceptionHandler implements ExceptionHandler<NullPointerException> {

	@Override
	public Response render(NullPointerException exception) {
		return ResponseBuilder.redirectTo( "/fail/" );
	}

}
