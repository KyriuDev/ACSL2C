package ast.c;

/**
 * Name:        IfStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTIfStatement" class.
 * 				This node follows the CASTIfStatement convention for its content, that is:
 * 				- Child 1 (mandatory): condition of the "if"
 * 				- Child 2 (mandatory): content of the "if" block (can be empty, but must exist)
 * 				- Child 3 (optional): either an "IfStatementNode" representing an "else if" condition, or a
 * 									  "CompoundStatementNode" representing an "else" condition.
 * 				- ....
 * 				- Child n (optional): either an "IfStatementNode" representing an "else if" condition, or a
 *									  "CompoundStatementNode" representing an "else" condition.
 *				Note that if an optional child is an "IfStatementNode", its previous sibling cannot be a
 *				"CompoundStatementNode" (this would represent an "else if" after an "else").
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class IfStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- If statement has ";
	}

	@Override
	public String checkWellFormedness()
	{
		//TODO
		return null;
	}
}
