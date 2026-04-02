package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        ExpressionStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTExpressionStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CExpressionStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Expression statement has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createExpressionStatementNode();
	}
}
