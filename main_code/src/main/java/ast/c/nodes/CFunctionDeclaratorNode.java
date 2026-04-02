package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        FunctionDeclaratorNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTFunctionDeclarator" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CFunctionDeclaratorNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Function declarator has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createFunctionDeclaratorNode();
	}
}
