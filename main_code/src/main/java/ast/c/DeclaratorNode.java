package ast.c;

import ast.AbstractSyntaxNode;
import misc.Utils;

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
}
