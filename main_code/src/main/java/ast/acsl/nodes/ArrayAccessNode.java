package ast.acsl.nodes;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;
import misc.Utils;

/**
 * Name:        ArrayAccessNode.java
 * Content:	    This class defines an ArrayAccessNode representing an array access, and whose children represent the
 * 				index at which the array is accessed and the array itself.
 * 				For instance the expression "t[10]" will be represented as an ArrayAccessNode whose children are of type
 * 				AcslType.INDEX and AcslType.PREDICATE_OR_TERM, representing respectively the index (10), and the array
 * 				(t).
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class ArrayAccessNode extends PredicateOrTermNode
{
	//Constructors

	public ArrayAccessNode()
	{
		super(AcslPredicateOrTermKind.ARRAY_ACCESS);
	}

	public ArrayAccessNode(final String content)
	{
		super(AcslPredicateOrTermKind.ARRAY_ACCESS, content);
	}

	//Overrides

	@Override
	public String checkWellFormedness()
	{
		if (this.getChildren().isEmpty()
			|| ((AcslBaseNode) this.getChildren().get(0)).getType() != AcslType.INDEX)
		{
			return String.format(
				"Array access node is malformed:" +
				"\n\t- Expected 2 children, got %d;" +
				"\n\t- Expected first child to be an index, got a \"%s\".",
				this.getChildren().size(),
				!this.getChildren().isEmpty() ? ((AcslBaseNode) this.getChildren().get(0)).getType().getReadableName() : null
			);
		}

		return null;
	}

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append("- Array access has ");

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
