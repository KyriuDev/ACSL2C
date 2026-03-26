package ast.c;

import ast.AbstractSyntaxNode;
import misc.Utils;

/**
 * Name:        LiteralExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTLiteralExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class LiteralExpressionNode extends CBaseNode
{
	private final String value;

	//Constructors

	public LiteralExpressionNode(final String value)
	{
		this.value = value;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format("- Literal expression \"%s\" has ", this.value);
	}

	//Public methods

	public String getValue()
	{
		return this.value;
	}
}
