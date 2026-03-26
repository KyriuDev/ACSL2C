package ast.acsl;

import constants.acsl.others.AcslType;

/**
 * Name:        NameNode.java
 * Content:	    This class defines a NameNode representing the name of a named ACSL object, such as a behavior.
 * 				It must be a child of a SyntacticNamingNode, and have no child.
 * 				Its "name" field represents the string value of that name.
 * 				For instance, the expression "behavior t_is_sorted : [...]" will be represented as a SyntacticNamingNode
 * 				whose children will be a NameNode containing "t_is_sorted", and a PredicateOrTermNode representing the
 * 				behaviour's behaviour.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    17/03/26
 */

public class NameNode extends AcslBaseNode
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
