package layr.util;

import java.lang.reflect.Type;
import java.util.Collection;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DefaultDataParser implements IConverter {

    /* (non-Javadoc)
     * @see layr.binding.IConverter#decode(java.lang.Object, java.lang.Class, java.lang.reflect.Type[])
     */
    @Override
    public Object decode(Object serializedData, Class<?> type, Type[] genericTypes) {
        if (!String.class.isInstance(serializedData))
            return serializedData;

        GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Collection.class, new CollectionDeserializer());
		Gson gson = builder.create();
		return gson.fromJson((String)serializedData, type);
    }

	/* (non-Javadoc)
	 * @see layr.binding.IConverter#encode(java.lang.Object)
	 */
	@Override
	public String encode(Object object) {
		if (object == null)
			return null;
		else if (String.class.isInstance(object))
            return (String)object;
		else if (object.getClass().getPackage().getName().equals("java.lang"))
			return String.valueOf(object);

		return new Gson().toJson(object);
	}

}
