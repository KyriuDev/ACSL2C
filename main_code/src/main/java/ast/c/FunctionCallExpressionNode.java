package ast.c;

import ast.AbstractSyntaxNode;
import misc.Utils;

/**
 * Name:        FunctionCallExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTFunctionCallExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class FunctionCallExpressionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Function call expression has";
	}
}
