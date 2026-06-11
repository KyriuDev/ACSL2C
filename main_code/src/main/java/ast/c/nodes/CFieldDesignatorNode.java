package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        CFieldDesignatorNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTFieldDesignator" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    10/06/26
 */

public class CFieldDesignatorNode extends CBaseNode
{
	@Override
	public String getNodeHeader()
	{
		return "- Field designator has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createFieldDesignatorNode();
	}
}
