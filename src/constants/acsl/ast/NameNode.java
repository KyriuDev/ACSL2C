package constants.acsl.ast;

import ast.AbstractSyntaxNode;
import constants.acsl.others.AcslType;

public class NameNode extends AbstractSyntaxNode
{
	private String name;

	//Constructors

	public NameNode()
	{
		this(null);
	}

	public NameNode(final String name)
	{
		super(AcslType.NAME);
		this.name = name;
	}

	//Public methods

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
