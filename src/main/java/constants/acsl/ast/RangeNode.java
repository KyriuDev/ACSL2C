package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import misc.Utils;

/**
 * Name:        RangeNode.java
 * Content:	    This class defines a RangeNode representing a range between an upper bound and a lower bound.
 * 				According to the ACSL manual, ranges do not require bounds, in which case the range can take any correct
 * 				value in its field of definition (-2,147,483,648 to 2,147,483,647 for a C "int" for instance).
 * 				For instance, the expression "t+(0 .. n-1)" will be represented as a BinaryOperationNode having two
 * 				children, a PredicateOrTermNode representing "t", and a RangeNode representing "0 .. n-1".
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class RangeNode extends PredicateOrTermNode
{
	//Constructors

	public RangeNode()
	{
		super(AcslPredicateOrTermKind.RANGE);
	}

	public RangeNode(final String content)
	{
		super(AcslPredicateOrTermKind.RANGE, content);
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append("- Range has ");

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
