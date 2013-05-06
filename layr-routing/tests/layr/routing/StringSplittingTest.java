package layr.routing;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class StringSplittingTest {

	private static final String HELLO_WORLD = "/hello/world";
	private static final int MANY_TIMES = 100000;

	@Test
	public void grantThatSplitterWorksAsExpected(){
		String[] splitted = null;

		splitted = split( HELLO_WORLD, '?' );
		assertNotNull( splitted );
		assertEquals( 1, splitted.length );
		assertEquals( HELLO_WORLD, splitted[0]);

		splitted = split( "/hello/world?blah=123", '?' );
		assertNotNull( splitted );
		assertEquals( 2, splitted.length );
		assertEquals( HELLO_WORLD, splitted[0]);
		assertEquals( "blah=123", splitted[1]);
	}

	@Test
	public void grantThatSplitWithRegExpAsExpected(){
		String[] splitted = null;

		splitted = splitWithRegExp( HELLO_WORLD, "\\?" );
		assertNotNull( splitted );
		assertEquals( 1, splitted.length );
		assertEquals( HELLO_WORLD, splitted[0]);

		splitted = splitWithRegExp( "/hello/world?blah=123", "\\?" );
		assertNotNull( splitted );
		assertEquals( 2, splitted.length );
		assertEquals( HELLO_WORLD, splitted[0]);
		assertEquals( "blah=123", splitted[1]);
	}
	
	@Test( timeout = 85 )
	public void grantThatSplitManyTimes(){
		for ( int i=0; i<MANY_TIMES; i++ )
			grantThatSplitterWorksAsExpected();
	}

	@Test( timeout= 485 )
	public void grantThatSplitWithRegExpManyTimes(){
		for ( int i=0; i<MANY_TIMES; i++ )
			grantThatSplitWithRegExpAsExpected();
	}
	
	String[] split( String uri, char divider ) {
		return new StringSplitter( uri, divider ).split();
	}
	
	String[] splitWithRegExp( String uri, String divider ) {
		if ( !uri.contains( divider.replace( "\\", "" ) ) )
			return new String[]{ uri };
		return uri.split(divider);
	}

}

class StringSplitter {
	
	String target;
	int cursor;
	char delimiter;
	private String[] splittedStrings;
	private int counter;

	public StringSplitter( String target, char delimiter ) {
		this.target = target;
		this.delimiter = delimiter;
	}
	
	public String[] split(){
		reset();

		while ( notTheEndOfString() ) {
			int nextDelimiterPosition = nextDelimiterPosition();
			String substring = target.substring( cursor, nextDelimiterPosition );
			append( substring );
			cursor = nextDelimiterPosition + 1;
			counter++;
		}

		return splittedStrings;
	}

	public void reset() {
		splittedStrings = new String[0];
		cursor = 0;
		counter = 0;
	}

	public boolean notTheEndOfString() {
		return cursor < target.length();
	}

	public int nextDelimiterPosition() {
		int nextDelimiterPosition = target.indexOf( delimiter, cursor );
		return nextDelimiterPosition >= 0 ? nextDelimiterPosition : target.length();
	}

	public void append(String substring) {
		splittedStrings = Arrays.copyOf( splittedStrings, splittedStrings.length + 1 );
		splittedStrings[counter] = substring;
	}
}
