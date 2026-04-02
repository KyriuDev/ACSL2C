package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        ArrayDeclaratorNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTArrayDeclarator" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CArrayDeclaratorNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Array declarator has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createArrayDeclaratorNode();
	}
}
