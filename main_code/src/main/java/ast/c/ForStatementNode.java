package ast.c;

import ast.AbstractSyntaxNode;
import misc.Utils;

/**
 * Name:        ForStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTForStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class ForStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- For statement has ";
	}
}
