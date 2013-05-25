package layr.engine.components;

import java.util.HashMap;
import java.util.Map;

import layr.api.Component;
import layr.api.ComponentFactory;
import layr.api.RequestContext;


public class DefaultComponentFactory implements ComponentFactory {

	protected Map<String, Class<? extends Component>> components;
	private String rootDir;
	private Class<? extends Component> defaultComponent;

	public DefaultComponentFactory() {
		defaultComponent = GenericComponent.class;
		components = new HashMap<String, Class<? extends Component>>();
		configure();
	}

	public void configure(){}

	/**
	 * Registers a component and its respective class.
	 * @param componentName
	 * @param componentClass
	 */
	public void register(String componentName, Class<? extends Component> componentClass) {
		components.put(componentName, componentClass);
	}

	@Override
	public Component newComponent(String name, String qName, RequestContext context)
			throws InstantiationException, IllegalAccessException {
		Component component = null;
		
		Class<? extends Component> clazz = components.get(name);
		if (clazz == null)
			clazz = defaultComponent;

		component = clazz.newInstance();
		component.setComponentName(name);
		component.setRequestContext(context);
		component.setQualifiedName(qName);

		if (rootDir != null)
			component.setRootdir(rootDir);

		return component;
	}

	public void setDefaultComponent(Class<? extends Component> defaultComponent) {
		this.defaultComponent = defaultComponent;
	}

	public Class<? extends Component> getDefaultComponent() {
		return defaultComponent;
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

}