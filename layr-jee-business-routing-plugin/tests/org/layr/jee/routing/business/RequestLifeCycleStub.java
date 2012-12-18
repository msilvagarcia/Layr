package org.layr.jee.routing.business;

import java.io.IOException;

public class RequestLifeCycleStub extends RequestLifeCycle {
	
	String lastRedirectedUri;

	@Override
	public void redirect(String uri) throws IOException {
		lastRedirectedUri = uri;
	}
	
}
