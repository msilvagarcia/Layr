package org.layr.engine.expressions;

import org.layr.engine.IRequestContext;

public abstract class PlaceHolder {

	IRequestContext context;
	String placeHolderExpression;

	public PlaceHolder(
			IRequestContext context,
			String placeHolderExpression) {
		this.context = context;
		this.placeHolderExpression = placeHolderExpression;
	}

	public abstract Object eval();

}
