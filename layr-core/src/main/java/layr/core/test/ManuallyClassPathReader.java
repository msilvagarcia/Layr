package layr.core.test;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import layr.core.resources.ClassPathResourceLoader;

public class ManuallyClassPathReader {

	public Set<Class<?>> read() throws IOException, ClassNotFoundException{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ClassPathResourceLoader classPathResourceLoader = new ClassPathResourceLoader(classLoader);
		List<Class<?>> availableClasses = classPathResourceLoader.retrieveAvailableClasses();
		HashSet<Class<?>> classHashSet = new HashSet<Class<?>>();
		
		for ( Class<?> clazz : availableClasses )
			classHashSet.add(clazz);
		
		return classHashSet;
	}
	
}
