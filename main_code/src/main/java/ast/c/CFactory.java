package ast.c;

import constants.c.*;

import java.awt.*;

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

	public static CastExpressionNode createCastExpressionNode()
	{
		return new CastExpressionNode();
	}

	public static CompositeTypeSpecifierNode createCompositeTypeSpecifierNode(final int storageClass)
	{
		return CFactory.createCompositeTypeSpecifierNode(CStorageClass.convertEclipseCDTTypesToThis(storageClass));
	}

	public static CompositeTypeSpecifierNode createCompositeTypeSpecifierNode(final CStorageClass storageClass)
	{
		return new CompositeTypeSpecifierNode(storageClass);
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

	public static ElaboratedTypeSpecifierNode createElaboratedTypeSpecifierNode(final int specifierKind)
	{
		return new ElaboratedTypeSpecifierNode(CElaboratedTypeSpecifier.convertEclipseCDTElaboratedTypeSpecifierToThis(specifierKind));
	}

	public static EqualsInitializerNode createEqualsInitializerNode()
	{
		return new EqualsInitializerNode();
	}

	public static ExpressionStatementNode createExpressionStatementNode()
	{
		return new ExpressionStatementNode();
	}

	public static FieldReferenceNode createFieldReferenceNode()
	{
		return new FieldReferenceNode();
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

	public static GotoStatementNode createGotoStatementNode()
	{
		return new GotoStatementNode();
	}

	public static IdExpressionNode createIdExpressionNode()
	{
		return new IdExpressionNode();
	}

	public static IfStatementNode createIfStatementNode()
	{
		return new IfStatementNode();
	}

	public static LabelStatementNode createLabelStatementNode()
	{
		return new LabelStatementNode();
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
		return CFactory.createSimpleDeclarationSpecifierNode(
			CStorageClass.convertEclipseCDTTypesToThis(storageClass),
			CType.convertEclipseCDTTypesToThis(type),
			isConst,
			isInline,
			isRestrict,
			isVolatile
		);
	}

	public static SimpleDeclSpecifierNode createSimpleDeclarationSpecifierNode(final CStorageClass storageClass,
	                                                                           final CType type,
	                                                                           final boolean isConst,
	                                                                           final boolean isInline,
	                                                                           final boolean isRestrict,
	                                                                           final boolean isVolatile)
	{
		return new SimpleDeclSpecifierNode(
			storageClass,
			type,
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

	public static TypeIdNode createTypeIdNode()
	{
		return new TypeIdNode();
	}

	public static TypeIdExpressionNode createTypeIdExpressionNode()
	{
		return new TypeIdExpressionNode();
	}

	public static TypedefNameSpecifierNode createTypedefNameSpecifierNode(final int storageClass,
																		  final boolean isConst,
																		  final boolean isInline,
																		  final boolean isRestrict,
																		  final boolean isVolatile,
																		  final String typeName)
	{
		return CFactory.createTypedefNameSpecifierNode(
			CStorageClass.convertEclipseCDTTypesToThis(storageClass),
			isConst,
			isInline,
			isRestrict,
			isVolatile,
			typeName
		);
	}

	public static TypedefNameSpecifierNode createTypedefNameSpecifierNode(final CStorageClass storageClass,
	                                                                      final boolean isConst,
	                                                                      final boolean isInline,
	                                                                      final boolean isRestrict,
	                                                                      final boolean isVolatile,
	                                                                      final String typeName)
	{
		return new TypedefNameSpecifierNode(
			storageClass,
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

	public static WhileStatementNode createWhileStatementNode()
	{
		return new WhileStatementNode();
	}
}
