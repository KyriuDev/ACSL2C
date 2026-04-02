package ast.acsl.nodes;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import misc.Utils;

/**
 * Name:        OldNode.java
 * Content:	    This class defines an OldNode representing the "\old" ACSL operand.
 * 				It must have exactly one PredicateOrTermNode child describing the content of this operand.
 * 				For instance, the expression "\old(a)" will be represented as an OldNode having a PredicateOrTermNode
 * 				child representing "a".
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class OldNode extends PredicateOrTermNode
{
	//Constructors

	public OldNode()
	{
		super(AcslPredicateOrTermKind.OLD);
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append("- Old (\"\\old\") has ");

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


}
