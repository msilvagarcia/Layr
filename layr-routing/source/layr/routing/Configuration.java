package layr.routing;

import java.util.List;
import java.util.Map;

import layr.engine.Cache;
import layr.engine.RequestContext;
import layr.engine.components.ComponentFactory;
import layr.routing.exceptions.RoutingException;

public interface Configuration {

	public abstract String getDefaultResource();

	public abstract Map<String, ComponentFactory> getRegisteredTagLibs();

	public abstract Cache getCache();

	public abstract List<RouteClass> getRegisteredWebResources();

	public abstract RequestContext createContext();

	public abstract Object newInstanceOf(RouteClass routeClass) throws RoutingException;

	public abstract Map<String, Class<ExceptionHandler<?>>> getRegisteredExceptionHandlers();

}
