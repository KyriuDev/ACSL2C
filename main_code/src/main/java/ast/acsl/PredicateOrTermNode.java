package ast.acsl;

import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;

/**
 * Name:        PredicateOrTermNode.java
 * Content:	    This class defines a PredicateOrTermNode with a kind that classical nodes do not have.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public class PredicateOrTermNode extends AcslBaseNode
{
	private final AcslPredicateOrTermKind kind;
	protected String content;

	//Constructors

	public PredicateOrTermNode(final AcslPredicateOrTermKind kind)
	{
		this(kind, null);
	}

	public PredicateOrTermNode(final AcslPredicateOrTermKind kind,
							   final String content)
	{
		super(AcslType.PREDICATE_OR_TERM);
		this.kind = kind;
		this.content = content;
	}

	//Public methods

	public String getContent()
	{
		throw new UnsupportedOperationException(
			"Default predicates or terms should not have a content. This method should be overridden by nodes " +
			"extending this node to handle contents."
		);
	}

	public AcslPredicateOrTermKind getKind()
	{
		return this.kind;
	}

	//Private methods
}
