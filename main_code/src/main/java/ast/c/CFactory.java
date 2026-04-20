package ast.c;

import ast.c.nodes.*;
import constants.c.*;

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

	public static CArrayDeclaratorNode createArrayDeclaratorNode()
	{
		return new CArrayDeclaratorNode();
	}

	public static CArrayModifierNode createArrayModifierNode()
	{
		return new CArrayModifierNode();
	}

	public static CArraySubscriptExpressionNode createArraySubscriptExpressionNode()
	{
		return new CArraySubscriptExpressionNode();
	}

	public static CBinaryExpressionNode createBinaryExpressionNode(final int binaryOperator)
	{
		return CFactory.createBinaryExpressionNode(CBinaryOperator.convertEclipseCDTBinaryOperatorToThis(binaryOperator));
	}

	public static CBinaryExpressionNode createBinaryExpressionNode(final CBinaryOperator binaryOperator)
	{
		return new CBinaryExpressionNode(binaryOperator);
	}

	public static CBreakStatementNode createBreakStatementNode()
	{
		return new CBreakStatementNode();
	}

	public static CCastExpressionNode createCastExpressionNode()
	{
		return new CCastExpressionNode();
	}

	public static CCompositeTypeSpecifierNode createCompositeTypeSpecifierNode(final int storageClass)
	{
		return CFactory.createCompositeTypeSpecifierNode(CStorageClass.convertEclipseCDTTypesToThis(storageClass));
	}

	public static CCompositeTypeSpecifierNode createCompositeTypeSpecifierNode(final CStorageClass storageClass)
	{
		return new CCompositeTypeSpecifierNode(storageClass);
	}

	public static CCompoundStatementNode createCompoundStatementNode()
	{
		return new CCompoundStatementNode();
	}

	public static CDeclarationStatementNode createDeclarationStatementNode()
	{
		return new CDeclarationStatementNode();
	}

	public static CDeclaratorNode createDeclaratorNode()
	{
		return new CDeclaratorNode();
	}

	public static CElaboratedTypeSpecifierNode createElaboratedTypeSpecifierNode(final int specifierKind)
	{
		return CFactory.createElaboratedTypeSpecifierNode(CElaboratedTypeSpecifier.convertEclipseCDTElaboratedTypeSpecifierToThis(specifierKind));
	}

		public static CElaboratedTypeSpecifierNode createElaboratedTypeSpecifierNode(final CElaboratedTypeSpecifier elaboratedTypeSpecifier)
	{
		return new CElaboratedTypeSpecifierNode(elaboratedTypeSpecifier);
	}

	public static CEqualsInitializerNode createEqualsInitializerNode()
	{
		return new CEqualsInitializerNode();
	}

	public static CExpressionStatementNode createExpressionStatementNode()
	{
		return new CExpressionStatementNode();
	}

	public static CFieldReferenceNode createFieldReferenceNode()
	{
		return new CFieldReferenceNode();
	}

	public static CForStatementNode createForStatementNode()
	{
		return new CForStatementNode();
	}

	public static CFunctionCallExpressionNode createFunctionCallExpressionNode()
	{
		return new CFunctionCallExpressionNode();
	}

	public static CFunctionDeclaratorNode createFunctionDeclaratorNode()
	{
		return new CFunctionDeclaratorNode();
	}

	public static CFunctionDefinitionNode createFunctionDefinitionNode()
	{
		return new CFunctionDefinitionNode();
	}

	public static CGotoStatementNode createGotoStatementNode()
	{
		return new CGotoStatementNode();
	}

	public static CIdExpressionNode createIdExpressionNode()
	{
		return new CIdExpressionNode();
	}

	public static CIfStatementNode createIfStatementNode()
	{
		return new CIfStatementNode();
	}

	public static CInitializerListNode createInitializerListNode()
	{
		return new CInitializerListNode();
	}

	public static CLabelStatementNode createLabelStatementNode()
	{
		return new CLabelStatementNode();
	}

	public static CLiteralExpressionNode createLiteralExpressionNode(final String value)
	{
		return new CLiteralExpressionNode(value);
	}

	public static CNameNode createNameNode(final String value)
	{
		return new CNameNode(value);
	}

	public static CNullStatementNode createNullStatementNode()
	{
		return new CNullStatementNode();
	}

	public static CParameterDeclarationNode createParameterDeclarationNode()
	{
		return new CParameterDeclarationNode();
	}

	public static CPointerNode createPointerNode()
	{
		return new CPointerNode();
	}

	public static CReturnStatementNode createReturnStatementNode()
	{
		return new CReturnStatementNode();
	}

	public static CSimpleDeclarationNode createSimpleDeclarationNode()
	{
		return new CSimpleDeclarationNode();
	}

	public static CSimpleDeclSpecifierNode createSimpleDeclarationSpecifierNode(final int storageClass,
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

	public static CSimpleDeclSpecifierNode createSimpleDeclarationSpecifierNode(final CStorageClass storageClass,
	                                                                            final CType type)
	{
		return CFactory.createSimpleDeclarationSpecifierNode(
			storageClass,
			type,
			false,
			false,
			false,
			false
		);
	}

	public static CSimpleDeclSpecifierNode createSimpleDeclarationSpecifierNode(final CType type)
	{
		return CFactory.createSimpleDeclarationSpecifierNode(
			CStorageClass.UNSPECIFIED,
			type
		);
	}

	public static CSimpleDeclSpecifierNode createSimpleDeclarationSpecifierNode(final CStorageClass storageClass,
	                                                                            final CType type,
	                                                                            final boolean isConst,
	                                                                            final boolean isInline,
	                                                                            final boolean isRestrict,
	                                                                            final boolean isVolatile)
	{
		return new CSimpleDeclSpecifierNode(
			storageClass,
			type,
			isConst,
			isInline,
			isRestrict,
			isVolatile
		);
	}

	public static CTranslationUnitNode createTranslationUnitNode()
	{
		return new CTranslationUnitNode();
	}

	public static CTypeIdNode createTypeIdNode()
	{
		return new CTypeIdNode();
	}

	public static CTypeIdExpressionNode createTypeIdExpressionNode(final int typeIdExpression)
	{
		return CFactory.createTypeIdExpressionNode(CTypeIdExpression.convertEclipseCDTTypeIdExpressionToThis(typeIdExpression));
	}

	public static CTypeIdExpressionNode createTypeIdExpressionNode(final CTypeIdExpression typeIdExpression)
	{
		return new CTypeIdExpressionNode(typeIdExpression);
	}

	public static CTypedefNameSpecifierNode createTypedefNameSpecifierNode(final int storageClass,
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

	public static CTypedefNameSpecifierNode createTypedefNameSpecifierNode(final CStorageClass storageClass,
	                                                                       final boolean isConst,
	                                                                       final boolean isInline,
	                                                                       final boolean isRestrict,
	                                                                       final boolean isVolatile,
	                                                                       final String typeName)
	{
		return new CTypedefNameSpecifierNode(
			storageClass,
			isConst,
			isInline,
			isRestrict,
			isVolatile,
			typeName
		);
	}

	public static CUnaryExpressionNode createUnaryExpressionNode(final int operator)
	{
		return CFactory.createUnaryExpressionNode(CUnaryOperator.convertEclipseCDTUnaryOperatorToThis(operator));
	}

	public static CUnaryExpressionNode createUnaryExpressionNode(final CUnaryOperator operator)
	{
		return new CUnaryExpressionNode(operator);
	}

	public static CWhileStatementNode createWhileStatementNode()
	{
		return new CWhileStatementNode();
	}
}
