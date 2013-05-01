package layr.engine.expressions;

import java.util.Iterator;

import layr.engine.RequestContext;


public class ExpressionPlaceHolderParser implements Iterator<PlaceHolder> {

	static final String PLACEHOLDER_END = "}";
	static final String PLACEHOLDER_START = "#{";

	RequestContext layrContext;
	String expression;
	int cursor;

	public ExpressionPlaceHolderParser( RequestContext layrContext, String expression ) {
		this.layrContext = layrContext;
		this.expression = expression;
		this.cursor = -1;
	}
	
	public void reset() {
		this.cursor = -1;
	}

	@Override
	public boolean hasNext() {
		return cursor < expression.length() - 1;
	}

	@Override
	public PlaceHolder next() {
		PlaceHolder placeHolder = null;
		
		int nextExpressionStart = whereIsTheNextExpressionStart();
		if ( cursor + 1 == nextExpressionStart )
			placeHolder = parseAsExpression( nextExpressionStart );
		else
			placeHolder = parseAsString( nextExpressionStart );

		return placeHolder;
	}

	public int whereIsTheNextExpressionStart() {
		int start = Math.max( cursor,
				expression.indexOf( PLACEHOLDER_START, cursor ));
		if ( start == cursor )
			start = expression.length();
		return start;
	}

	public PlaceHolder parseAsExpression( int start ) {
		int end = expression.indexOf( PLACEHOLDER_END, start );
		if ( end == -1 )
			throw new RuntimeException("Invalid Expression");
		cursor = end;

		String expression = this.expression.substring( start + 2, end );
		return createExpressionPlaceHolder( expression );
	}

	public ExpressionPlaceHolder createExpressionPlaceHolder(String expression) {
		return new ExpressionPlaceHolder( layrContext, expression );
	}

	public PlaceHolder parseAsString(int nextExpressionStart) {
		String expression = this.expression.substring( cursor + 1, nextExpressionStart );
		cursor = nextExpressionStart - 1;
		return createTextPlaceHolder( expression );
	}

	public TextPlaceHolder createTextPlaceHolder(String expression) {
		return new TextPlaceHolder( layrContext, expression );
	}

	@Override
	public void remove() {}

}
