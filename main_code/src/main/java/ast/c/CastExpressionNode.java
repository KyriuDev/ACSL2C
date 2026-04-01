package ast.c;

/**
 * Name:        CastExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTCastExpression" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CastExpressionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Cast expression node has ";
	}
}
