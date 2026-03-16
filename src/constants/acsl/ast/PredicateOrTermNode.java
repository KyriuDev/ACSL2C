package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;

/**
 * Name:        PredicateOrTermNode.java
 * Content:	    This class defines a PredicateOrTermNode with a kind that classical nodes do not have.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public class PredicateOrTermNode extends AbstractSyntaxNode
{
	private AcslPredicateOrTermKind kind;

	//Constructors

	public PredicateOrTermNode(final AcslPredicateOrTermKind kind)
	{
		super(AcslType.PREDICATE_OR_TERM);
		this.kind = kind;
	}

	//Public methods

	//Private methods
}
