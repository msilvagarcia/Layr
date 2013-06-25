package layr.exceptions;

public class DataProviderException extends RoutingException {

	private static final long serialVersionUID = 8789863000902707168L;

	public DataProviderException(Exception cause) {
		super(cause);
	}

	public DataProviderException(String message, Exception cause) {
		super(message, cause);
	}

}
