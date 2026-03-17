package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import misc.Utils;

/**
 * Name:        IdentifierNode.java
 * Content:	    This class defines an IdentifierNode whose content is the string representation of the identifier.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class IdentifierNode extends PredicateOrTermNode
{
	//Constructors

	public IdentifierNode(final String content)
	{
		super(AcslPredicateOrTermKind.IDENTIFIER, content);
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
			return "Identifier node is malformed: it should have a non-null non-empty content.";
		}

		return null;
	}

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format("- Identifier \"%s\" has ", this.content == null ? null : this.content));

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

	//Public methods

	//Private methods

}
