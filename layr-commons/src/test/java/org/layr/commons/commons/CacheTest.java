package org.layr.commons.commons;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.layr.commons.Cache;

public class CacheTest {

	private Cache cache;

	@Before
	public void setup(){
		cache = new Cache();
	}

	@Test
	public void populateWithObjectsAndClearCache(){
		cache.put("C", new Object());
		assertEquals( 1, cache.size() );
		cache.clearCache();
		assertEquals( 0, cache.size() );
	}

	@Test
	public void populateWithArrayAndClearCache(){
		ArrayList<Integer> cachedArray = new ArrayList<Integer>();
		for ( int i=0; i<4; i++ )
			cachedArray.add(i);
		cache.put("C", cachedArray);
		assertEquals( 4, cachedArray.size() );
		assertEquals( 1, cache.size() );
		cache.clearCache();
		assertEquals( 0, cache.size() );
		assertEquals( 0, cachedArray.size() );
	}

	@Test
	public void populateWithMapAndClearCache(){
		HashMap<Integer, Integer> cachedMap = new HashMap<Integer, Integer>();
		for ( int i=0; i<4; i++ )
			cachedMap.put(i, i*10);
		cache.put("D", cachedMap);
		assertEquals( 4, cachedMap.size() );
		assertEquals( 1, cache.size() );
		cache.clearCache();
		assertEquals( 0, cache.size() );
		assertEquals( 0, cachedMap.size() );
	}
}
