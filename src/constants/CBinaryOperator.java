package constants;

import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;

/**
 * Name:        CBinaryOperator.java
 * Content:	    This enum defines the binary operators supported by Eclipse-CDT along with their actual
 * 				(textual) representation, and some utility methods useful for converting the Eclipse-CDT types
 * 				to these operators and conversely.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/02/26
 */

public enum CBinaryOperator
{
	ASSIGNMENT("=", IASTBinaryExpression.op_assign),
	BINARY_AND("&", IASTBinaryExpression.op_binaryAnd),
	BINARY_AND_ASSIGNMENT("&=", IASTBinaryExpression.op_binaryAndAssign),
	BINARY_OR("|", IASTBinaryExpression.op_binaryOr),
	BINARY_OR_ASSIGNMENT("|=", IASTBinaryExpression.op_binaryOrAssign),
	BINARY_XOR("^", IASTBinaryExpression.op_binaryXor),
	BINARY_XOR_ASSIGNMENT("^=", IASTBinaryExpression.op_binaryXorAssign),
	DIVIDE("/", IASTBinaryExpression.op_divide),
	DIVIDE_ASSIGNMENT("/=", IASTBinaryExpression.op_divideAssign),
	ELLIPSES("...", IASTBinaryExpression.op_ellipses),
	EQUALS("==", IASTBinaryExpression.op_equals),
	GREATER_OR_EQUAL(">=", IASTBinaryExpression.op_greaterEqual),
	GREATER_THAN(">", IASTBinaryExpression.op_greaterThan),
	LESS_OR_EQUAL("<=", IASTBinaryExpression.op_lessEqual),
	LESS_THAN("<", IASTBinaryExpression.op_lessThan),
	LOGICAL_AND("&&", IASTBinaryExpression.op_logicalAnd),
	LOGICAL_OR("||", IASTBinaryExpression.op_logicalOr),
	MINUS("-", IASTBinaryExpression.op_minus),
	MINUS_ASSIGNMENT("-=", IASTBinaryExpression.op_minusAssign),
	MODULO("%", IASTBinaryExpression.op_modulo),
	MODULO_ASSIGNMENT("%=", IASTBinaryExpression.op_moduloAssign),
	MULTIPLY("*", IASTBinaryExpression.op_multiply),
	MULTIPLY_ASSIGNMENT("*=", IASTBinaryExpression.op_multiplyAssign),
	NOT_EQUALS("!=", IASTBinaryExpression.op_notequals),
	PLUS("+", IASTBinaryExpression.op_plus),
	PLUS_ASSIGNMENT("+=", IASTBinaryExpression.op_plusAssign),
	SHIFT_LEFT("<<", IASTBinaryExpression.op_shiftLeft),
	SHIFT_LEFT_ASSIGNMENT("<<=", IASTBinaryExpression.op_shiftLeftAssign),
	SHIFT_RIGHT(">>", IASTBinaryExpression.op_shiftRight),
	SHIFT_RIGHT_ASSIGNMENT(">>=", IASTBinaryExpression.op_shiftRightAssign)
	;

	private final String operator;
	private final int eclipseCDTIntValue;

	//Constructors

	CBinaryOperator(final String operator,
					final int eclipseCDTIntValue)
	{
		this.operator = operator;
		this.eclipseCDTIntValue = eclipseCDTIntValue;
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

	//Static functions

	public static CBinaryOperator convertEclipseCDTBinaryOperatorToThis(final int eclipseCDTBinaryOperatorInt)
	{
		switch (eclipseCDTBinaryOperatorInt)
		{
			case IASTBinaryExpression.op_assign:
			{
				return CBinaryOperator.ASSIGNMENT;
			}

			case IASTBinaryExpression.op_binaryAnd:
			{
				return CBinaryOperator.BINARY_AND;
			}

			case IASTBinaryExpression.op_binaryAndAssign:
			{
				return CBinaryOperator.BINARY_AND_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_binaryOr:
			{
				return CBinaryOperator.BINARY_OR;
			}

			case IASTBinaryExpression.op_binaryOrAssign:
			{
				return CBinaryOperator.BINARY_OR_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_binaryXor:
			{
				return CBinaryOperator.BINARY_XOR;
			}

			case IASTBinaryExpression.op_binaryXorAssign:
			{
				return CBinaryOperator.BINARY_XOR_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_divide:
			{
				return CBinaryOperator.DIVIDE;
			}

			case IASTBinaryExpression.op_divideAssign:
			{
				return CBinaryOperator.DIVIDE_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_ellipses:
			{
				return CBinaryOperator.ELLIPSES;
			}

			case IASTBinaryExpression.op_equals:
			{
				return CBinaryOperator.EQUALS;
			}

			case IASTBinaryExpression.op_greaterEqual:
			{
				return CBinaryOperator.GREATER_OR_EQUAL;
			}

			case IASTBinaryExpression.op_greaterThan:
			{
				return CBinaryOperator.GREATER_THAN;
			}

			case IASTBinaryExpression.op_lessEqual:
			{
				return CBinaryOperator.LESS_OR_EQUAL;
			}

			case IASTBinaryExpression.op_lessThan:
			{
				return CBinaryOperator.LESS_THAN;
			}

			case IASTBinaryExpression.op_logicalAnd:
			{
				return CBinaryOperator.LOGICAL_AND;
			}

			case IASTBinaryExpression.op_logicalOr:
			{
				return CBinaryOperator.LOGICAL_OR;
			}

			case IASTBinaryExpression.op_minus:
			{
				return CBinaryOperator.MINUS;
			}

			case IASTBinaryExpression.op_minusAssign:
			{
				return CBinaryOperator.MINUS_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_modulo:
			{
				return CBinaryOperator.MODULO;
			}

			case IASTBinaryExpression.op_moduloAssign:
			{
				return CBinaryOperator.MODULO_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_multiply:
			{
				return CBinaryOperator.MULTIPLY;
			}

			case IASTBinaryExpression.op_multiplyAssign:
			{
				return CBinaryOperator.MULTIPLY_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_notequals:
			{
				return CBinaryOperator.NOT_EQUALS;
			}

			case IASTBinaryExpression.op_plus:
			{
				return CBinaryOperator.PLUS;
			}

			case IASTBinaryExpression.op_plusAssign:
			{
				return CBinaryOperator.PLUS_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_shiftLeft:
			{
				return CBinaryOperator.SHIFT_LEFT;
			}

			case IASTBinaryExpression.op_shiftLeftAssign:
			{
				return CBinaryOperator.SHIFT_LEFT_ASSIGNMENT;
			}

			case IASTBinaryExpression.op_shiftRight:
			{
				return CBinaryOperator.SHIFT_RIGHT;
			}

			case IASTBinaryExpression.op_shiftRightAssign:
			{
				return CBinaryOperator.SHIFT_RIGHT_ASSIGNMENT;
			}

			default:
			{
				throw new UnsupportedOperationException(
					String.format(
						"The Eclipse-CDT binary operator of value |%d| is not handled yet.",
						eclipseCDTBinaryOperatorInt
					)
				);
			}
		}
	}
}
