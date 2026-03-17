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
	ARRAY_ACCESS("array_access"),
	BINARY_OPERATOR("binary"),
	EXISTS("exists"),
	FOR_ALL("forall"),
	IDENTIFIER("id"),
	LITERAL("literal"),
	RANGE("range"),
	RESULT("result"),
	SYNTACTIC_NAMING("syntactic_naming"),
	UNARY_OPERATOR("unary"),
	VALID("valid")
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
		else if (name.equals(AcslPredicateOrTermKind.EXISTS.getName()))
		{
			return AcslPredicateOrTermKind.EXISTS;
		}
		else if (name.equals(AcslPredicateOrTermKind.FOR_ALL.getName()))
		{
			return AcslPredicateOrTermKind.FOR_ALL;
		}
		else if (name.equals(AcslPredicateOrTermKind.IDENTIFIER.getName()))
		{
			return AcslPredicateOrTermKind.IDENTIFIER;
		}
		else if (name.equals(AcslPredicateOrTermKind.LITERAL.getName()))
		{
			return AcslPredicateOrTermKind.LITERAL;
		}
		else if (name.equals(AcslPredicateOrTermKind.RANGE.getName()))
		{
			return AcslPredicateOrTermKind.RANGE;
		}
		else if (name.equals(AcslPredicateOrTermKind.RESULT.getName()))
		{
			return AcslPredicateOrTermKind.RESULT;
		}
		else if (name.equals(AcslPredicateOrTermKind.SYNTACTIC_NAMING.getName()))
		{
			return AcslPredicateOrTermKind.SYNTACTIC_NAMING;
		}
		else if (name.equals(AcslPredicateOrTermKind.UNARY_OPERATOR.getName()))
		{
			return AcslPredicateOrTermKind.UNARY_OPERATOR;
		}
		else if (name.equals(AcslPredicateOrTermKind.VALID.getName()))
		{
			return AcslPredicateOrTermKind.VALID;
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
