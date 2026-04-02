package ast.c.nodes;

import ast.AbstractSyntaxNode;
import ast.c.CFactory;

import java.util.ArrayList;

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

public class CIfStatementNode extends CBaseNode
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

	@Override
	public AbstractSyntaxNode copy()
	{
		return CFactory.createIfStatementNode();
	}

	@Override
	public boolean collapse()
	{
		boolean collapsed = false;

		final ArrayList<AbstractSyntaxNode> childrenToRemove = new ArrayList<>();

		for (final AbstractSyntaxNode child : this.getChildren())
		{
			if (child instanceof CCompoundStatementNode
				&& child.getChildren().size() == 1)
			{
				//A compound statement with a single child is useless => removing it allows for prettier printing.
				childrenToRemove.add(child);
				collapsed = true;
			}
		}

		for (final AbstractSyntaxNode childToRemove : childrenToRemove)
		{
			final int removedChildIndex = this.removeChildAndForceParent(childToRemove);
			final AbstractSyntaxNode childToRemoveChild = childToRemove.removeFirstChildAndForceParent();

			if (removedChildIndex >= this.getChildren().size())
			{
				this.addChildAndForceParent(childToRemoveChild);
			}
			else
			{
				this.addChildAtIndexAndForceParent(childToRemoveChild, removedChildIndex);
			}
		}

		return collapsed;
	}
}
