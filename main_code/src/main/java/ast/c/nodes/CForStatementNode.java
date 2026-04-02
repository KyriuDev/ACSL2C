package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

/**
 * Name:        ForStatementNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTForStatement" class.
 * 				This node follows the CASTForStatement convention for its content, that is:
 * 				- Child 1 (mandatory): either a NullStatementNode if the "for" has no condition (equivalent to a "while
 * 			      true") or a statement representing the part between the left bracket "(" and the first semicolon ";"
 * 			      of the "for".
 * 				- Child 2 (mandatory): either a statement representing the middle part of the "for" header (between the
 * 				  first and the second semicolon) or a statement describing the body of the "for" loop.
 * 				  It represents the body of the "for" loop if and only if it is the last child of this node.
 * 				- Child 3 (optional): either a statement representing the right part of the "for" header (between the
 *				  last semicolon and the right bracket) or a statement describing the body of the "for" loop.
 *  			  It represents the body of the "for" loop if and only if it is the last child of this node.
 * 				- Child 4 (optional): the body of the "for" loop if not previously defined.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CForStatementNode extends CBaseNode
{
	//Overrides

	@Override
	public String getNodeHeader()
	{
		return "- For statement has ";
	}

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createForStatementNode();
	}
}
