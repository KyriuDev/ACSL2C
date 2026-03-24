package constants.acsl.others;

/**
 * Name:        AcslType.java
 * Content:	    This enum lists the type of ACSL elements that are used in the context of this approach.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public enum AcslType
{
	ASSIGN_CLAUSE("assign-clause", "Assign clause"),
	ASSUMES_CLAUSE_LIST("assumes-clause-list", "List of assumes clauses"),
	BEHAVIOR("behavior", "Behavior"),
	BINDER("binder", "Binder"),
	BINDERS("binders", "List of binders"),
	ENSURES_CLAUSE("ensures-clause", "Ensures clause"),
	FUNCTION_CONTRACT("function-contract", "Function contract"),
	INDEX("index", "Index"),
	LOCATION("location", "Location"),
	LOCATIONS("locations", "Locations"),
	LOWER_BOUND("lower-bound", "Lower bound"),
	MEMORY_ALLOCATION_SET("memory-allocation-set", "Memory allocation set"),
	NAME("name", "Name"),
	NAMED_BEHAVIOR_LIST("named-behavior-list", "List of named behaviors"),
	OPERATOR("op", "Operator"),
	PREDICATE_OR_TERM("predicate-or-term", "Predicate or term"),
	REQUIRES_CLAUSE("requires", "Requires clause"),
	REQUIRES_CLAUSE_LIST("requires-clause-list", "List of requires clauses"),
	ROOT("acslfile", "ACSL file"),
	SIMPLE_CLAUSE_LIST("simple-clause-list", "List of simple clauses"),
	TYPE_SPECIFIER("type-specifier", "Type specifier"),
	TYPES("types", "List of types"),
	UPPER_BOUND("upper-bound", "Upper bound"),
	VARIABLE_IDENTIFIER("var-id", "Variable identifier")
	;

	private final String xmlTag;
	private final String readableName;

	AcslType(final String xmlTag,
			 final String readableName)
	{
		this.xmlTag = xmlTag;
		this.readableName = readableName;
	}

	public String getXmlTag()
	{
		return this.xmlTag;
	}

	public String getReadableName()
	{
		return this.readableName;
	}
}
