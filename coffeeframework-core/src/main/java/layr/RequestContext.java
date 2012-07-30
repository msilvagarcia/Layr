/**
 * Copyright 2011 Miere Liniel Teixeira
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package layr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import layr.binding.ComplexExpressionEvaluator;
import layr.components.IComponentFactory;


public class RequestContext {

	private Map<String, IComponentFactory> registeredTagLibs = new HashMap<String, IComponentFactory>();

	private Map<String, Object> properties;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ServletContext servletContext;
	private String servletPath;
	private String requestAction;
	private String requestActionPattern;
	private ApplicationContext applicationContext;

	private int idComponentCounter = 0;
	private boolean multiPartRequest = false;
	private Map<String, Object> params;

	private String relativePath;

	public RequestContext() {
		properties = new HashMap<String, Object>();
	}

	/**
	 * @throws ServletException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	public void parseMultiPartRequest() throws ServletException, IOException {
		params = new HashMap<String, Object>();
		for (Part part : request.getParts()) {
			String filename = getParameterFilename(part);
			String fieldname = part.getName();
			if (filename == null) {
				String fieldvalue = getValue(part);
				params.put(fieldname, fieldvalue);
			} else if (!filename.isEmpty()) {
				if (reachedMaxFileSize(part))
					throw new IOException("MAX_FILE_SIZE_REACHED");
				params.put(fieldname, part);
			}
		}
	}

	/**
	 * @param part
	 * @return
	 */
	public boolean reachedMaxFileSize(Part part) {
		return false;
	}

	/**
	 * @param paramName
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws FileUploadException
	 */
	public Object getParameter(String paramName) throws ServletException,
			IOException {
		if (!multiPartRequest)
			return getRequest().getParameter(paramName);

		if (params == null)
			parseMultiPartRequest();

		return params.get(paramName);
	}

	/**
	 * @param part
	 * @return
	 */
	public String getParameterFilename(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String fileName = cd.substring(cd.indexOf('=') + 1).trim()
						.replace("\"", "");
				fileName = fileName.substring(fileName.lastIndexOf('/') + 1)
						.substring(fileName.lastIndexOf('\\') + 1);
				return fileName;
			}
		}
		return null;
	}

	/**
	 * @param part
	 * @return
	 * @throws IOException
	 */
	public String getValue(Part part) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				part.getInputStream(), "UTF-8"));
		StringBuilder value = new StringBuilder();
		char[] buffer = new char[1024];
		for (int length = 0; (length = reader.read(buffer)) > 0;) {
			value.append(buffer, 0, length);
		}
		return value.toString();
	}

	/**
	 * @param namespace
	 * @param factory
	 */
	public void registerNamespace(String namespace, IComponentFactory factory) {
		registeredTagLibs.put(namespace, factory);
	}

	/**
	 * @param namespace
	 * @return
	 */
	public IComponentFactory getComponentFactory(String namespace) {
		return registeredTagLibs.get(namespace);
	}

	/**
	 * @param namespace
	 * @return
	 */
	public boolean isRegisteredNamespace(String namespace) {
		return registeredTagLibs.containsKey(namespace);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(ServletRequest request) {
		this.request = (HttpServletRequest) request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(ServletResponse response) {
		this.response = (HttpServletResponse) response;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext context) {
		this.servletContext = context;
	}

	/**
	 * @param name
	 * @param property
	 */
	public RequestContext put(String name, Object property) {
		properties.put(name, property);
		return this;
	}

	/**
	 * @param name
	 * @return
	 */
	public Object get(String name) {
		return properties.get(name);
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Get the location URI relative to the layrContext.
	 * 
	 * @return
	 */
	public String getRelativePath() {
		if ( relativePath == null ) {
			String uri = request.getRequestURI()
								.replaceFirst(request.getContextPath(), "");

			if ( uri.equals("/") )
				 uri = applicationContext.getLayrConfiguration().getDefaultResource();
			relativePath = uri;
		}
		
		return relativePath;
	}

	/**
	 * Retrieves the current layrContext path. For more info see
	 * {@link HttpServletRequest#getContextPath()}
	 * 
	 * @return contextPath
	 */
	public String getContextPath() {
		return request.getContextPath();
	}

	public void setIdComponentCounter(int idComponentCounter) {
		this.idComponentCounter = idComponentCounter;
	}

	public int getIdComponentCounter() {
		return idComponentCounter;
	}

	/**
	 * @return
	 */
	public String getNextId() {
		return "component" + (idComponentCounter++);
	}

	/**
	 * @return
	 */
	public boolean isMultiPartRequest() {
		return multiPartRequest;
	}

	/**
	 * @param multiPartRequest
	 */
	public void setMultiPartRequest(boolean multiPartRequest) {
		this.multiPartRequest = multiPartRequest;
	}

	public Map<String, IComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public void setRegisteredTagLibs(
			Map<String, IComponentFactory> registeredNamespaces) {
		this.registeredTagLibs = registeredNamespaces;
	}

	public String getServletPath() {
		return servletPath;
	}

	public void setServletPath(String servletPath) {
		if (!servletPath.endsWith("/"))
			servletPath = servletPath + "/";
		this.servletPath = servletPath;
	}

	public String getRequestAction() {
		return requestAction;
	}

	public void setRequestAction(String requestAction) {
		this.requestAction = requestAction;
	}

    /**
     * @param encoding
     * @throws IOException
     */
    public void setCharacterEncoding(String encoding) throws IOException {
        getRequest().setCharacterEncoding(encoding);
        getResponse().setCharacterEncoding(encoding);
    }

    /**
     * @param contentType
     */
    public void setContentType(String contentType) {
        getResponse().setContentType(contentType);
    }

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getRequestActionPattern() {
		return requestActionPattern;
	}

	public void setRequestActionPattern(String requestActionPattern) {
		this.requestActionPattern = requestActionPattern;
	}
	
	/**
	 * Extract the parameter sent to the action method through the
	 * action's URL pattern. For example: it returns a Map with
	 * the 'id' key set to 9834 when the action method's pattern is
	 * /user/#{id}/edit and the request URL is /user/9834/edit.
	 *  
	 * @return
	 */
	public Map<String, String> getRequestParamsFromActionPattern(){
		if ( getRequestActionPattern() == null 
		||   getRequestAction() == null )
			return null;
		
		return ComplexExpressionEvaluator.extractMethodPlaceHoldersValueFromURL(
					getRequestActionPattern(),
					getRequestAction());
	}
	
	public void log(String text) {
		getServletContext().log(text);
	}
}
