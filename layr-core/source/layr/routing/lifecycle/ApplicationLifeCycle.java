package layr.routing.lifecycle;

import layr.api.RequestContext;
import layr.api.Response;
import layr.commons.Listener;
import layr.exceptions.NotFoundException;

public class ApplicationLifeCycle {

	Listener<Response> onSuccess;
	Listener<Exception> onFail;
	Listener<Exception> exceptionHandler;

	public ApplicationLifeCycle() {
		super();
	}

	public void run(ApplicationContext applicationContext, RequestContext requestContext) throws Exception {
		createRequestExceptionHandler(applicationContext, requestContext);
		LifeCycle[] lifeCycles = createLifeCycles(applicationContext, requestContext);

		for ( LifeCycle lifeCycle : lifeCycles )
			if ( lifeCycle.canHandleRequest() ){
				beforeRun();
				lifeCycle.run();
				afterRun();
				return;
			}

		throw new NotFoundException("Not found");
	}

	protected void createRequestExceptionHandler(ApplicationContext applicationContext,
			RequestContext requestContext) {
		exceptionHandler = new ExceptionHandlerListener(applicationContext, requestContext);
	}

	protected LifeCycle[] createLifeCycles(ApplicationContext applicationContext, RequestContext requestContext) {
		LifeCycle[] lifeCycles = new LifeCycle[]{
			createBusinessRoutingLifeCycle(applicationContext, requestContext),
			createNaturalRoutingLifeCycle(applicationContext, requestContext)
		};
		return lifeCycles;
	}

	protected BusinessRoutingLifeCycle createBusinessRoutingLifeCycle(ApplicationContext applicationContext, RequestContext requestContext) {
		BusinessRoutingLifeCycle lifeCycle = new BusinessRoutingLifeCycle( applicationContext, requestContext );
		lifeCycle.onSuccess(onSuccess);
		lifeCycle.onFail(exceptionHandler);
		lifeCycle.onFail(onFail);
		return lifeCycle;
	}

	protected NaturalRoutingLifeCycle createNaturalRoutingLifeCycle(ApplicationContext applicationContext, RequestContext requestContext) {
		NaturalRoutingLifeCycle lifeCycle = new NaturalRoutingLifeCycle( applicationContext, requestContext );
		lifeCycle.onSuccess(onSuccess);
		lifeCycle.onFail(exceptionHandler);
		lifeCycle.onFail(onFail);
		return lifeCycle;
	}

	protected void beforeRun() {}
	protected void afterRun() {}

	public void onFail(Listener<Exception> listener) {
		this.onFail = listener;
	}

	public void onSuccess(Listener<Response> listener) {
		this.onSuccess = listener;
	}

}