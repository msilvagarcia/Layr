package layr.jee;

import java.io.IOException;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import layr.commons.Listener;
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
		run( configuration, requestContext );
	}

	@Override
	protected void beforeRun() {
		if (isAsyncRequest())
			asyncContext = request.startAsync(request, response);
	}

	public JEEConfiguration retrieveConfiguration(HttpServletRequest request) {
		return (JEEConfiguration) request.getServletContext().getAttribute(
				JEEConfiguration.class.getCanonicalName() );
	}

	public JEERequestContext createContext(JEEConfiguration configuration) throws IOException{
		JEERequestContext requestContext = new JEERequestContext(request, response);
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
				result.printStackTrace();
				onFinishRequest();
			}
		};
	}

	private boolean isAsyncRequest() {
		return requestContext.isAsyncRequest();
	}
}
