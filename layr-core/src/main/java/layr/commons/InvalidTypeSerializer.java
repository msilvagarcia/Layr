package layr.commons;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class InvalidTypeSerializer implements JsonSerializer<Object> {

	@Override
	public JsonElement serialize(Object src, Type typeOfSrc,
			JsonSerializationContext context) {
		return null;
	}

}
