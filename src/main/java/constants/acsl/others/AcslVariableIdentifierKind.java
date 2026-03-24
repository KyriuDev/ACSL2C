package constants.acsl.others;

/**
 * Name:        AcslVariableIdentifierKind.java
 * Content:	    This enum lists the different kinds that a variable identifier can take, according the ACSL manual.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public enum AcslVariableIdentifierKind
{
	ARRAY("array"),
	BRACKETS("brackets"),
	IDENTIFIER("id"),
	POINTER("pointer")
	;

	private final String name;

	AcslVariableIdentifierKind(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}