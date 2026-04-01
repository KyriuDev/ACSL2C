package ast.c;

import ast.AbstractSyntaxNode;

import java.util.ArrayList;

/**
 * Name:        ParameterDeclarationNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTParameterDeclaration" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class ParameterDeclarationNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Parameter declaration has ";
	}

	@Override
	public boolean collapse()
	{
		boolean collapsed = false;

		final ArrayList<AbstractSyntaxNode> childrenToRemove = new ArrayList<>();

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			if (child instanceof DeclaratorNode
				&& child.getChildren().isEmpty())
			{
				childrenToRemove.add(child);
			}
			else
			{
				collapsed = collapsed || child.collapse();
			}
		}

		for (final AbstractSyntaxNode childToRemove : childrenToRemove)
		{
			this.removeChildAndForceParent(childToRemove);
		}

		return collapsed || !childrenToRemove.isEmpty();
	}
}
