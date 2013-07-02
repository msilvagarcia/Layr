package layr.servlet;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import layr.api.ApplicationContext;
import layr.commons.Listener;
import layr.routing.lifecycle.ApplicationLifeCycle;

class ServletApplicationLifeCycle extends ApplicationLifeCycle {

	HttpServletRequest request;
	HttpServletResponse response;
	AsyncContext asyncContext;
	ServletRequestContext requestContext;

	public ServletApplicationLifeCycle(ServletRequest request, ServletResponse response) {
		this.request = (HttpServletRequest) request;
		this.response = (HttpServletResponse) response;
		onSuccess(createOnSuccessListener());
		onFail(createOnFailListener());
	}

	public void run() throws Exception {
		ApplicationContext configuration = retrieveConfiguration( request );
		requestContext = createContext( configuration );
		run( configuration, requestContext );
	}

	@Override
	protected void beforeRun() {
		if (isAsyncRequest())
			asyncContext = request.startAsync(request, response);
	}

	public ApplicationContext retrieveConfiguration(HttpServletRequest request) {
		return (ApplicationContext) request.getServletContext().getAttribute(
				ApplicationContext.class.getCanonicalName() );
	}

	public ServletRequestContext createContext(ApplicationContext configuration) throws IOException{
		ServletRequestContext requestContext = new ServletRequestContext(request, response);
		requestContext.setRegisteredTagLibs(configuration.getRegisteredTagLibs());
		requestContext.setDefaultResource(configuration.getDefaultResource());
		return requestContext;
	}

	void onFinishRequest(){
		if ( isAsyncRequest() )
			asyncContext.complete();
	}

	public Listener<Object> createOnSuccessListener(){
		return new Listener<Object>() {
			public void listen(Object result) {
				onFinishRequest();
			}
		};
	}

	public Listener<Exception> createOnFailListener(){
		return new Listener<Exception>() {
			public void listen(Exception result) {
				onFinishRequest();
			}
		};
	}

	private boolean isAsyncRequest() {
		return requestContext.isAsyncRequest();
	}
}
