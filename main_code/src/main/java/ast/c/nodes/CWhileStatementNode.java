package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

public class CWhileStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- While statement has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createWhileStatementNode();
	}

	@Override
	public String checkWellFormedness()
	{
		if (this.getChildren().size() != 2)
		{
			return String.format(
				"WhileStatementNode is malformed: expected exactly 2 children, got %d",
				this.getChildren().size()
			);
		}

		return null;
	}
}
