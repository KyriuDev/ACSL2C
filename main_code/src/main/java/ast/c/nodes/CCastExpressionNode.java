package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        CastExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTCastExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CCastExpressionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Cast expression node has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createCastExpressionNode();
	}
}
