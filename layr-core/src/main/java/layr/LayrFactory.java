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
package layr;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class LayrFactory {

	private static Map<String, ApplicationContext> applicationContexts = new HashMap<String, ApplicationContext>();

	public static RequestContext createRequestContext(ServletRequest request,
			ServletResponse response, ServletContext servletContext) {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		RequestContext context = new RequestContext();

		context.setRequest(request);
		context.setResponse(response);
		context.setServletContext(servletContext);

		if (servletContext != null) {
			ApplicationContext applicationContext = getOrCreateApplicationContext(servletContext);
			context.setApplicationContext(applicationContext);
			context.setRegisteredTagLibs(applicationContext.getRegisteredTagLibs());
		}

		if (httpRequest != null) {
			context.put("contextPath", httpRequest.getContextPath());
			context.put("path", httpRequest.getRequestURI());
		}

		return context;
	}

	public static ApplicationContext getOrCreateApplicationContext(RequestContext context) {
		return getOrCreateApplicationContext(context.getServletContext());
	}

	public static ApplicationContext getOrCreateApplicationContext(ServletContext servletContext) {
		String contextPath = servletContext.getContextPath();
		ApplicationContext applicationContext = applicationContexts.get(contextPath);
		
		if (applicationContext == null)
			applicationContext = createApplicationContext(
					servletContext, contextPath, applicationContext);

		return applicationContext;
	}

	private static ApplicationContext createApplicationContext(
			ServletContext servletContext, String contextPath,
			ApplicationContext applicationContext) {
		try {
			applicationContext = new ApplicationContext();

			applicationContext.setServletContext(servletContext);
			applicationContexts.put(contextPath, applicationContext);
		} catch (NamingException e) {
			servletContext.log(e.getMessage());
			//e.printStackTrace();
		}
		return applicationContext;
	}

	public static void destroyApplicationContext( ApplicationContext applicationContext ) {
		String contextPath = applicationContext.getServletContext().getContextPath();
		applicationContexts.remove(contextPath);
		applicationContext.setServletContext(null);
	}
}
