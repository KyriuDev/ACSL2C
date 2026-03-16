package constants.acsl.others;

/**
 * Name:        AcslPredicateOrTermKind.java
 * Content:	    This enum lists the different kinds that a predicate or a term can take, according the ACSL manual.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public enum AcslPredicateOrTermKind
{
	ARRAY_ACCESS("array-access"),
	BINARY_OPERATOR("binary"),
	IDENTIFIER("id"),
	LITERAL("literal"),
	UNARY_OPERATOR("unary")
	;

	private final String name;

	AcslPredicateOrTermKind(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}

	public static AcslPredicateOrTermKind getKindFromName(final String name)
	{
		if (name.equals(AcslPredicateOrTermKind.ARRAY_ACCESS.getName()))
		{
			return AcslPredicateOrTermKind.ARRAY_ACCESS;
		}
		else if (name.equals(AcslPredicateOrTermKind.BINARY_OPERATOR.getName()))
		{
			return AcslPredicateOrTermKind.BINARY_OPERATOR;
		}
		else if (name.equals(AcslPredicateOrTermKind.IDENTIFIER.getName()))
		{
			return AcslPredicateOrTermKind.IDENTIFIER;
		}
		else if (name.equals(AcslPredicateOrTermKind.LITERAL.getName()))
		{
			return AcslPredicateOrTermKind.LITERAL;
		}
		else if (name.equals(AcslPredicateOrTermKind.UNARY_OPERATOR.getName()))
		{
			return AcslPredicateOrTermKind.UNARY_OPERATOR;
		}
		else
		{
			throw new RuntimeException(String.format(
				"The kind \"%s\" does not correspond to any predicate or term kind.",
				name
			));
		}
	}
}
