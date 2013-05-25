package layr.api;


public interface ExceptionHandler<T extends Throwable> {

	Response render(T exception, RequestContext requestContext);

}
