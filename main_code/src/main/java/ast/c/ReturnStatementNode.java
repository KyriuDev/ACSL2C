package ast.c;

/**
 * Name:        ReturnStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTReturnStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class ReturnStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Return statement has ";
	}
}
