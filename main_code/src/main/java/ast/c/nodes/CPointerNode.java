package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        PointerNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTPointer" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CPointerNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Pointer (*) has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createPointerNode();
	}
}
