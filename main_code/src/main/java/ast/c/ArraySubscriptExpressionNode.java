package ast.c;

import ast.AbstractSyntaxNode;
import misc.Utils;

/**
 * Name:        ArraySubscriptExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTArraySubscriptExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class ArraySubscriptExpressionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Array subscript expression has ";
	}
}
