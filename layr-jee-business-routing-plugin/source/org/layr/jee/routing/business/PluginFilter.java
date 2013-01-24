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

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		filterConfig.getServletContext().log( "[Layr] JEE Business Routing Filter Initialized.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		JEEBusinessRoutingRequestContext requestContext = JEEBusinessRoutingRequestContext
			.createRequestContext(request, response, filterConfig.getServletContext());

		try {
			Object instance = lookupWebResource(requestContext);
			if (instance == null) {
				chain.doFilter(request, response);
				return;
			}

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
	public Object lookupWebResource(JEEBusinessRoutingRequestContext requestContext) throws InstantiationException, IllegalAccessException {
		String requestURI = requestContext.getRelativePath();
		JEEBusinessRoutingConfiguration jeeBusinessRoutingConfiguration = (JEEBusinessRoutingConfiguration)requestContext.getConfiguration();
		Map<String, Object> webResources = (Map<String, Object>)jeeBusinessRoutingConfiguration.getWebResources();
		Set<String> patterns = webResources.keySet();

		for (String pattern : patterns)
			if (requestURI.matches(pattern+".*"))
				return createInstanceFromPattern(
					requestContext, webResources,pattern);

		return null;
	}

	/**
	 * @param requestContext
	 * @param webResources
	 * @param pattern
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Object createInstanceFromPattern(JEEBusinessRoutingRequestContext requestContext,
			Map<String, Object> webResources, String pattern)
			throws InstantiationException, IllegalAccessException {
		requestContext.setWebResourceRootPath(pattern);

		Object instance = webResources.get(pattern);
		if (Class.class.isInstance(instance))
			instance = ((Class<?>)instance).newInstance();

		return instance;
	}

	/**
	 * @param requestContext
	 * @param target
	 * @throws ServletException
	 * @throws IOException
	 */
	public void runWebResource(JEEBusinessRoutingRequestContext requestContext, Object target) throws ServletException, IOException {
    	RequestLifeCycle requestLifeCycle = new RequestLifeCycle();
    	requestLifeCycle.setTargetInstance(target);
    	requestLifeCycle.setRequestContext(requestContext);
    	requestLifeCycle.run();
    }

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		filterConfig = null;
	}

	/**
	 * @return
	 */
	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	/**
	 * @param filterConfig
	 */
	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

}
