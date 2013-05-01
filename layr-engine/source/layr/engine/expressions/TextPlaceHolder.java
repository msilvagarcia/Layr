package layr.engine.expressions;

import layr.engine.RequestContext;

public class TextPlaceHolder extends PlaceHolder {

	public TextPlaceHolder(
			RequestContext context,
			String placeHolderExpression) {
		super( context, placeHolderExpression );
	}

	@Override
	public String eval() {
		return placeHolderExpression;
	}

}
