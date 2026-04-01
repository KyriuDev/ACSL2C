package ast.c;

import ast.AbstractSyntaxNode;

/**
 * Name:        DeclaratorNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTDeclarator" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class DeclaratorNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Declarator has ";
	}

	@Override
	public boolean collapse()
	{
		final AbstractSyntaxNode firstChild = this.getFirstChild();

		if (firstChild instanceof NameNode
			&& ((NameNode) firstChild).getValue().isEmpty())
		{
			/*
				The name node contains an empty name, thus nothing will be dumped.
				Removing it now may avoid some undesired spaces in the dumped file.
			 */
			this.removeChildAndForceParent(firstChild);
			return true;
		}

		return false;
	}
}
