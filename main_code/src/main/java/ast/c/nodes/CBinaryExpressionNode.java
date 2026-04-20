package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;
import constants.c.CBinaryOperator;

/**
 * Name:        CBinaryExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTBinaryExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CBinaryExpressionNode extends CBaseNode
{
	private final CBinaryOperator binaryOperator;

	//Constructors

	public CBinaryExpressionNode(final CBinaryOperator binaryOperator)
	{
		this.binaryOperator = binaryOperator;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format("- Binary operation \"%s\" has ", this.binaryOperator.getOperator());
	}

	//Public methods

	public CBinaryOperator getBinaryOperator()
	{
		return this.binaryOperator;
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createBinaryExpressionNode(this.binaryOperator);
	}
}
