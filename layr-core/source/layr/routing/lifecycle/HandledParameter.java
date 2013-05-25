package layr.routing.lifecycle;

public class HandledParameter {
	String name;
	Class<?> targetClazz;

	public HandledParameter(String name, Class<?> targetClazz) {
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

