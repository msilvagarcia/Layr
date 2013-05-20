package layr.commons;

import static org.junit.Assert.*;
import layr.commons.StringUtil;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void grantThatScapeTheString(){
		String escapedString = StringUtil.escape("<h1>It really works!</h1>");
		assertEquals( "&lt;h1&gt;It really works!&lt;/h1&gt;", escapedString );
	}
	
	@Test
	public void grantThatJoinString(){
		String[] strings = new String[]{ "Helden", "is", "a", "legendary", "hero!" };
		String jointString = StringUtil.join(strings, " ");
		assertEquals( "Helden is a legendary hero!", jointString );
	}
	
	@Test
	public void grantThatMacthString(){
		String word = "Começo";
		String matchedString = StringUtil.match(word, "comeco");
		assertEquals( word, matchedString );
	}
	
	@Test
	public void grantThatStringIsEmpty(){
		assertTrue( StringUtil.isEmpty(null) );
		assertTrue( StringUtil.isEmpty("") );
	}
	
	@Test
	public void grantThatStringIsNotEmpty(){
		assertFalse( StringUtil.isEmpty("Not empty string") );
	}
}
