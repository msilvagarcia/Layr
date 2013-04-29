package org.layr.engine.expressions;

import org.layr.engine.IRequestContext;

public class TextPlaceHolder extends PlaceHolder {

	public TextPlaceHolder(
			IRequestContext context,
			String placeHolderExpression) {
		super( context, placeHolderExpression );
	}

	@Override
	public String eval() {
		return placeHolderExpression;
	}

}
