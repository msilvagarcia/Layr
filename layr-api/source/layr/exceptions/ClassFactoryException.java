package layr.exceptions;

import java.io.IOException;

public class ClassFactoryException extends IOException {

	private static final long serialVersionUID = -6889207365541233087L;

	public ClassFactoryException( String string, Throwable cause ) {
		super(string, cause);
	}
}
