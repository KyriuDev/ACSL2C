package constants;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;

/**
 * Name:        CDeclarationSpecifier.java
 * Content:	    This enum defines the declaration specifiers supported by Eclipse-CDT along with their actual
 * 				(textual) representation, and some utility methods useful for converting the Eclipse-CDT types
 * 				to these specifiers and conversely.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/02/26
 */

public enum CDeclarationSpecifier
{
	AUTO("auto", IASTDeclSpecifier.sc_auto),
	EXTERN("extern", IASTDeclSpecifier.sc_extern),
	REGISTER("register", IASTDeclSpecifier.sc_register),
	STATIC("static", IASTDeclSpecifier.sc_static),
	TYPEDEF("typedef", IASTDeclSpecifier.sc_typedef),
	UNSPECIFIED("", IASTDeclSpecifier.sc_unspecified)
	;

	private final String specifier;
	private final int eclipseCDTIntValue;

	//Constructors

	CDeclarationSpecifier(final String specifier,
					final int eclipseCDTIntValue)
	{
		this.specifier = specifier;
		this.eclipseCDTIntValue = eclipseCDTIntValue;
	}

	//Public methods

	public String getSpecifier()
	{
		return this.specifier;
	}

	public int getEclipseCDTIntValue()
	{
		return this.eclipseCDTIntValue;
	}

	//Static methods

	public static CDeclarationSpecifier convertEclipseCDTTypesToThis(final int eclipseCDTTypeInt)
	{
		switch (eclipseCDTTypeInt)
		{
			case IASTDeclSpecifier.sc_auto:
			{
				return CDeclarationSpecifier.AUTO;
			}

			case IASTDeclSpecifier.sc_extern:
			{
				return CDeclarationSpecifier.EXTERN;
			}

			case IASTDeclSpecifier.sc_register:
			{
				return CDeclarationSpecifier.REGISTER;
			}

			case IASTDeclSpecifier.sc_static:
			{
				return CDeclarationSpecifier.STATIC;
			}

			case IASTDeclSpecifier.sc_typedef:
			{
				return CDeclarationSpecifier.TYPEDEF;
			}

			case IASTDeclSpecifier.sc_unspecified:
			{
				return CDeclarationSpecifier.UNSPECIFIED;
			}

			default:
			{
				throw new UnsupportedOperationException(
					String.format("The declaration specifier |%d| is not yet handled.", eclipseCDTTypeInt)
				);
			}
		}
	}
}
