package layr.util;

import java.util.ArrayList;

public class List<T> extends ArrayList<T> {

	private static final long serialVersionUID = 795276539448779396L;

	public static<T> List<T> fromArray( T[] source) {
		List<T> instance = new List<T>();
		
		for ( T t : source )
			instance.add(t);
		
		return instance;
	}
}
