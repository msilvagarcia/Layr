package layr.routing;

import java.util.List;
import java.util.Map;

import layr.engine.Cache;
import layr.engine.components.ComponentFactory;

/**
 * Default abstract implementation of Configuration interface. Developers
 * are encouraged to extends this class in order to avoid rework.
 */
public abstract class AbstractConfiguration implements Configuration {

	String defaultResource;
	Cache cache;
	Map<String, ComponentFactory> registeredTagLibs;
	List<RouteClass> exposedResources;

	public AbstractConfiguration() {
		defaultResource = "home";
	}

	@Override
	public String getDefaultResource() {
		return defaultResource;
	}

	public void setDefaultResource(String defaultResource) {
		this.defaultResource = defaultResource;
	}

	@Override
	public Map<String, ComponentFactory> getRegisteredTagLibs() {
		return registeredTagLibs;
	}

	public void setRegisteredTagLibs(Map<String, ComponentFactory> registeredTagLibs) {
		this.registeredTagLibs = registeredTagLibs;
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	@Override
	public List<RouteClass> getRegisteredWebResources() {
		return exposedResources;
	}

	public void setRegisteredWebResources(List<RouteClass> exposedMethods) {
		this.exposedResources = exposedMethods;
	}

	protected Class<?> retrieveTargetClass(RouteClass routeClass) {
		return routeClass.targetClass;
	}

}
