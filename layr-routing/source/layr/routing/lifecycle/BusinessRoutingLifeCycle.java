package layr.routing.lifecycle;

import static layr.routing.async.ListenableCall.listenable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import layr.engine.RequestContext;
import layr.routing.api.ApplicationContext;
import layr.routing.api.Response;
import layr.routing.async.ListenableCall;
import layr.routing.async.Listener;
import layr.routing.exceptions.NotFoundException;
import layr.routing.exceptions.RoutingException;

public class BusinessRoutingLifeCycle implements LifeCycle {

    ApplicationContext configuration;
	RequestContext requestContext;

	public BusinessRoutingLifeCycle(
			ApplicationContext configuration,
			RequestContext requestContext) {
    	this.configuration = configuration;
    	this.requestContext = requestContext;
	}

    public void run() throws NotFoundException, RoutingException {
    	HandledMethod routeMethod = getMatchedRouteMethod();
    	if ( routeMethod == null )
    		throw new NotFoundException( "No route found." );
		runMethod( routeMethod );
    }

	public HandledMethod getMatchedRouteMethod() {
		BusinessRoutingMethodMatching businessRoutingMethodMatching = new BusinessRoutingMethodMatching( configuration, requestContext );
		HandledMethod routeMethod = businessRoutingMethodMatching.getMatchedRouteMethod();
		return routeMethod;
	}

	public void runMethod(HandledMethod routeMethod) {
		Listener<Exception> exceptionHandler = new ExceptionHandlerListener(configuration, requestContext);
		try {
			ListenableCall<Response> listenable = createListenableAsyncRunner(routeMethod, exceptionHandler);
			Future<?> submit = configuration.getExecutorService().submit(listenable);
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
		listenable.onFail(exceptionHandler);
		return listenable;
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

}
