package constants.c;

/**
 * Name:        CTypeQualifier.java
 * Content:	    This enum defines the type qualifiers supported by Eclipse-CDT along with their actual (textual)
 * 				representation.
 * 			    and conversely.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public enum CTypeQualifier
{
	CONST("const"),
	INLINE("inline"),
	RESTRICT("restrict"),
	VOLATILE("volatile")
	;

	private final String typeQualifier;

	//Constructors

	CTypeQualifier(final String typeQualifier)
	{
		this.typeQualifier = typeQualifier;
	}

	//Overrides

	@Override
	public String toString()
	{
		return this.typeQualifier;
	}

	//Public methods

	public String getTypeQualifier()
	{
		return this.typeQualifier;
	}
}
