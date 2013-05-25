package layr.routing.lifecycle;

import layr.engine.RequestContext;
import layr.routing.api.ApplicationContext;
import layr.routing.api.ExceptionHandler;
import layr.routing.api.Response;
import layr.routing.async.Listener;
import layr.routing.exceptions.UnhandledException;

public class ExceptionHandlerListener implements Listener<Exception> {

	ApplicationContext applicationContext;
	RequestContext requestContext;

	public ExceptionHandlerListener(ApplicationContext applicationContext,
			RequestContext requestContext) {
		this.applicationContext = applicationContext;
		this.requestContext = requestContext;
	}

	@Override
	public void listen(Exception result) {
		BusinessRoutingRenderer renderer = new BusinessRoutingRenderer( applicationContext, requestContext );
		try {
			Response response = handleException( result );
			renderer.render(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public <T extends Throwable> Response handleException(T e) throws UnhandledException {
		String canonicalName = e.getClass().getCanonicalName();
		Class<ExceptionHandler> exceptionHandlerClass = applicationContext
				.getRegisteredExceptionHandlers().get(canonicalName);
		if (exceptionHandlerClass == null)
			throw new UnhandledException(e);
		try {
			Response render = handleException(e, exceptionHandlerClass);
			return render;
		} catch (Throwable e1) {
			throw new UnhandledException(e1);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends Throwable> Response handleException(T e,
			Class<ExceptionHandler> exceptionHandlerClass)
			throws InstantiationException, IllegalAccessException {
		ExceptionHandler<?> exceptionHandlerInstance = exceptionHandlerClass.newInstance();
		return ((ExceptionHandler<T>) exceptionHandlerInstance).render(e, applicationContext, requestContext );
	}

}
