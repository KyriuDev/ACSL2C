package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        TypeIdNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTTypeId" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CTypeIdNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Type id has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createTypeIdNode();
	}
}
