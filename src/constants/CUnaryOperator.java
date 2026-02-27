package constants;

import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;

import java.util.*;

/**
 * Name:        CUnaryOperator.java
 * Content:	    This enum defines the unary operators supported by Eclipse-CDT along with their actual
 * 				(textual) representation, and some utility methods useful for converting the Eclipse-CDT types
 * 				to these operators and conversely.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/02/26
 */

public enum CUnaryOperator
{
	AMPER("&", IASTUnaryExpression.op_amper, true),
	MINUS("-", IASTUnaryExpression.op_minus, true),
	NOT("!", IASTUnaryExpression.op_not, true),
	PLUS("+", IASTUnaryExpression.op_plus, true),
	POSTFIX_DECREMENTATION("--", IASTUnaryExpression.op_postFixDecr, false),
	POSTFIX_INCREMENTATION("++", IASTUnaryExpression.op_postFixIncr, false),
	PREFIX_DECREMENTATION("--", IASTUnaryExpression.op_prefixDecr, true),
	PREFIX_INCREMENTATION("++", IASTUnaryExpression.op_prefixIncr, true),
	SIZEOF("sizeof", IASTUnaryExpression.op_sizeof, true),
	STAR("*", IASTUnaryExpression.op_star, true),
	TILDE("~", IASTUnaryExpression.op_tilde, true)
	;

	private final String operator;
	private final int eclipseCDTIntValue;
	private final boolean isPrefixOperator;

	//Constructors

	CUnaryOperator(final String operator,
				   final int eclipseCDTIntValue,
				   final boolean isPrefixOperator)
	{
		this.operator = operator;
		this.eclipseCDTIntValue = eclipseCDTIntValue;
		this.isPrefixOperator = isPrefixOperator;
	}

	//Public functions

	public String getOperator()
	{
		return this.operator;
	}

	public int getEclipseCDTIntValue()
	{
		return this.eclipseCDTIntValue;
	}

	public boolean isPrefixOperator()
	{
		return this.isPrefixOperator;
	}

	//Static functions

	public static CUnaryOperator convertEclipseCDTUnaryOperatorToThis(final int eclipseCDTUnaryOperatorInt)
	{
		switch (eclipseCDTUnaryOperatorInt)
		{
			case IASTUnaryExpression.op_amper:
			{
				return CUnaryOperator.AMPER;
			}

			case IASTUnaryExpression.op_minus:
			{
				return CUnaryOperator.MINUS;
			}

			case IASTUnaryExpression.op_not:
			{
				return CUnaryOperator.NOT;
			}

			case IASTUnaryExpression.op_plus:
			{
				return CUnaryOperator.PLUS;
			}

			case IASTUnaryExpression.op_postFixDecr:
			{
				return CUnaryOperator.POSTFIX_DECREMENTATION;
			}

			case IASTUnaryExpression.op_postFixIncr:
			{
				return CUnaryOperator.POSTFIX_INCREMENTATION;
			}

			case IASTUnaryExpression.op_prefixDecr:
			{
				return CUnaryOperator.PREFIX_DECREMENTATION;
			}

			case IASTUnaryExpression.op_prefixIncr:
			{
				return CUnaryOperator.PREFIX_INCREMENTATION;
			}

			case IASTUnaryExpression.op_sizeof:
			{
				return CUnaryOperator.SIZEOF;
			}

			case IASTUnaryExpression.op_star:
			{
				return CUnaryOperator.STAR;
			}

			case IASTUnaryExpression.op_tilde:
			{
				return CUnaryOperator.TILDE;
			}

			case IASTUnaryExpression.op_throw:
			case IASTUnaryExpression.op_typeid:
			case IASTUnaryExpression.op_sizeofParameterPack:
			case IASTUnaryExpression.op_noexcept:
			case IASTUnaryExpression.op_integerPack:
			{
				throw new UnsupportedOperationException(
					String.format(
						"The Eclipse-CDT unary operator of value |%d| is reserved to C++ programs!",
						eclipseCDTUnaryOperatorInt
					)
				);
			}

			default:
			{
				throw new UnsupportedOperationException(
					String.format(
						"The Eclipse-CDT unary operator of value |%d| is not handled yet.",
						eclipseCDTUnaryOperatorInt
					)
				);
			}
		}
	}
}
