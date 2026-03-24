package constants.acsl.ast;

import constants.acsl.others.AcslVariableIdentifierKind;

/**
 * Name:        PointerNode.java
 * Content:	    This class defines a PointerNode representing a pointer VariableIdentifierNode.
 * 				For instance, the expression "*a" will be represented as a PointerNode with a single child being a
 * 				PredicateOrTermNode representing "a".
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class PointerNode extends VariableIdentifierNode
{
	//Constructors

	public PointerNode(final String content)
	{
		super(AcslVariableIdentifierKind.POINTER, content);
	}
}
