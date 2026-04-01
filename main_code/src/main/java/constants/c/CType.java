package constants.c;

import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;

/**
 * Name:        CType.java
 * Content:	    This enum defines the simple C types supported by Eclipse-CDT along with their actual (textual)
 * 				representation, and some utility methods useful for converting the Eclipse-CDT types to these and
 * 				conversely.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/02/26
 */

public enum CType
{
	AUTO("auto", IASTSimpleDeclSpecifier.t_auto),
	BOOL("_Bool", IASTSimpleDeclSpecifier.t_bool),
	CHAR("char", IASTSimpleDeclSpecifier.t_char),
	CHAR_8_T("char8_t", IASTSimpleDeclSpecifier.t_char8_t),
	CHAR_16_T("char16_t", IASTSimpleDeclSpecifier.t_char16_t),
	CHAR_32_T("char32_t", IASTSimpleDeclSpecifier.t_char32_t),
	DECIMAL_32("_Decimal32", IASTSimpleDeclSpecifier.t_decimal32),
	DECIMAL_64("_Decimal64", IASTSimpleDeclSpecifier.t_decimal64),
	DECIMAL_128("_Decimal128", IASTSimpleDeclSpecifier.t_decimal128),
	DECL_TYPE("decltype", IASTSimpleDeclSpecifier.t_decltype),
	DECL_TYPE_AUTO("decltype(auto)", IASTSimpleDeclSpecifier.t_decltype_auto),
	DOUBLE("double", IASTSimpleDeclSpecifier.t_double),
	FLOAT("float", IASTSimpleDeclSpecifier.t_float),
	FLOAT_128("__float128", IASTSimpleDeclSpecifier.t_float128),
	INT("int", IASTSimpleDeclSpecifier.t_int),
	INT_128("__int128", IASTSimpleDeclSpecifier.t_int128),
	LONG("long", -1),
	SHORT("short", -1),
	TYPE_OF("typeof", IASTSimpleDeclSpecifier.t_typeof),
	UNSPECIFIED("", IASTSimpleDeclSpecifier.t_unspecified),
	USER_DEFINED("", -1),
	VOID("void", IASTSimpleDeclSpecifier.t_void),
	W_CHAR_T("w_char_t", IASTSimpleDeclSpecifier.t_wchar_t),
	;

	private String type;
	private final int eclipseCDTIntValue;

	//Constructors

	CType(final String type,
		  final int eclipseCDTIntValue)
	{
		this.type = type;
		this.eclipseCDTIntValue = eclipseCDTIntValue;
	}

	//Overrides

	@Override
	public String toString()
	{
		return this.type;
	}

	//Public functions

	public String getType()
	{
		return this.type;
	}

	public int getEclipseCDTIntValue()
	{
		return this.eclipseCDTIntValue;
	}

	public CType setType(final String type)
	{
		this.type = type;
		return this;
	}

	//Static functions

	public static CType convertEclipseCDTTypesToThis(final int eclipseCDTTypeInt)
	{
		switch (eclipseCDTTypeInt)
		{
			case IASTSimpleDeclSpecifier.t_auto:
			{
				return CType.AUTO;
			}

			case IASTSimpleDeclSpecifier.t_bool:
			{
				return CType.BOOL;
			}

			case IASTSimpleDeclSpecifier.t_char:
			{
				return CType.CHAR;
			}

			case IASTSimpleDeclSpecifier.t_char8_t:
			{
				return CType.CHAR_8_T;
			}

			case IASTSimpleDeclSpecifier.t_char16_t:
			{
				return CType.CHAR_16_T;
			}

			case IASTSimpleDeclSpecifier.t_char32_t:
			{
				return CType.CHAR_32_T;
			}

			case IASTSimpleDeclSpecifier.t_decimal32:
			{
				return CType.DECIMAL_32;
			}

			case IASTSimpleDeclSpecifier.t_decimal64:
			{
				return CType.DECIMAL_64;
			}

			case IASTSimpleDeclSpecifier.t_decimal128:
			{
				return CType.DECIMAL_128;
			}

			case IASTSimpleDeclSpecifier.t_decltype:
			{
				return CType.DECL_TYPE;
			}

			case IASTSimpleDeclSpecifier.t_decltype_auto:
			{
				return CType.DECL_TYPE_AUTO;
			}

			case IASTSimpleDeclSpecifier.t_double:
			{
				return CType.DOUBLE;
			}

			case IASTSimpleDeclSpecifier.t_float:
			{
				return CType.FLOAT;
			}

			case IASTSimpleDeclSpecifier.t_float128:
			{
				return CType.FLOAT_128;
			}

			case IASTSimpleDeclSpecifier.t_int:
			{
				return CType.INT;
			}

			case IASTSimpleDeclSpecifier.t_int128:
			{
				return CType.INT_128;
			}

			case IASTSimpleDeclSpecifier.t_typeof:
			{
				return CType.TYPE_OF;
			}

			case IASTSimpleDeclSpecifier.t_unspecified:
			{
				return CType.UNSPECIFIED;
			}

			case IASTSimpleDeclSpecifier.t_void:
			{
				return CType.VOID;
			}

			case IASTSimpleDeclSpecifier.t_wchar_t:
			{
				return CType.W_CHAR_T;
			}

			default:
			{
				throw new UnsupportedOperationException(
					String.format("The Eclipse-CDT type |%d| is not handled yet.", eclipseCDTTypeInt)
				);
			}
		}
	}
}
