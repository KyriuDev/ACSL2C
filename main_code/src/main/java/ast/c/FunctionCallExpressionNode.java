package ast.c;

/**
 * Name:        FunctionCallExpressionNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTFunctionCallExpression" class.
 * 				This node follows the CASTFunctionCallExpression convention for its content, that is:
 * 				- Child 1 (mandatory): IdExpressionNode describing the name of the function
 * 				- Child 2 (optional): representation of argument 1
 * 				- ....
 * 				- Child n (optional): representation of argument n - 1
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class FunctionCallExpressionNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- Function call expression has ";
	}
}
