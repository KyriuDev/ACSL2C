package constants.acsl.others;

/**
 * Name:        AcslClauseKind.java
 * Content:	    This enum lists the different kinds that a contract clause can take, according the ACSL manual.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public enum AcslClauseKind
{
	ADMIT("admit"),
	CHECK("check"),
	NONE("none")
	;

	private final String name;

	AcslClauseKind(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public static AcslClauseKind getKindFromName(final String name)
	{
		if (name.equals(AcslClauseKind.ADMIT.getName()))
		{
			return AcslClauseKind.ADMIT;
		}
		else if (name.equals(AcslClauseKind.CHECK.getName()))
		{
			return AcslClauseKind.CHECK;
		}
		else if (name.equals(AcslClauseKind.NONE.getName())
				|| name.isEmpty())
		{
			return AcslClauseKind.NONE;
		}
		else
		{
			throw new RuntimeException(String.format("The kind \"%s\" does not correspond to any clause kind.", name));
		}
	}
}
