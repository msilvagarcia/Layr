package org.layr.commons.classpath;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ManuallyClassPathReader {

	private ClassPathResourceLoader classPathResourceLoader;

	public Set<Class<?>> read() throws IOException, ClassNotFoundException{
		ClassPathResourceLoader classPathResourceLoader = getClassPathReader();
		List<Class<?>> availableClasses = classPathResourceLoader.retrieveAvailableClasses();
		HashSet<Class<?>> classHashSet = new HashSet<Class<?>>();
		
		for ( Class<?> clazz : availableClasses )
			classHashSet.add(clazz);
		
		return classHashSet;
	}

	public Set<String> readAvailableResources() throws IOException {
		ClassPathResourceLoader classPathResourceLoader = getClassPathReader();
		List<String> availableResources = classPathResourceLoader.retrieveAvailableResources();
		HashSet<String> availableSet = new HashSet<String>();

		for ( String resourceName : availableResources )
			availableSet.add(resourceName);

		return availableSet;
	}

	private ClassPathResourceLoader getClassPathReader() {
		if ( classPathResourceLoader != null )
			return classPathResourceLoader;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		classPathResourceLoader = new ClassPathResourceLoader(classLoader);
		return classPathResourceLoader;
	}
	
}
