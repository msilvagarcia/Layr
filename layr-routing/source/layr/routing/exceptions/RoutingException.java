package layr.routing.exceptions;

import java.io.IOException;

public class RoutingException extends IOException {

	private static final long serialVersionUID = 5061318521017132669L;

	public RoutingException(String message, Exception cause) {
		super( message, cause );
	}

	public RoutingException(Throwable e) {
		super( e );
	}

}
