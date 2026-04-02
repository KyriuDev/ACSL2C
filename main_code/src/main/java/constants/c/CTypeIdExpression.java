package constants.c;


import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;

/**
 * Name:        CTypeIdExpression.java
 * Content:	    This enum defines the C type id expressions supported by Eclipse-CDT along with their actual (textual)
 * 				representation, and some utility methods useful for converting the Eclipse-CDT types to these and
 * 				conversely.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    02/04/26
 */

public enum CTypeIdExpression
{
	ALIGN_OF("alignof", IASTTypeIdExpression.op_alignof),
	SIZE_OF("sizeof", IASTTypeIdExpression.op_sizeof),
	TYPE_ID("typeid", IASTTypeIdExpression.op_typeid),
	TYPE_OF("typeof", IASTTypeIdExpression.op_typeof)
	;

	private final String expression;
	private final int eclipseCDTIntValue;

	//Constructors

	CTypeIdExpression(final String expression,
					  final int eclipseCDTIntValue)
	{
		this.expression = expression;
		this.eclipseCDTIntValue = eclipseCDTIntValue;
	}

	//Overrides

	@Override
	public String toString()
	{
		return this.expression;
	}

	//Static methods

	public static CTypeIdExpression convertEclipseCDTTypeIdExpressionToThis(final int eclipseCDTTypeIdExpressionInt)
	{
		switch (eclipseCDTTypeIdExpressionInt)
		{
			case IASTTypeIdExpression.op_alignof:
			{
				return CTypeIdExpression.ALIGN_OF;
			}

			case IASTTypeIdExpression.op_sizeof:
			{
				return CTypeIdExpression.SIZE_OF;
			}

			case IASTTypeIdExpression.op_typeid:
			{
				return CTypeIdExpression.TYPE_ID;
			}

			case IASTTypeIdExpression.op_typeof:
			{
				return CTypeIdExpression.TYPE_OF;
			}

			default:
			{
				throw new UnsupportedOperationException(String.format(
					"The Eclipse-CDT type id expression |%d| is not handled yet.",
					eclipseCDTTypeIdExpressionInt
				));
			}
		}
	}

	//Public methods

	public String getExpression()
	{
		return this.expression;
	}

	public int getEclipseCDTIntValue()
	{
		return this.eclipseCDTIntValue;
	}
}
