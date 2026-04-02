package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        SimpleDeclarationNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTSimpleDeclaration" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CSimpleDeclarationNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Simple declaration has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createSimpleDeclarationNode();
	}
}
