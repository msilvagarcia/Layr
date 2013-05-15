package layr.routing.api;

public class RouteParameter {
	String name;
	Class<?> targetClazz;

	public RouteParameter(String name, Class<?> targetClazz) {
		this.name = name;
		this.targetClazz = targetClazz;
	}

	public String getName() {
		return name;
	}
	
	public Class<?> getTargetClazz() {
		return targetClazz;
	}
}

