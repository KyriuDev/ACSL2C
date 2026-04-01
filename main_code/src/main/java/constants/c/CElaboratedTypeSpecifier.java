package constants.c;

import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;

/**
 * Name:        CElaboratedTypeSpecifier.java
 * Content:	    This enum defines the elaborated type specifiers supported by Eclipse-CDT along with their actual
 * 				(textual) representation.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    31/03/26
 */

public enum CElaboratedTypeSpecifier
{
	ENUM("enum", IASTElaboratedTypeSpecifier.k_enum),
	STRUCT("struct", IASTElaboratedTypeSpecifier.k_struct),
	UNION("union", IASTElaboratedTypeSpecifier.k_union)
	;

	private final int eclipseCDTIntValue;
	private final String typeSpecifier;

	//Constructors

	CElaboratedTypeSpecifier(final String name,
							 final int eclipseCDTIntValue)
	{
		this.typeSpecifier = name;
		this.eclipseCDTIntValue = eclipseCDTIntValue;
	}

	//Overrides

	@Override
	public String toString()
	{
		return this.typeSpecifier;
	}

	//Static functions

	public static CElaboratedTypeSpecifier convertEclipseCDTElaboratedTypeSpecifierToThis(final int eclipseCDTElaboratedTypeSpecifierInt)
	{
		switch (eclipseCDTElaboratedTypeSpecifierInt)
		{
			case IASTElaboratedTypeSpecifier.k_enum:
			{
				return CElaboratedTypeSpecifier.ENUM;
			}

			case IASTElaboratedTypeSpecifier.k_struct:
			{
				return CElaboratedTypeSpecifier.STRUCT;
			}

			case IASTElaboratedTypeSpecifier.k_union:
			{
				return CElaboratedTypeSpecifier.UNION;
			}

			default:
			{
				throw new UnsupportedOperationException(
					String.format(
						"The Eclipse-CDT elaborated type specifier of value |%d| is not handled yet.",
						eclipseCDTElaboratedTypeSpecifierInt
					)
				);
			}
		}
	}

	//Public methods

	public String getTypeSpecifier()
	{
		return this.typeSpecifier;
	}

	public int getEclipseCDTIntValue()
	{
		return this.eclipseCDTIntValue;
	}
}
