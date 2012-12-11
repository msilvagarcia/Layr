package org.layr.jee.routing.natural;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.layr.engine.TemplateParser;
import org.layr.engine.components.IComponent;
import org.layr.engine.components.TemplateParsingException;
import org.layr.jee.commons.JEERequestContext;
import org.xml.sax.SAXException;

@WebFilter("/*")
public class PluginFilter implements Filter {

	private FilterConfig filterConfig;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		filterConfig.getServletContext().log( "[Layr] Natural Routing Filter Initialized.");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			JEERequestContext layrContext = JEERequestContext.createRequestContext(
					request, response, filterConfig.getServletContext());
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
	 * @throws TemplateParsingException 
	 */
	public boolean haveFoundTheWebPageXHTMLAndRenderedSuccessfully( JEERequestContext requestContext ) 
			throws IOException, ParserConfigurationException, SAXException, CloneNotSupportedException, ServletException, TemplateParsingException {

		String relativePath = requestContext.getRelativePath().replaceFirst("/$", "");
		if ( !relativePath.endsWith(".xhtml") )
			relativePath += ".xhtml";

		IComponent webpage = null;
		webpage = compile( relativePath, requestContext );
		if ( webpage == null )
			return false;

		HttpServletRequest request = requestContext.getRequest();
		Enumeration<String> parameterNames = request.getParameterNames();
		String expresion = null;

		while ( parameterNames.hasMoreElements() ){
			expresion = parameterNames.nextElement();
			requestContext.put(expresion, request.getParameter(expresion));
		}

		webpage.render();
		return true;
	}

	public IComponent compile(String templateName, JEERequestContext requestContext) throws TemplateParsingException {
		TemplateParser parser = new TemplateParser(requestContext);
		IComponent compiledTemplate = parser.compile(templateName);
		return compiledTemplate;
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
