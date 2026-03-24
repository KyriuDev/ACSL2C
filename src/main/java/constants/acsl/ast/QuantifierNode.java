package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import misc.Utils;

/**
 * Name:        QuantifierNode.java
 * Content:	    This class defines a QuantifierNode representing a mathematical quantifier like "forall" or "exists".
 * 				It has exactly two children nodes, a node of type AcslType.BINDERS representing the information of the
 * 			    variables being defined, and a PredicateOrTermNode representing	the "usage" of the previously defined
 * 			    variables.
 * 			    For instance, the expression "forall integer k; 0 <= k <= n" will be represented as a QuantifierNode
 * 			    whose binders will be (the internal representation of) "integer k", and whose predicate will be (the
 * 			    internal representation of) "0 <= k <= n".
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class QuantifierNode extends PredicateOrTermNode
{
	//Constructors

	public QuantifierNode(final AcslPredicateOrTermKind kind)
	{
		this(kind, null);
	}

	public QuantifierNode(final AcslPredicateOrTermKind kind,
						  final String content)
	{
		super(kind, content);
	}

	//Overrides

	@Override
	public String checkWellFormedness()
	{
		if ((this.getKind() != AcslPredicateOrTermKind.EXISTS
			&& this.getKind() != AcslPredicateOrTermKind.FOR_ALL)
			|| this.getChildren().isEmpty())
		{
			return String.format(
				"Quantifier node is malformed:" +
				"\n\t- Expected either \"%s\" or \"%s\" kinds, got \"%s\";" +
				"\n\t- Expected at least 1 child, got 0;",
				AcslPredicateOrTermKind.EXISTS.getName(),
				AcslPredicateOrTermKind.FOR_ALL.getName(),
				this.getKind().getName()
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
				.append(String.format("- Quantifier (%s) has ", this.getKind().getName()));

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
