package ast.c;

import ast.AbstractSyntaxNode;

/**
 * Name:        TranslationUnitNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTTranslationUnit" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class TranslationUnitNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Translation unit has ";
	}

	@Override
	public boolean collapse()
	{
		boolean collapsed = false;

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			if (child.hasSuccessorOfType(FunctionDeclaratorNode.class))
			{
				/*
					This is purely personal taste, but I like to differentiate clearly between function declaration and
					function definition, thus we replace this child by a FunctionDeclaratorNode.
			 	*/
			}
		}

		return collapsed;
	}
}
