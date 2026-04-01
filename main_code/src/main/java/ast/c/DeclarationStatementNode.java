package ast.c;

/**
 * Name:        DeclarationStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTDeclarationStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class DeclarationStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Declaration statement has ";
	}
}
