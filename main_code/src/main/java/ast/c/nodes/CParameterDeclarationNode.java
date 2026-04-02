package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Name:        ParameterDeclarationNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTParameterDeclaration" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CParameterDeclarationNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Parameter declaration has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createNullStatementNode();
	}

	@Override
	public boolean collapse()
	{
		boolean collapsed = false;

		final ArrayList<AbstractSyntaxNode> childrenToRemove = new ArrayList<>();

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			if (child instanceof CDeclaratorNode
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

	//Public methods

	public boolean hasNameIn(final Collection<String> names)
	{
		final AbstractSyntaxNode nameNode = this.getSuccessorOfType(CNameNode.class);

		if (nameNode == null)
		{
			throw new RuntimeException(
				"The current ParameterDeclarationNode does not have any successor of type NameNode!"
			);
		}

		return names.contains(((CNameNode) nameNode).getValue());
	}

	//Private methods


}
