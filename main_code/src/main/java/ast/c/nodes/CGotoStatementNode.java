package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        GotoStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTGotoStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CGotoStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Goto statement has ";
	}

	@Override
	public String checkWellFormedness()
	{
		if (this.getChildren().size() != 1)
		{
			return String.format(
				"GotoStatementNode is malformed: expected exactly 1 child, got %d",
				this.getChildren().size()
			);
		}

		return null;
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createGotoStatementNode();
	}
}
