package ast.c;

import constants.c.CBinaryOperator;
import constants.c.CStorageClass;
import constants.c.CType;
import constants.c.CUnaryOperator;

/**
 * Name:        CFactory.java
 * Content:	    This class contains utility methods to create CBaseNodes without the burden of correctly
 * 				using the CBaseNode constructors manually, by wrapping them into nicer functions.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/03/26
 */

public class CFactory
{
	//Constructors

	private CFactory()
	{

	}

	//Public methods

	public static ArrayDeclaratorNode createArrayDeclaratorNode()
	{
		return new ArrayDeclaratorNode();
	}

	public static ArrayModifierNode createArrayModifierNode()
	{
		return new ArrayModifierNode();
	}

	public static ArraySubscriptExpressionNode createArraySubscriptExpressionNode()
	{
		return new ArraySubscriptExpressionNode();
	}

	public static BinaryExpressionNode createBinaryExpressionNode(final int binaryOperator)
	{
		return new BinaryExpressionNode(CBinaryOperator.convertEclipseCDTBinaryOperatorToThis(binaryOperator));
	}

	public static CompoundStatementNode createCompoundStatementNode()
	{
		return new CompoundStatementNode();
	}

	public static DeclarationStatementNode createDeclarationStatementNode()
	{
		return new DeclarationStatementNode();
	}

	public static DeclaratorNode createDeclaratorNode()
	{
		return new DeclaratorNode();
	}

	public static EqualsInitializerNode createEqualsInitializerNode()
	{
		return new EqualsInitializerNode();
	}

	public static ExpressionStatementNode createExpressionStatementNode()
	{
		return new ExpressionStatementNode();
	}

	public static ForStatementNode createForStatementNode()
	{
		return new ForStatementNode();
	}

	public static FunctionCallExpressionNode createFunctionCallExpressionNode()
	{
		return new FunctionCallExpressionNode();
	}

	public static FunctionDeclaratorNode createFunctionDeclaratorNode()
	{
		return new FunctionDeclaratorNode();
	}

	public static FunctionDefinitionNode createFunctionDefinitionNode()
	{
		return new FunctionDefinitionNode();
	}

	public static IdExpressionNode createIdExpressionNode()
	{
		return new IdExpressionNode();
	}

	public static IfStatementNode createIfStatementNode()
	{
		return new IfStatementNode();
	}

	public static LiteralExpressionNode createLiteralExpressionNode(final String value)
	{
		return new LiteralExpressionNode(value);
	}

	public static NameNode createNameNode(final String value)
	{
		return new NameNode(value);
	}

	public static NullStatementNode createNullStatementNode()
	{
		return new NullStatementNode();
	}

	public static ParameterDeclarationNode createParameterDeclarationNode()
	{
		return new ParameterDeclarationNode();
	}

	public static PointerNode createPointerNode()
	{
		return new PointerNode();
	}

	public static ReturnStatementNode createReturnStatementNode()
	{
		return new ReturnStatementNode();
	}

	public static SimpleDeclarationNode createSimpleDeclarationNode()
	{
		return new SimpleDeclarationNode();
	}

	public static SimpleDeclSpecifierNode createSimpleDeclarationSpecifierNode(final int storageClass,
																			   final int type,
																			   final boolean isConst,
																			   final boolean isInline,
																			   final boolean isRestrict,
																			   final boolean isVolatile)
	{
		return new SimpleDeclSpecifierNode(
			CStorageClass.convertEclipseCDTTypesToThis(storageClass),
			CType.convertEclipseCDTTypesToThis(type),
			isConst,
			isInline,
			isRestrict,
			isVolatile
		);
	}

	public static TranslationUnitNode createTranslationUnitNode()
	{
		return new TranslationUnitNode();
	}

	public static TypedefNameSpecifierNode createTypedefNameSpecifierNode(final int storageClass,
																		  final boolean isConst,
																		  final boolean isInline,
																		  final boolean isRestrict,
																		  final boolean isVolatile,
																		  final String typeName)
	{
		return new TypedefNameSpecifierNode(
			CStorageClass.convertEclipseCDTTypesToThis(storageClass),
			isConst,
			isInline,
			isRestrict,
			isVolatile,
			typeName
		);
	}

	public static UnaryExpressionNode createUnaryExpressionNode(final int operator)
	{
		return new UnaryExpressionNode(CUnaryOperator.convertEclipseCDTUnaryOperatorToThis(operator));
	}
}
