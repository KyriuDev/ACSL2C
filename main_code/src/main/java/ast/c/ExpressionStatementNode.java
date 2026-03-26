package ast.c;

import ast.AbstractSyntaxNode;
import misc.Utils;

/**
 * Name:        ExpressionStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTExpressionStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class ExpressionStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Expression statement has ";
	}
}
