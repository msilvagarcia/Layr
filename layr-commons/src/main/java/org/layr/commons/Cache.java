package org.layr.commons;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {

	private static Cache instance;
	private Map<String, Object> data;

	private Cache() {
		data = new ConcurrentHashMap<String, Object>();
	}

	public static Cache getInstance( String instanceIdentifier ){
		instantiateCacheSingleton();
		Cache cache = (Cache)instance.data.get(instanceIdentifier);
		return cache;
	}

	public static Cache newInstance( String instanceIdentifier ){
		Cache cache = (Cache)getInstance(instanceIdentifier);
		if ( cache == null ){
			cache = new Cache();
			instance.data.put(instanceIdentifier, cache);
		}
		return cache;
	}

	private static void instantiateCacheSingleton() {
		if ( instance == null )
			instance = new Cache();
	}

	public static void clearAllCaches(){
		if ( instance == null )
			return;
		
		for ( String key: instance.data.keySet() )
			clearCache( key );
	}

	public static void clearCache( String instanceIdentifier ){
		Cache cache = (Cache)getInstance(instanceIdentifier);
		if ( cache == null )
			return;

		cache.clearCache();
		instance.data.remove(instanceIdentifier);
	}
	
	public Object get( String key ){
		return data.get(key);
	}
	
	public void put( String key, Object value ) {
		data.put(key, value);
	}

	@SuppressWarnings("rawtypes")
	public void clearCache() {
		for ( String key: data.keySet() ){
			Object object = data.get(key);
			if ( Collection.class.isInstance(object) )
				((Collection)object).clear();
			if ( Map.class.isInstance(object) )
				((Map)object).clear();
			data.remove(key);
		}

		data.clear();
	}

	public int size() {
		return data.size();
	}
}
