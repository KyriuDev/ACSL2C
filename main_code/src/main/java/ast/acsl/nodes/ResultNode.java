package ast.acsl.nodes;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import misc.Utils;

/**
 * Name:        ResultNode.java
 * Content:	    This class defines a ResultNode corresponding to the ACSL "\result" operator.
 * 				It should not have any child.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */


public class ResultNode extends PredicateOrTermNode
{
	//Constructors

	public ResultNode()
	{
		super(AcslPredicateOrTermKind.RESULT);
	}

	//Overrides

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append("- Result (\"\\result\") has ");

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
		if (!this.getChildren().isEmpty())
		{
			return "ResultNode is malformed: it should not have any child.";
		}

		return null;
	}
}
