package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslClauseKind;
import constants.acsl.others.AcslType;
import misc.Utils;

public class MemoryAllocationSetNode extends AbstractSyntaxNode
{
	private boolean isEmpty;

	//Constructors

	public MemoryAllocationSetNode()
	{
		super(AcslType.MEMORY_ALLOCATION_SET);
	}

	public MemoryAllocationSetNode(final boolean isEmpty)
	{
		super(AcslType.MEMORY_ALLOCATION_SET);
		this.isEmpty = isEmpty;
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format("- %s memory allocation set has ", this.isEmpty ? "Empty" : "Non-empty"));

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

	public void setEmpty(final boolean isEmpty)
	{
		this.isEmpty = isEmpty;
	}

	public boolean isEmpty()
	{
		return this.isEmpty;
	}
}
