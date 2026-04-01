package ast.c;

/**
 * Name:        LabelStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTLabelStatement" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class LabelStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Label statement has ";
	}
}
