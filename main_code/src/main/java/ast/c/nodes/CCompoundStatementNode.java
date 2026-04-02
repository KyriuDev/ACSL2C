package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        CompoundStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTCompoundStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CCompoundStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Compound statement has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createCompoundStatementNode();
	}
}
