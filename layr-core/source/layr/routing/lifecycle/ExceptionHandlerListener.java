package layr.routing.lifecycle;

import layr.api.ExceptionHandler;
import layr.api.RequestContext;
import layr.api.Response;
import layr.commons.Listener;
import layr.exceptions.UnhandledException;

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
		try {
			Class<? extends ExceptionHandler> exceptionHandlerClass = getExceptionHandlerOrDefaultHandler(e);
			Response render = handleException(e, exceptionHandlerClass);
			return render;
		} catch (Throwable e1) {
			throw new UnhandledException(e1);
		}
	}

	@SuppressWarnings("rawtypes")
	protected <T extends Throwable> Class<? extends ExceptionHandler>
				getExceptionHandlerOrDefaultHandler( T e ) throws Throwable {
		Throwable t = e;
		Class<? extends ExceptionHandler> exceptionHandlerClass = getExceptionHandler(t);
		if (exceptionHandlerClass == null)
			t = new UnhandledException(e);
		exceptionHandlerClass = getExceptionHandler(t);
		if (exceptionHandlerClass == null)
			throw t;
		return exceptionHandlerClass;
	}

	@SuppressWarnings("rawtypes")
	public <T extends Throwable> Class<? extends ExceptionHandler> getExceptionHandler(T e) {
		String canonicalName = e.getClass().getCanonicalName();
		Class<? extends ExceptionHandler> exceptionHandlerClass = applicationContext
				.getRegisteredExceptionHandlers().get(canonicalName);
		return exceptionHandlerClass;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends Throwable> Response handleException(T e,
			Class<? extends ExceptionHandler> exceptionHandlerClass)
			throws InstantiationException, IllegalAccessException {
		ExceptionHandler<?> exceptionHandlerInstance = exceptionHandlerClass.newInstance();
		return ((ExceptionHandler<T>) exceptionHandlerInstance).render(e, requestContext );
	}

}
