package constants.acsl.ast;

import constants.acsl.others.AcslPredicateOrTermKind;

public class QuantifierNode extends PredicateOrTermNode
{
	//Constructors

	public QuantifierNode(final AcslPredicateOrTermKind kind)
	{
		this(kind, null);
	}

	public QuantifierNode(final AcslPredicateOrTermKind kind,
						  final String content)
	{
		super(kind, content);

		if (kind != AcslPredicateOrTermKind.FOR_ALL
			&& kind != AcslPredicateOrTermKind.EXISTS)
		{
			throw new RuntimeException(String.format(
				"Quantifier node should be of type \"%s\" or \"%s\", got \"%s\"",
				AcslPredicateOrTermKind.EXISTS.getName(),
				AcslPredicateOrTermKind.FOR_ALL.getName(),
				kind.getName()
			));
		}
	}
}
