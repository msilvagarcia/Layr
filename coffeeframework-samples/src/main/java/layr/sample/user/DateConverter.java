package layr.sample.user;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import layr.util.IConverter;


public class DateConverter implements IConverter {

	private DateFormat formatter;

	public DateConverter() {
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
