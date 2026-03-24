package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslType;
import misc.Utils;

/**
 * Name:        BehaviorNode.java
 * Content:	    This class defines a BehaviorNode representing a behaviour definition having a specific identifier,
 * 				and whose children are either of type AcslType.ASSUMES_CLAUSE_LIST, AcslType.REQUIRES_CLAUSE_LIST, or
 * 				AcslType.SIMPLE_CLAUSE_LIST.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class BehaviorNode extends AcslBaseNode
{
	private String id;

	//Constructors

	public BehaviorNode()
	{
		this(null);
	}

	public BehaviorNode(final String id)
	{
		super(AcslType.BEHAVIOR);
		this.id = id;
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format(
					"- Behavior \"%s\" has ",
					this.id == null ? null : this.id
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
		if (this.id == null
			|| this.id.trim().isEmpty())
		{
			return "Behavior node is malformed: it must have a non-null non-empty identifier.";
		}

		return null;
	}

	//Public methods

	public void setId(final String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return this.id;
	}
}
