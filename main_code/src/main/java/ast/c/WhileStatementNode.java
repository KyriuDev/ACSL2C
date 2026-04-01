package ast.c;

public class WhileStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- While statement has ";
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
