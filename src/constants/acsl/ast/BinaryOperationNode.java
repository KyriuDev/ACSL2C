package constants.acsl.ast;

import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;
import constants.c.CBinaryOperator;

/**
 * Name:        BinaryOperationNode.java
 * Content:	    This class defines a BinaryOperationNode with an operator and two PredicateOrTerm children nodes.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public class BinaryOperationNode extends PredicateOrTermNode
{
	private CBinaryOperator operator;

	//Constructors

	public BinaryOperationNode()
	{
		this(null);
	}

	public BinaryOperationNode(final CBinaryOperator operator)
	{
		super(AcslPredicateOrTermKind.BINARY_OPERATOR);
		this.operator = operator;
	}

	//Overrides

	/**
	 * A binary operation node is considered to be well-formed as long it as a non-null "operator" and exactly two
	 * children being predicates or terms.
	 *
	 * @return true if the current node is well-formed, false otherwise.
	 */
	@Override
	public boolean checkWellFormedness()
	{
		return this.operator != null
				&& this.getChildren().size() == 2
				&& this.getChildren().get(0).getType() == AcslType.PREDICATE_OR_TERM
				&& this.getChildren().get(1).getType() == AcslType.PREDICATE_OR_TERM;
	}

	//Public methods

	public void setOperator(final CBinaryOperator operator)
	{
		this.operator = operator;
	}

	public CBinaryOperator getOperator()
	{
		return this.operator;
	}
}
