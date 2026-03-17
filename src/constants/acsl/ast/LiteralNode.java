package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import misc.Utils;

public class LiteralNode extends PredicateOrTermNode
{
	//Constructors

	public LiteralNode(final String content)
	{
		super(AcslPredicateOrTermKind.LITERAL, content);
	}

	//Overrides

	/**
	 * This allows content to be retrieved.
	 *
	 * @return the content of the node
	 */
	@Override
	public String getContent()
	{
		return this.content;
	}

	@Override
	public String checkWellFormedness()
	{
		if (this.content == null
			|| this.content.trim().isEmpty())
		{
			return "Literal node is malformed: it should have a non-null non-empty content.";
		}

		return null;
	}

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format("- Literal \"%s\" has ", this.content == null ? null : this.content));

		if (this.getChildren().isEmpty())
		{
			builder.append("no child.");
		}
		else if (this.getChildren().size() == 1)
		{
			builder.append("1 child:");
			this.getChildren().iterator().next().stringify(builder, depth + 1);
		}
		else
		{
			builder.append(String.format("%d children:", this.getChildren().size()));

			for (final AbstractSyntaxNode child : this.getChildren())
			{
				child.stringify(builder, depth + 1);
			}
		}
	}
}
