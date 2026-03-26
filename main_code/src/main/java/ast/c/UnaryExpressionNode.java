package ast.c;

import constants.c.CUnaryOperator;

/**
 * Name:        UnaryExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTUnaryExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class UnaryExpressionNode extends CBaseNode
{
	private final CUnaryOperator operator;

	//Constructors

	public UnaryExpressionNode(final CUnaryOperator operator)
	{
		this.operator = operator;
	}

	//Overrides

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
