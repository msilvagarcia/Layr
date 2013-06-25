package layr.exceptions;

import java.io.IOException;

public class ConversionException extends IOException {

	private static final long serialVersionUID = 2695056089411684745L;
	
	public ConversionException(String message, Exception cause) {
		super( message, cause );
	}

	public ConversionException(String message) {
		super( message );
	}

	public ConversionException(Exception cause) {
		super(cause);
	}
}
