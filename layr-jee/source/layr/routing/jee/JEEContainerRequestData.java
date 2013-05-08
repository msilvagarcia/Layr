package layr.routing.jee;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import layr.routing.ContainerRequestData;

class JEEContainerRequestData 
	implements ContainerRequestData<HttpServletRequest, HttpServletResponse> {

	HttpServletResponse response;
	HttpServletRequest request;
	
	public JEEContainerRequestData(
			HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

}
