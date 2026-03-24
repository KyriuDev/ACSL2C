package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslType;
import constants.acsl.others.AcslVariableIdentifierKind;
import misc.Utils;

/**
 * Name:        VariableIdentifierNode.java
 * Content:	    This class defines a VariableIdentifierNode with a kind that classical nodes do not have.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    18/03/26
 */

public class VariableIdentifierNode extends AcslBaseNode
{
	private AcslVariableIdentifierKind kind;
	protected String content;

	//Constructors

	public VariableIdentifierNode()
	{
		this(AcslVariableIdentifierKind.IDENTIFIER);
	}

	public VariableIdentifierNode(final String content)
	{
		this(AcslVariableIdentifierKind.IDENTIFIER, content);
	}

	public VariableIdentifierNode(final AcslVariableIdentifierKind kind)
	{
		this(kind, null);
	}

	public VariableIdentifierNode(final AcslVariableIdentifierKind kind,
								  final String content)
	{
		super(AcslType.VARIABLE_IDENTIFIER);
		this.kind = kind;
		this.content = content;
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format("- Variable identifier \"%s\" has ", this.content));

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

	public String getContent()
	{
		return this.content;
	}

	public void setContent(final String content)
	{
		this.content = content;
	}

	//Private methods
}

