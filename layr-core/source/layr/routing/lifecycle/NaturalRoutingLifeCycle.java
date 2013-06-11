package layr.routing.lifecycle;

import static layr.commons.ListenableCall.listenable;
import static layr.commons.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import layr.api.Component;
import layr.api.RequestContext;
import layr.api.Response;
import layr.commons.ListenableCall;
import layr.commons.Listener;
import layr.engine.TemplateParser;
import layr.engine.components.TemplateParsingException;

public class NaturalRoutingLifeCycle implements LifeCycle {

	ApplicationContext configuration;
	RequestContext requestContext;
	Listener<Response> onSuccess;
	List<Listener<Exception>> onFail;
	Component compiledWebPage;

	public NaturalRoutingLifeCycle(ApplicationContext configuration,
			RequestContext requestContext) {
		this.configuration = configuration;
		this.requestContext = requestContext;
		this.onFail = new ArrayList<Listener<Exception>>();
	}

	@Override
	public boolean canHandleRequest() throws Exception {
		String template = measureTemplateFromRequestedURI();
		compiledWebPage = compile(template);
		return compiledWebPage != null;
	}

	public void run() {
		try {
			NaturalRouterRenderer renderer = new NaturalRouterRenderer(
					requestContext, compiledWebPage);
			ListenableCall<Response> listenableRenderer = listenable(renderer);
			listenableRenderer.onSuccess(onSuccess);
			defineOnFail(listenableRenderer);
			configuration.getRenderingThreadPool().submit(listenableRenderer);
		} catch ( Exception e ){
			onFail( e );
		}
	}

	public void defineOnFail(ListenableCall<Response> listenable) {
		for (Listener<Exception> onFailListener : onFail)
			listenable.onFail(onFailListener);
	}

	public String measureTemplateFromRequestedURI() {
		String relativePath = getRequestURI().replaceFirst("/$", "");
		if (!relativePath.endsWith(".xhtml"))
			relativePath += ".xhtml";
		return relativePath;
	}

	public String getRequestURI() {
		String requestURI = requestContext.getRequestURI();
		if (isEmpty(requestURI) || requestURI.equals("/"))
			return configuration.getDefaultResource();
		return requestURI;
	}

	public Component compile(String templateName)
			throws TemplateParsingException {
		TemplateParser parser = new TemplateParser(requestContext);
		Component compiledTemplate = parser.compile(templateName);
		return compiledTemplate;
	}

	@Override
	public RequestContext getRequestContext() {
		return requestContext;
	}

	public void onFail(Listener<Exception> listener) {
		this.onFail.add(listener);
	}

	protected void onFail(Exception cause) {
		for (Listener<Exception> exceptionListener : onFail)
			exceptionListener.listen(cause);
	}

	public void onSuccess(Listener<Response> listener) {
		this.onSuccess = listener;
	}
}
