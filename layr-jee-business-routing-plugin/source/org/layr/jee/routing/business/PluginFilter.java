package org.layr.jee.routing.business;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter("/*")
public class PluginFilter implements Filter {

	public static final String REGISTERED_WEB_RESOURCES = "REGISTERED_WEB_RESOURCES";
	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		filterConfig.getServletContext().log( "[Layr] Business Routing Filter Initialized.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		JEEBusinessRoutingRequestContext requestContext = JEEBusinessRoutingRequestContext.createRequestContext(request,
				response, filterConfig.getServletContext());

		try {
			Object instance = lookupLayrResource(requestContext);
			if (instance == null) {
				chain.doFilter(request, response);
				return;
			}

			// TODO: colocar cache de lifecycle
			runWebResource(requestContext, instance);
		} catch (Exception e) {
			throw new ServletException(e.getMessage(), e);
		}
	}

	/**
	 * @param requestContext
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Object lookupLayrResource(JEEBusinessRoutingRequestContext requestContext) throws InstantiationException, IllegalAccessException {
		String requestURI = requestContext.getRelativePath();
		Map<String, Object> webResources = (Map<String, Object>)((JEEBusinessRoutingConfiguration)requestContext.getConfiguration()).getWebResources();

		Set<String> patterns = webResources.keySet();
		for (String pattern : patterns) {
			if (requestURI.matches(pattern+".*")){
				requestContext.setWebResourceRootPath(pattern);
				Object instance = webResources.get(pattern);

				if (Class.class.isInstance(instance))
					instance = ((Class<?>)instance).newInstance();

				return instance;
			}
		}

		return null;
	}

	/**
	 * @param layrContext
	 * @param target
	 * @throws ServletException
	 * @throws IOException
	 */
	public void runWebResource(JEEBusinessRoutingRequestContext layrContext, Object target) throws ServletException, IOException {
    	RequestLifeCycle requestLifeCycle = new RequestLifeCycle();
    	requestLifeCycle.setTargetInstance(target);
    	requestLifeCycle.setRequestContext(layrContext);
    	requestLifeCycle.run();
    }

	@Override
	public void destroy() {
		filterConfig = null;
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

}
