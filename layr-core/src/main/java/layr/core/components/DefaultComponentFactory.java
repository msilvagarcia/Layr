package layr.core.components;

import java.util.HashMap;
import java.util.Map;

import layr.core.RequestContext;


public class DefaultComponentFactory implements IComponentFactory {

	protected Map<String, Class<? extends IComponent>> components;
	private String rootDir;
	private Class<? extends IComponent> defaultComponent;

	public DefaultComponentFactory() {
		defaultComponent = GenericComponent.class;
		components = new HashMap<String, Class<? extends IComponent>>();
		configure();
	}

	public void configure(){}

	/**
	 * Registers a component and its respective class.
	 * @param componentName
	 * @param componentClass
	 */
	public void register(String componentName, Class<? extends IComponent> componentClass) {
		components.put(componentName, componentClass);
	}

	@Override
	public IComponent newComponent(String name, String qName, RequestContext context)
			throws InstantiationException, IllegalAccessException {
		IComponent component = null;
		
		Class<? extends IComponent> clazz = components.get(name);
		if (clazz == null)
			clazz = defaultComponent;

		component = clazz.newInstance();
		component.setComponentName(name);
		component.setLayrContext(context);
		component.setQualifiedName(qName);

		if (rootDir != null)
			component.setRootdir(rootDir);

		return component;
	}

	public void setDefaultComponent(Class<? extends IComponent> defaultComponent) {
		this.defaultComponent = defaultComponent;
	}

	public Class<? extends IComponent> getDefaultComponent() {
		return defaultComponent;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

}