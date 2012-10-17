package layr.core.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import layr.core.commons.gson.DefaultDataParser;
import layr.core.sample.World;

import org.junit.Test;

import com.google.gson.Gson;

public class JSONParsingTest {
	
	List<World> worlds;

	@Test
	public void grantThatJSONParserWorksWithObject(){
		String worldAsString = "{\"name\":\"name\",\"hello\":null,\"id\":1}";
		World world = new Gson().fromJson(worldAsString, World.class);
		assertNotNull(world);
		assertNull( world.getHello() );
		assertEquals( "name", world.getName() );
		assertEquals( (Long)1L, world.getId() );
	}

	@Test
	public void grantThatJSONParserWorksWithObjectArray() {
		String worldAsString = "{\"worlds\": [" +
					"{\"name\":\"name1\",\"hello\":null,\"id\":1}," +
					"{\"name\":\"name2\",\"hello\":null,\"id\":2, \"somethingBad\": \"Pagode\"}," +
					"{\"name\":\"name3\",\"hello\":null,\"id\":3}" +
				"]}";
		
		DefaultDataParser parser = new DefaultDataParser();
		Object decoded = parser.decode(worldAsString, JSONParsingTest.class, null);
		
		assertNotNull(decoded);
		assertTrue(JSONParsingTest.class.isInstance(decoded));
		
		JSONParsingTest decodedObject = (JSONParsingTest)decoded;
		assertEquals(3, decodedObject.worlds.size());
		assertEquals((Long)2L, decodedObject.worlds.get(1).getId());
		assertNull(decodedObject.worlds.get(1).getSomethingBad());

	}
	
}
