package org.layr.jee.routing.business.sample;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.layr.commons.IConverter;



public class BrazilianFormatDateConverter implements IConverter {

	private DateFormat formatter;

	public BrazilianFormatDateConverter() {
		formatter = new SimpleDateFormat("dd/MM/yyyy");  
	}

	@Override
	public Object decode(Object value, Class<?> type, Type[] genericTypes) {
		try {
			return formatter.parse((String)value);
		} catch (ParseException e) {
			System.out.println("Can't parse date '"+ value +"'. Cause: "+e.getMessage()+". Returning null.");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String encode(Object object) {
		return formatter.format(object);
	}

}
