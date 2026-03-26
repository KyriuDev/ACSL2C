package ast.acsl;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslClauseKind;
import constants.acsl.others.AcslType;
import misc.Utils;

/**
 * Name:        RequiresClauseNode.java
 * Content:	    This class defines a RequiresClauseNode with a kind that classical nodes do not have.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public class RequiresClauseNode extends AcslBaseNode
{
	private AcslClauseKind clauseKind;

	//Constructors

	public RequiresClauseNode()
	{
		this(null);
	}

	public RequiresClauseNode(final AcslClauseKind clauseKind)
	{
		super(AcslType.REQUIRES_CLAUSE);
		this.clauseKind = clauseKind;
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format(
					"- Requires clause%s has ",
					this.clauseKind == AcslClauseKind.NONE ? "" : this.clauseKind.getName()
				));

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

	@Override
	public String checkWellFormedness()
	{
		if (this.clauseKind == null)
		{
			return "Requires clause node is malformed: it should have a non-null clause kind.";
		}

		return null;
	}

	//Public methods

	public void setClauseKind(final AcslClauseKind clauseKind)
	{
		this.clauseKind = clauseKind;
	}

	public AcslClauseKind getClauseKind()
	{
		return this.clauseKind;
	}

	//Private methods
}
