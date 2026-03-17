package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslType;

public class BoundaryNode extends AbstractSyntaxNode
{
	//Constructors

	public BoundaryNode(final AcslType type)
	{
		super(type);

		if (type != AcslType.LOWER_BOUND
			&& type != AcslType.UPPER_BOUND)
		{
			throw new RuntimeException();
		}
	}
}
