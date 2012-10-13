package layr.core.commons;

import java.util.HashMap;
import java.util.Iterator;

public class Dictionary<T,N> extends HashMap<T,N> implements Iterable<T> {

	private static final long serialVersionUID = 6918901377131903204L;

	public Dictionary() {}
	
	public Dictionary<T,N> set(T key, N value) {
		put(key, value);
		return this;
	}

	@Override
	public Iterator<T> iterator() {
		return keySet().iterator();
	}
}
