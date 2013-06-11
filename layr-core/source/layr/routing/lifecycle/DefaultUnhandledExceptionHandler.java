package layr.routing.lifecycle;

import layr.api.ExceptionHandler;
import layr.api.RequestContext;
import layr.api.Response;

public class DefaultUnhandledExceptionHandler implements ExceptionHandler<Throwable> {

	@Override
	public Response render(Throwable exception, RequestContext requestContext) {
		System.out.println("Rendered oh yeah!");
		return null;
	}

}
