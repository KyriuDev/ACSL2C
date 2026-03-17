package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslClauseKind;
import constants.acsl.others.AcslType;
import misc.Utils;

public class EnsuresClauseNode extends AbstractSyntaxNode
{
	private AcslClauseKind clauseKind;

	//Constructors

	public EnsuresClauseNode()
	{
		this(null);
	}

	public EnsuresClauseNode(final AcslClauseKind clauseKind)
	{
		super(AcslType.ENSURES_CLAUSE);
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
					"- Ensures clause%s has ",
					this.clauseKind == AcslClauseKind.NONE ? "" : "\"" + this.clauseKind.getName() + "\""
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
			return "Ensures clause node is malformed: it should have a non-null clause kind.";
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
}
