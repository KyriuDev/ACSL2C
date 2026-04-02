package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        FunctionDefinitionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTFunctionDefinition" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CFunctionDefinitionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Function definition has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createFunctionDefinitionNode();
	}
}
