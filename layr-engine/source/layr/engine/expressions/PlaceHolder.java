package layr.engine.expressions;

import layr.engine.RequestContext;

public abstract class PlaceHolder {

	RequestContext context;
	String placeHolderExpression;

	public PlaceHolder(
			RequestContext context,
			String placeHolderExpression) {
		this.context = context;
		this.placeHolderExpression = placeHolderExpression;
	}

	public abstract Object eval();

}
