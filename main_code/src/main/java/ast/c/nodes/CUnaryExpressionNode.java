package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;
import constants.c.CUnaryOperator;

/**
 * Name:        UnaryExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTUnaryExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CUnaryExpressionNode extends CBaseNode
{
	private final CUnaryOperator operator;

	//Constructors

	public CUnaryExpressionNode(final CUnaryOperator operator)
	{
		this.operator = operator;
	}

	//Overrides

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createUnaryExpressionNode(this.operator);
	}

	@Override
	public boolean collapse()
	{
		if (true)
		{
			return false;
		}

		boolean collapsed = false;

		if (this.operator == CUnaryOperator.SIZEOF)
		{
			AbstractSyntaxNode childToRemove = null;

			for (final AbstractSyntaxNode child : this.getChildren())
			{
				if (child.hasSuccessorOfType(CUnaryExpressionNode.class))
				{
					if (((CUnaryExpressionNode) child).getOperator() == CUnaryOperator.BRACKETS)
					{
						/*
							As the "sizeof" operator is actually a unary operator and not a function, parenthesis are
							not required around it, although it is the best practice to use them.
							Thus, Eclipse-CDT adds a (in my humble opinion) useless child UnaryExpressionNode
							representing brackets, which is thus removed by this function.
						*/
						collapsed = true;

						if (childToRemove != null)
						{
							throw new RuntimeException(
								"There should not be multiple UnaryExpressionNode of type \"()\" as children of " +
								"the \"sizeof\" operator!"
							);
						}

						childToRemove = child;
					}
				}
			}

			if (childToRemove != null)
			{
				this.removeChildAndForceParent(childToRemove);

				for (AbstractSyntaxNode childToRemoveChild : childToRemove.getChildren())
				{
					this.addChildAndForceParent(childToRemoveChild);
				}

				childToRemove.removeAllChildrenAndForceParent();
			}
		}

		return collapsed;
	}

	@Override
	public String getNodeHeader()
	{
		return String.format("- Unary expression \"%s\" has ", this.operator.getOperator());
	}

	//Public methods

	public CUnaryOperator getOperator()
	{
		return this.operator;
	}
}
