package layr.routing.lifecycle;

import static layr.commons.ListenableCall.listenable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import layr.api.RequestContext;
import layr.api.Response;
import layr.commons.ListenableCall;
import layr.commons.Listener;

public class BusinessRoutingLifeCycle implements LifeCycle {

    ApplicationContext configuration;
	RequestContext requestContext;
	Listener<Response> onSuccess;
	Listener<Exception> onFail;
	HandledMethod matchedRouteMethod;

	public BusinessRoutingLifeCycle(
			ApplicationContext configuration,
			RequestContext requestContext) {
    	this.configuration = configuration;
    	this.requestContext = requestContext;
	}
	
	@Override
	public boolean canHandleRequest() {
		matchedRouteMethod = getMatchedRouteMethod();
		return matchedRouteMethod != null;
	}

	public HandledMethod getMatchedRouteMethod() {
		BusinessRoutingMethodMatching businessRoutingMethodMatching = new BusinessRoutingMethodMatching( configuration, requestContext );
		HandledMethod routeMethod = businessRoutingMethodMatching.getMatchedRouteMethod();
		return routeMethod;
	}

    public void run() {
		runMethod( matchedRouteMethod );
    }

	public void runMethod(HandledMethod routeMethod) {
		Listener<Exception> exceptionHandler = new ExceptionHandlerListener(configuration, requestContext);
		try {
			ListenableCall<Response> listenable = createListenableAsyncRunner(routeMethod, exceptionHandler);
			Future<?> submit = configuration.getMethodExecutionThreadPool().submit(listenable);
			if ( !requestContext.isAsyncRequest() )
				submit.get();
		} catch ( ExecutionException e ) {
			exceptionHandler.listen(e);
		} catch (Exception e) {
			exceptionHandler.listen(e);
		}
	}

	public ListenableCall<Response> createListenableAsyncRunner(
			HandledMethod routeMethod, Listener<Exception> exceptionHandler) {
		BusinessRoutingMethodRunner runner = new BusinessRoutingMethodRunner( configuration, requestContext, routeMethod );
		ListenableCall<Response> listenable = listenable(runner);
		listenable.onSuccess(new RendererListener(configuration, requestContext));
		listenable.onSuccess(onSuccess);
		listenable.onFail(exceptionHandler);
		listenable.onFail(onFail);
		return listenable;
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public void onFail( Listener<Exception> listener ){
		this.onFail = listener;
	}

	public void onSuccess( Listener<Response> listener ){
		this.onSuccess = listener;
	}
}
