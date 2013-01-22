package org.layr.commons.sample;

import java.lang.reflect.Type;

import org.layr.commons.IConverter;

public class StringConverter implements IConverter {

	@Override
	public Object decode(Object value, Class<?> type, Type[] genericTypes) {
		return value;
	}

	@Override
	public String encode(Object object) {
		return "<" + object + ">";
	}

}
