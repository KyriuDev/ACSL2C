package constants.c;

import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;

/**
 * Name:        CStorageClass.java
 * Content:	    This enum defines the storage classes supported by Eclipse-CDT along with their actual
 * 				(textual) representation, and some utility methods useful for converting the Eclipse-CDT types
 * 				to these specifiers and conversely.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/02/26
 */

public enum CStorageClass
{
	AUTO("auto", IASTDeclSpecifier.sc_auto),
	EXTERN("extern", IASTDeclSpecifier.sc_extern),
	REGISTER("register", IASTDeclSpecifier.sc_register),
	STATIC("static", IASTDeclSpecifier.sc_static),
	TYPEDEF("typedef", IASTDeclSpecifier.sc_typedef),
	UNSPECIFIED("", IASTDeclSpecifier.sc_unspecified)
	;

	private final String storageClass;
	private final int eclipseCDTIntValue;

	//Constructors

	CStorageClass(final String storageClass,
	              final int eclipseCDTIntValue)
	{
		this.storageClass = storageClass;
		this.eclipseCDTIntValue = eclipseCDTIntValue;
	}

	//Overrides
	@Override
	public String toString()
	{
		return this.storageClass;
	}

	//Public methods

	public String getStorageClass()
	{
		return this.storageClass;
	}

	public int getEclipseCDTIntValue()
	{
		return this.eclipseCDTIntValue;
	}

	//Static methods

	public static CStorageClass convertEclipseCDTTypesToThis(final int eclipseCDTTypeInt)
	{
		switch (eclipseCDTTypeInt)
		{
			case IASTDeclSpecifier.sc_auto:
			{
				return CStorageClass.AUTO;
			}

			case IASTDeclSpecifier.sc_extern:
			{
				return CStorageClass.EXTERN;
			}

			case IASTDeclSpecifier.sc_register:
			{
				return CStorageClass.REGISTER;
			}

			case IASTDeclSpecifier.sc_static:
			{
				return CStorageClass.STATIC;
			}

			case IASTDeclSpecifier.sc_typedef:
			{
				return CStorageClass.TYPEDEF;
			}

			case IASTDeclSpecifier.sc_unspecified:
			{
				return CStorageClass.UNSPECIFIED;
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
