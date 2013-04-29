package org.layr.engine.expressions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.layr.engine.IRequestContext;

public class Evaluator implements Iterable<PlaceHolder> {

	ExpressionPlaceHolderParser parser;

	public Evaluator( IRequestContext layrContext, String expression ) {
		parser = new ExpressionPlaceHolderParser( layrContext, expression );
	}

	/**
	 * @return
	 */
	public String eval(){
		return eval( this );
	}

	/**
	 * @param iterable
	 * @return
	 */
	public String eval( Iterable<PlaceHolder> iterable ){
		StringBuilder buffer = new StringBuilder();

		for ( PlaceHolder placeHolder : iterable )
			buffer.append( placeHolder.eval() );

		return buffer.toString();
	}
	
	/**
	 * @return
	 */
	public Object parse(){
		List<PlaceHolder> list = new ArrayList<PlaceHolder>();
		for ( PlaceHolder placeHolder : this )
			list.add( placeHolder );
		
		if ( list.size() == 1 )
			return list.get( 0 ).eval();
		
		return eval( list );
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<PlaceHolder> iterator() {
		parser.reset();
		return parser;
	}

}
