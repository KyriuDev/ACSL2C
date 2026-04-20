package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        CBreakStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTBreakStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    09/04/26
 */

public class CBreakStatementNode extends CBaseNode
{
	@Override
	public String getNodeHeader()
	{
		return "- Break statement has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createBreakStatementNode();
	}
}
