package ast.acsl;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;
import constants.c.CBinaryOperator;
import misc.Utils;

import java.util.ArrayList;

/**
 * Name:        BinaryOperationNode.java
 * Content:	    This class defines a BinaryOperationNode with an operator and (at least) two PredicateOrTerm children
 * 				nodes.
 * 				For instance, the expression "n + m" will be represented as a BinaryOperationNode of operator "+",
 * 				whose children are PredicateOrTermNodes containing respectively "n" and "m".
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public class BinaryOperationNode extends PredicateOrTermNode
{
	private CBinaryOperator operator;

	//Constructors

	public BinaryOperationNode()
	{
		this(null, null);
	}

	public BinaryOperationNode(final String content)
	{
		this(null, content);
	}

	public BinaryOperationNode(final CBinaryOperator operator)
	{
		this(operator, null);
	}

	public BinaryOperationNode(final CBinaryOperator operator,
							   final String content)
	{
		super(AcslPredicateOrTermKind.BINARY_OPERATOR, content);
		this.operator = operator;
	}

	//Overrides

	/**
	 * A binary operation node is considered to be well-formed as long it as a non-null "operator" and exactly two
	 * children being predicates or terms.
	 *
	 * @return null if the current node is well-formed, an error message otherwise.
	 */
	@Override
	public String checkWellFormedness()
	{
		if (this.operator == null
			|| this.getChildren().size() < 2)
		{
			return String.format(
				"Binary operation node is malformed:" +
				"\n\t- Expected non-null operator, got \"%s\";" +
				"\n\t- Expected at least 2 children, got %d;" +
				"\n\t- Expected all children to be predicates or terms.",
				this.operator == null ? null : this.operator.getOperator(),
				this.getChildren().size()
			);
		}

		for (final AbstractSyntaxNode abstractSyntaxNode : this.getChildren())
		{
			if (((AcslBaseNode) abstractSyntaxNode).getType() != AcslType.PREDICATE_OR_TERM)
			{
				return String.format(
					"Binary operation node is malformed:" +
					"\n\t- Expected non-null operator, got \"%s\";" +
					"\n\t- Expected at least 2 children, got %d;" +
					"\n\t- Expected all children to be predicates or terms.",
					this.operator == null ? null : this.operator.getOperator(),
					this.getChildren().size()
				);
			}
		}

		return null;
	}

	/**
	 * A binary operation node can be collapsed in multiple ways:
	 * - First, its OperatorNode child information can be stored directly in the binary operation node itself;
	 * - Second, each of its children being a binary operation node of same operator can be merged with the current one.
	 * This is what this method performs, thus ending with the removal of the no longer useful children.
	 */
	@Override
	public boolean collapse()
	{
		boolean collapsed = false;
		AbstractSyntaxNode childToRemove = null;

		//Incorporate the binary operator
		for (final AbstractSyntaxNode child : this.getChildren())
		{
			if (child instanceof OperatorNode)
			{
				final CBinaryOperator operator = CBinaryOperator.convertXmlOperatorNameToThis(
					((OperatorNode) child).getName()
				);
				this.setOperator(operator);
				childToRemove = child;
				child.removeParent(this);
				collapsed = true;
				/*
					This break is useful to handle malformed BinaryOperationNodes when method "checkWellFormedness()"
					will be called.
					Otherwise, it may silently collapse multiple OperatorNodes children, which is a structural error.
				 */
				break;
			}
		}

		this.removeChild(childToRemove);

		//Merge the eventual identical child operators
		final ArrayList<BinaryOperationNode> childrenToMerge = new ArrayList<>();

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			if (child instanceof BinaryOperationNode)
			{
				if (((BinaryOperationNode) child).getOperator() == this.operator)
				{
					childrenToMerge.add((BinaryOperationNode) child);
				}
			}
		}

		for (final BinaryOperationNode childToMerge : childrenToMerge)
		{
			final int childIndex = this.getChildren().indexOf(childToMerge);

			if (childIndex == -1)
			{
				throw new RuntimeException();
			}

			for (int i = 0; i < childToMerge.getChildren().size(); i++)
			{
				final AbstractSyntaxNode child = childToMerge.getChildren().get(i);
				child.removeParent(childToMerge);
				this.addChildAtIndexAndForceParent(child, childIndex + i);
			}

			this.removeChildAndForceParent(childToMerge);
			childToMerge.removeAllChildren();
		}

		return collapsed || super.collapse();
	}

	@Override
	public void stringify(final StringBuilder builder,
						  final int depth)
	{
		builder.append("\n")
				.append(Utils.repeatTabs(depth))
				.append(String.format(
					"- Binary operation \"%s\" has ",
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

	public void setOperator(final CBinaryOperator operator)
	{
		this.operator = operator;
	}

	public CBinaryOperator getOperator()
	{
		return this.operator;
	}
}
