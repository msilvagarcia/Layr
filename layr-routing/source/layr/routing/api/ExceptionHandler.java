package layr.routing.api;

import layr.engine.RequestContext;

public interface ExceptionHandler<T extends Throwable> {

	Response render(T exception, ApplicationContext applicationContext,
			RequestContext requestContext);

}
