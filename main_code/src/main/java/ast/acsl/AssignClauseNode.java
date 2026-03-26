package ast.acsl;

import constants.acsl.others.AcslType;

public class AssignClauseNode extends AcslBaseNode
{
	/**
	 * Name:        AssignClauseNode.java
	 * Content:	    This class defines an AssignClauseNode representing an assign clause, and whose children represent
	 * 				the memory locations being assigned.
	 *              For instance the expression "assign p" will be represented as an AssignClauseNode whose child is the
	 *              "p" location (for instance, a pointer to a memory location).
	 * Author:      Quentin Nivon
	 * Email:       quentin.nivon@uol.de
	 * Creation:    17/03/26
	 */

	public AssignClauseNode()
	{
		super(AcslType.ASSIGN_CLAUSE);
	}
}
