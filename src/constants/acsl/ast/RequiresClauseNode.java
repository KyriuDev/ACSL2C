package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslClauseKind;
import constants.acsl.others.AcslType;

/**
 * Name:        RequiresClauseNode.java
 * Content:	    This class defines a RequiresClauseNode with a kind that classical nodes do not have.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public class RequiresClauseNode extends AbstractSyntaxNode
{
	private AcslClauseKind clauseKind;

	//Constructors

	public RequiresClauseNode()
	{
		this(null);
	}

	public RequiresClauseNode(final AcslClauseKind clauseKind)
	{
		super(AcslType.REQUIRES_CLAUSE);
		this.clauseKind = clauseKind;
	}

	//Public methods

	public void setClauseKind(final AcslClauseKind clauseKind)
	{
		this.clauseKind = clauseKind;
	}

	public AcslClauseKind getClauseKind()
	{
		return this.clauseKind;
	}

	//Private methods
}
