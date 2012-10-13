package layr.core.servlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import layr.core.ApplicationContext;
import layr.core.LayrFactory;
import layr.core.RequestContext;
import layr.core.components.IComponent;

import org.xml.sax.SAXException;

public class LayrFilter implements Filter {

	private FilterConfig filterConfig;
	private ApplicationContext layrApplicationContext;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		filterConfig.getServletContext().log( "[Layr] Initializing Main Filter defined by " + this.getClass().getCanonicalName());
		layrApplicationContext = LayrFactory.getOrCreateApplicationContext(filterConfig.getServletContext());
		this.filterConfig = filterConfig;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		RequestContext layrContext = LayrFactory.createRequestContext(request,
				response, filterConfig.getServletContext());

		try {
			if (!haveFoundTheWebPageXHTMLAndRenderedSuccessfully( layrContext ) )
				chain.doFilter(request, response);
		} catch (Exception e) {
			throw new ServletException(e.getMessage(), e);
		}
	}

	/**
	 * @param instance
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws CloneNotSupportedException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public boolean haveFoundTheWebPageXHTMLAndRenderedSuccessfully( RequestContext layrContext ) 
			throws IOException, ParserConfigurationException, SAXException, CloneNotSupportedException, ServletException {

		String relativePath = layrContext.getRelativePath().replaceFirst("/$", "");
		if ( !relativePath.endsWith(".xhtml") )
			relativePath += ".xhtml";

		IComponent webpage = null;
		webpage = layrApplicationContext.compile( relativePath, layrContext);
		if ( webpage == null )
			return false;

		HttpServletRequest request = layrContext.getRequest();
		Enumeration<String> parameterNames = request.getParameterNames();
		String expresion = null;

		while ( parameterNames.hasMoreElements() ){
			expresion = parameterNames.nextElement();
			layrContext.put(expresion, request.getParameter(expresion));
		}

		webpage.render();
		return true;
	}

	@Override
	public void destroy() {
		filterConfig = null;
		layrApplicationContext = null;
	}

	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

}
