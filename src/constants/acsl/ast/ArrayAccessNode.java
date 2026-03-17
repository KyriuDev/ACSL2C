package constants.acsl.ast;

import constants.acsl.others.AcslPredicateOrTermKind;
import constants.acsl.others.AcslType;

public class ArrayAccessNode extends PredicateOrTermNode
{
	//Constructors

	public ArrayAccessNode()
	{
		super(AcslPredicateOrTermKind.ARRAY_ACCESS);
	}

	public ArrayAccessNode(final String content)
	{
		super(AcslPredicateOrTermKind.ARRAY_ACCESS, content);
	}

	//Overrides

	@Override
	public String checkWellFormedness()
	{
		if (this.getChildren().isEmpty()
			|| this.getChildren().get(0).getType() != AcslType.INDEX)
		{
			return String.format(
				"Array access node is malformed:" +
				"\n\t- Expected 2 children, got %d;" +
				"\n\t- Expected first child to be an index, got a \"%s\".",
				this.getChildren().size(),
				!this.getChildren().isEmpty() ? this.getChildren().get(0).getType().getReadableName() : null
			);
		}

		return null;
	}
}
