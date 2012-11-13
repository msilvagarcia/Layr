package org.layr.commons;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Cache {

	private Map<String, Object> data;

	public Cache() {
		data = new ConcurrentHashMap<String, Object>();
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
