package ast.acsl.nodes;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;
import constants.c.CUnaryOperator;
import misc.Utils;

/**
 * Name:        UnaryOperationNode.java
 * Content:	    This class defines a UnaryOperationNode with a unary operator and a PredicateOrTermNode child.
 *              For instance, the expression "-n" will be represented as a UnaryOperationNode of operator "-" whose
 *              child is a PredicateOrTermNode representing "n".
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    18/03/26
 */

public class UnaryOperationNode extends PredicateOrTermNode
{
	private CUnaryOperator operator;

	//Constructors

	public UnaryOperationNode()
	{
		this(null, null);
	}

	public UnaryOperationNode(final String content)
	{
		this(null, content);
	}

	public UnaryOperationNode(final CUnaryOperator operator)
	{
		this(operator, null);
	}

	public UnaryOperationNode(final CUnaryOperator operator,
							  final String content)
	{
		super(AcslPredicateOrTermKind.UNARY_OPERATOR, content);
		this.operator = operator;
	}

	//Overrides

	/**
	 * A unary operation node is considered to be well-formed as long it as a non-null "operator" and exactly one child
	 * being a predicate or a term.
	 *
	 * @return null if the current node is well-formed, an error message otherwise.
	 */
	@Override
	public String checkWellFormedness()
	{
		if (this.operator == null
			|| this.getChildren().size() != 1
			|| ((AcslBaseNode) this.getChildren().get(0)).getType() != AcslType.PREDICATE_OR_TERM)
		{
			return String.format(
				"Unary operation node is malformed:" +
				"\n\t- Expected non-null operator, got \"%s\";" +
				"\n\t- Expected 1 child, got %d;" +
				"\n\t- Expected the child to be a predicate or a term, got a \"%s\".",
				this.operator == null ? null : this.operator.getOperator(),
				this.getChildren().size(),
				!this.getChildren().isEmpty() ? ((AcslBaseNode) this.getChildren().get(0)).getType().getReadableName() : null
			);
		}

		return null;
	}

	/**
	 * A unary operation node can be collapsed in the sense that its OperatorNode child information can be stored
	 * directly in the node.
	 * This is what this method performs, thus ending with the removal of the no longer useful child.
	 */
	@Override
	public boolean collapse()
	{
		boolean collapsed = false;
		AbstractSyntaxNode childToRemove = null;

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			if (child instanceof OperatorNode)
			{
				final CUnaryOperator operator = CUnaryOperator.convertXmlOperatorNameToThis(
					((OperatorNode) child).getName()
				);
				this.setOperator(operator);
				childToRemove = child;
				child.removeParent(this);
				collapsed = true;
				/*
					This break is useful to handle malformed UnaryOperationNodes when method "checkWellFormedness()"
					will be called.
					Otherwise, it may silently collapse multiple OperatorNodes children, which is a structural error.
				 */
				break;
			}
		}

		this.removeChild(childToRemove);

		return collapsed || super.collapse();
	}

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format(
					"- Unary operation \"%s\" has ",
					this.operator == null ? null : this.operator.getOperator()
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

	//Public methods

	public void setOperator(final CUnaryOperator operator)
	{
		this.operator = operator;
	}

	public CUnaryOperator getOperator()
	{
		return this.operator;
	}
}
