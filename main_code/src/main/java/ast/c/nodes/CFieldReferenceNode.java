package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        FieldReferenceNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTFieldReference" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class CFieldReferenceNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Field reference has ";
	}

	@Override
	public String checkWellFormedness()
	{
		if (this.getChildren().size() != 2)
		{
			return String.format(
				"FieldReferenceNode is malformed: it was expected to have exactly 2 children, but has %d",
				this.getChildren().size()
			);
		}

		return null;
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createFieldReferenceNode();
	}
}
