package org.layr.engine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletException;

public class GenericRequestContext extends AbstractRequestContext {
	
	StringWriter writer = new StringWriter();
	
	@Override
	public void setContentType(String contentType) {}
	
	@Override
	public void setCharacterEncoding(String encoding) {}
	
	@Override
	public Writer getWriter() {
		return writer;
	}

	@Override
	public Object getParameter(String paramName)
			throws ServletException, IOException {
		return null;
	}
}