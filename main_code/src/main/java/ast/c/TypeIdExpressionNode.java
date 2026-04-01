package ast.c;

/**
 * Name:        TypeIdExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTTypeIdExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public class TypeIdExpressionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Type id expression has ";
	}
}
