package ast.c;

/**
 * Name:        NullStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTNullStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class NullStatementNode extends CBaseNode
{
	@Override
	public String getNodeHeader()
	{
		return "- Null statement has ";
	}
}
