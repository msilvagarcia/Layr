package org.layr.engine.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletException;

import org.layr.commons.Cache;
import org.layr.commons.classpath.ManuallyClassPathReader;
import org.layr.engine.AbstractRequestContext;

public class StubRequestContext extends AbstractRequestContext {
	
	public StubRequestContext() throws IOException {
		ManuallyClassPathReader manuallyClassPathReader = new ManuallyClassPathReader();
		Set<String> availableResources = manuallyClassPathReader.readAvailableResources();
		setAvailableLocalResourceFiles(availableResources);
		
		Cache cache = new Cache();
		setCache(cache);
		setApplicationRootPath("test");
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
	public PrintWriter getWriter() {
		// TODO Auto-generated method stub
		return null;
	}

}
