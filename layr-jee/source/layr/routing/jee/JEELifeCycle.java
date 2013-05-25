package layr.routing.jee;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import layr.routing.api.Response;
import layr.routing.async.Listener;
import layr.routing.lifecycle.ApplicationLifeCycle;


class JEELifeCycle extends ApplicationLifeCycle {

	HttpServletRequest request;
	HttpServletResponse response;
	AsyncContext asyncContext;
	JEERequestContext requestContext;

	public JEELifeCycle(ServletRequest request, ServletResponse response) {
		this.request = (HttpServletRequest) request;
		this.response = (HttpServletResponse) response;
		onSuccess(createOnSuccessListener());
		onFail(createOnFailListener());
	}

	public void run() throws Exception {
		JEEConfiguration configuration = retrieveConfiguration( request );
		requestContext = createContext( configuration );
		asyncContext = createAsyncContext();
		run( configuration, requestContext );
	}

	public AsyncContext createAsyncContext() {
		if (isAsyncRequest())
			return request.startAsync(request, response);
		return null;
	}

	public JEEConfiguration retrieveConfiguration(HttpServletRequest request) {
		return (JEEConfiguration) request.getServletContext().getAttribute(
				JEEConfiguration.class.getCanonicalName() );
	}

	public JEERequestContext createContext(JEEConfiguration configuration){
		JEERequestContext requestContext = new JEERequestContext(request, response);
		requestContext.setRegisteredTagLibs(configuration.getRegisteredTagLibs());
		requestContext.setDefaultResource(configuration.getDefaultResource());
		return requestContext;
	}

	void onFinishRequest(){
		if ( isAsyncRequest() )
			asyncContext.complete();
	}

	public Listener<Response> createOnSuccessListener(){
		return new Listener<Response>() {
			public void listen(Response result) {
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
		return request.isAsyncSupported();
	}
}
