package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        DeclarationStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTDeclarationStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CDeclarationStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Declaration statement has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createDeclarationStatementNode();
	}
}
