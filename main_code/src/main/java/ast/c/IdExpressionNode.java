package ast.c;

/**
 * Name:        IdExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTIdExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class IdExpressionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Id expression has ";
	}
}
