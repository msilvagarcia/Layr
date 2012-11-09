package org.layr.commons.gson;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonTransientAttributesExclusionStrategy implements ExclusionStrategy {
	
	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return f.getAnnotation(JsonTransient.class) != null;
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return false;
	}
}
