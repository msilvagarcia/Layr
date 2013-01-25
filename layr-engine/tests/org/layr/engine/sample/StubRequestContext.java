package org.layr.engine.sample;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.servlet.ServletException;

import org.layr.commons.Cache;
import org.layr.engine.AbstractRequestContext;
import org.layr.engine.components.IComponentFactory;

public class StubRequestContext extends AbstractRequestContext {
	
	StringWriter writer;
	
	public StubRequestContext() throws IOException {
		writer = new StringWriter();
		populateWithDefaultTagLibs();
		createCache();
		setApplicationRootPath("test");
	}

	public void createCache() {
		Cache cache = new Cache();
		setCache(cache);
	}

	public void populateWithDefaultTagLibs() {
		HashMap<String, IComponentFactory> registeredTagLibs = new HashMap<String, IComponentFactory>();
		AbstractRequestContext.populateWithDefaultTagLibs(registeredTagLibs);
		setRegisteredTagLibs( registeredTagLibs );
	}

	@Override
	public Object getParameter(String paramName) throws ServletException,
			IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCharacterEncoding(String encoding) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setContentType(String contentType) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Writer getWriter() {
		return writer;
	}

	public String getBuffedWroteContentToOutput(){
		return writer.getBuffer().toString();
	}
}
