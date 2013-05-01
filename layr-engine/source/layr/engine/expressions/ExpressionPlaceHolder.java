package layr.engine.expressions;

import static layr.commons.Reflection.*;
import layr.engine.RequestContext;

public class ExpressionPlaceHolder extends PlaceHolder {

	public ExpressionPlaceHolder(
			RequestContext context,
			String placeHolderExpression) {
		super( context, placeHolderExpression );
	}

	/* (non-Javadoc)
	 * @see org.layr.engine.expressions.PlaceHolder#eval()
	 */
	@Override
	public Object eval() {
		String[] expressions = stripAttribute( placeHolderExpression );
		Object target = context.get( expressions[0] );
		if ( target == null )
			return "";
		else if ( expressions.length != 2 )
			return target;
		return tryToGetAttribute( expressions[1], target );
	}

	public Object tryToGetAttribute(String attribute, Object target) {
		Object attributeValue = getAttribute( target, attribute );
		return attributeValue == null ? "" : attributeValue;
	}

}
