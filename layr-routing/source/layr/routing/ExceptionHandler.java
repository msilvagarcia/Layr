package layr.routing;

public interface ExceptionHandler<T extends Throwable> {

	Response render( T exception );

}
