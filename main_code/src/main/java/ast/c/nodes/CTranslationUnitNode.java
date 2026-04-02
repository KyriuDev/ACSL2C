package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        TranslationUnitNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTTranslationUnit" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CTranslationUnitNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Translation unit has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createTranslationUnitNode();
	}
}
