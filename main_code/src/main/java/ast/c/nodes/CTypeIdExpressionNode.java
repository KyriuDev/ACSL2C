package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;
import constants.c.CTypeIdExpression;

/**
 * Name:        TypeIdExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTTypeIdExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CTypeIdExpressionNode extends CBaseNode
{
	private final CTypeIdExpression typeIdExpression;

	//Constructors

	public CTypeIdExpressionNode(final CTypeIdExpression typeIdExpression)
	{
		this.typeIdExpression = typeIdExpression;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format("- Type id expression \"%s\" has ", this.typeIdExpression.toString());
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createTypeIdExpressionNode(this.typeIdExpression);
	}

	@Override
	public String checkWellFormedness()
	{
		if (this.getChildren().size() != 1)
		{
			return String.format(
				"TypeIdExpressionNode is malformed: expected exactly 1 child, got %d",
				this.getChildren().size()
			);
		}

		return null;
	}

	//Public methods

	public CTypeIdExpression getTypeIdExpression()
	{
		return this.typeIdExpression;
	}
}
