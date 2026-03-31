package ast.c;

/**
 * Name:        ArrayDeclaratorNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTArrayDeclarator" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class ArrayDeclaratorNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Array declarator has ";
	}
}
