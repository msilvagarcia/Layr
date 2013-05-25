package layr.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import layr.engine.components.Component;

public class Cache {

	private Map<String, Component> data;

	public Cache() {
		data = new ConcurrentHashMap<String, Component>();
	}
	
	public Component get( String key ){
		return data.get(key);
	}
	
	public void put( String key, Component value ) {
		data.put(key, value);
	}

	public int size() {
		return data.size();
	}
}
