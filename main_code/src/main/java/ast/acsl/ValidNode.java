package ast.acsl;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import misc.Utils;

/**
 * Name:        ValidNode.java
 * Content:	    This class defines a ValidNode representing the "\valid(...)" ACSL operand that is used to assess the
 * 				validity of a memory location.
 * 				It must have exactly one PredicateOrTermNode child describing the content of this operand.
 * 				For instance, the expression "\valid(p)" will be represented as a ValidNode having a PredicateOrTermNode
 * 				child representing (the memory location) "p".
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class ValidNode extends PredicateOrTermNode
{
	//Constructors

	public ValidNode()
	{
		super(AcslPredicateOrTermKind.VALID);
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append("- Valid node (\"\\valid\") has ");

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
