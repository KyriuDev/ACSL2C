package constants.acsl.ast;

import constants.acsl.others.AcslPredicateOrTermKind;

public class RangeNode extends PredicateOrTermNode
{
	//Constructors

	public RangeNode()
	{
		super(AcslPredicateOrTermKind.RANGE);
	}

	public RangeNode(final String content)
	{
		super(AcslPredicateOrTermKind.RANGE, content);
	}
}
