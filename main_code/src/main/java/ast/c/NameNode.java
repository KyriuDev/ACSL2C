package ast.c;

/**
 * Name:        NameNode.java
 * Content:     This class is the internal representation of the Eclipse-CDT "CASTName" class.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class NameNode extends CBaseNode
{
	private final String value;

	//Constructors

	public NameNode(final String value)
	{
		this.value = value;
	}

	//Overrides

	@Override
	public String getNodeHeader()
	{
		return String.format("- Name \"%s\" has ", this.value);
	}

	//Public methods

	public String getValue()
	{
		return this.value;
	}
}
