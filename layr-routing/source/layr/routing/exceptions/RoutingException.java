package layr.routing.exceptions;

public class RoutingException extends Exception {

	private static final long serialVersionUID = 5061318521017132669L;

	public RoutingException(String message, Exception cause) {
		super( message, cause );
	}

}
