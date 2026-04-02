package ast.acsl.nodes;

import constants.acsl.others.AcslVariableIdentifierKind;

/**
 * Name:        BracketsNode.java
 * Content:	    This class defines an BracketsNode representing a bracketed variable identifier, and whose child is the
 * 				variable identifier put between these brackets.
 * 				For instance, expression "(*a)" will be represented as a BracketsNode whose child will represent the
 * 				"*a" part of the expression.
 * 				//TODO Is is really useful? I don't think so...
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class BracketsNode extends VariableIdentifierNode
{
	public BracketsNode(final String content)
	{
		super(AcslVariableIdentifierKind.BRACKETS, content);
	}
}
