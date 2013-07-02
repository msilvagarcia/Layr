package layr.api;


public interface ExceptionHandler<T extends Throwable> {

	Response render(ApplicationContext applicationContext,
			 RequestContext requestContext, T exception);

}
