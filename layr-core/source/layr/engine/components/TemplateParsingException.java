package layr.engine.components;

public class TemplateParsingException extends Exception {

	private static final long serialVersionUID = 13298173193874614L;

	public TemplateParsingException(String message) {
		super( message );
	}

	public TemplateParsingException(String message, Throwable rootCause) {
		super( message, rootCause );
	}

	
}
