package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        ArrayModifierNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTArrayModifier" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CArrayModifierNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Array modifier ([]) has ";
	}

	@Override
	public String checkWellFormedness()
	{
		if (this.getChildren().size() > 1)
		{
			return String.format(
				"ArrayModifierNode is malformed: expected at most 1 child, got %d.",
				this.getChildren().size()
			);
		}

		return null;
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createArrayModifierNode();
	}
}
