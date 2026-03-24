package constants.acsl.ast;

import constants.acsl.others.AcslVariableIdentifierKind;

public class ArrayNode extends VariableIdentifierNode
{
	/**
	 * Name:        ArrayNode.java
	 * Content:	    This class defines an ArrayNode representing an array definition, and whose children represent the
	 * 				array being defined.
	 * 				For instance the expression "(*a)[]" will be represented as an ArrayNode whose child is the "*a"
	 * 				predicate.
	 * Author:      Quentin Nivon
	 * Email:       quentin.nivon@uol.de
	 * Creation:    17/03/26
	 */

	public ArrayNode(final String content)
	{
		super(AcslVariableIdentifierKind.ARRAY, content);
	}
}
