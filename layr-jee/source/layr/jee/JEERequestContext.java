/**
 * Copyright 2012 Miere Liniel Teixeira
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
package layr.jee;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static layr.commons.StringUtil.isEmpty;
import layr.engine.AbstractRequestContext;
import layr.org.codehaus.jackson.ConversionException;
import layr.org.codehaus.jackson.ConverterFactory;

class JEERequestContext extends AbstractRequestContext {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private Map<String,String> requestParameter;
	private ConverterFactory converter;
	private String applicationRootPath;
	private String requestURI;

	public JEERequestContext(
			HttpServletRequest request, HttpServletResponse response  ) {
		super();
		this.request = request;
		this.response = response;
		this.converter = new ConverterFactory();
	}

	@Override
	public Writer getWriter() throws IOException {
		return response.getWriter();
	}

	@Override
	public void redirectTo(String redirectTo) throws IOException {
		response.sendRedirect( redirectTo );
	}

	@Override
	public void setStatusCode(int statusCode) {
		response.setStatus( statusCode );
	}

	@Override
	public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
		request.setCharacterEncoding( encoding );
		response.setCharacterEncoding( encoding );
	}

	@Override
	public void setContentType(String contentType) {
		response.setContentType( contentType );
	}

	@Override
	public String getRequestHttpMethod() {
		return request.getMethod();
	}

	@Override
	public String getRequestURI() {
		if ( isEmpty( requestURI ) )
			requestURI = returnRequestURIOrDefaultRequest(
				request.getRequestURI().replace( getApplicationRootPath(), "" ) );
		return requestURI;
	}

	@Override
	public String getApplicationRootPath() {
		if ( isEmpty( applicationRootPath ) )
			applicationRootPath = request.getContextPath();
		return applicationRootPath;
	}

	@Override
	public Map<String, String> getRequestParameters() {
		if ( requestParameter == null ) {
			requestParameter = new HashMap<String, String>();
			for ( String param : request.getParameterMap().keySet() )
				requestParameter.put( param, request.getParameter( param ) );
		}
		return requestParameter;
	}

	@Override
	public Object convert(String value, Class<?> targetClass) throws IOException {
		try {
			return converter.convert( value, targetClass );
		} catch (ConversionException e) {
			throw new IOException( e );
		}
	}

	public InputStream openStream(String url) {
		InputStream stream = getClassLoader().getResourceAsStream(url);
		if (stream == null && getServletContext() != null)
			stream = getServletContext().getResourceAsStream("/" + url);
		return stream;
	}

	public ServletContext getServletContext() {
		return request.getServletContext();
	}
	
	@Override
	public boolean isAsyncRequest() {
		return request.isAsyncSupported();
	}
}
