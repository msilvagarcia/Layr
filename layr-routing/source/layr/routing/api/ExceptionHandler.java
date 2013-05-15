package layr.routing.api;

public interface ExceptionHandler<T extends Throwable> {

	Response render( T exception );

}
