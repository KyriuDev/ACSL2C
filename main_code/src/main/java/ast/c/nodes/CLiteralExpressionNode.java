package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        LiteralExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTLiteralExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CLiteralExpressionNode extends CBaseNode
{
	private final String value;

	//Constructors

	public CLiteralExpressionNode(final String value)
	{
		this.value = value;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format("- Literal expression \"%s\" has ", this.value);
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createLiteralExpressionNode(this.value);
	}

	//Public methods

	public String getValue()
	{
		return this.value;
	}
}
