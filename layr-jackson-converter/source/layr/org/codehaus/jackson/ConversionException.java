package layr.org.codehaus.jackson;

public class ConversionException extends Exception {

	private static final long serialVersionUID = 2695056089411684745L;
	
	public ConversionException(String message, Exception cause) {
		super( message, cause );
	}

}
