package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        InitializerListNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTInitializerList" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    02/04/26
 */

public class CInitializerListNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Initializer list has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createInitializerListNode();
	}
}
