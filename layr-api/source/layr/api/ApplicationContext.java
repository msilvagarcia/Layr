package layr.api;

import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Holds application data useful for all requests.
 */
public interface ApplicationContext {

	/**
	 * @return a map with TagLibs found during the deploy
	 */
	public abstract Map<String, ComponentFactory> getRegisteredTagLibs();

	/**
	 * @return a map with Exception handlers found during the deploy
	 */
	@SuppressWarnings("rawtypes")
	public abstract Map<String, Class<? extends ExceptionHandler>> getRegisteredExceptionHandlers();

	/**
	 * @return a map with Exception handlers found during the deploy
	 */
	@SuppressWarnings("rawtypes")
	public abstract Map<String, Class<? extends DataProvider>> getRegisteredDataProviders();
	
	/**
	 * @return a map with all output renderer's found during deploy
	 */
	public Map<String, Class<? extends OutputRenderer>> getRegisteredOutputRenderers();

	/**
	 * @return a with all input converter's found during deploy
	 */
	public abstract Map<String,Class<? extends InputConverter>> getRegisteredInputConverters();

	/**
	 * @return the cache defined during deploy
	 */
	public abstract Cache getCache();

	/**
	 * Retrieves the default web resource path. It used to define the path
	 * to be rendered when requested the application root URI ( '/' ) .
	 * @return the default web resource path
	 */
	public abstract String getDefaultResource();
	
	/**
	 * @return the default encoding for template and request
	 */
	public abstract String getDefaultEncoding();

	/**
	 * Set an attribute that will be valid until application be removed or updated.
	 * @param name
	 * @param value
	 */
	public abstract void setAttribute( String name, Object value );

	/**
	 * @param name
	 * @return retrieve an attribute saved in application context.
	 */
	public abstract Object getAttribute( String name );

	/**
	 * Retrieves the ExecutorService that will execute every asynchronous method.
	 * 
	 * @return
	 * @throws Exception
	 */
	public ExecutorService getMethodExecutionThreadPool();
	
	/**
	 * Retrieves the ExecutorService that will render templates.
	 * @return
	 */
	public ExecutorService getRenderingThreadPool();

	/**
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Map<String, Class<? extends ClassFactory>> getRegisteredClassFactories();

	@SuppressWarnings("rawtypes")
	void setRegisteredClassFactories(
			Map<String, Class<? extends ClassFactory>> registeredClassFactories);
}
