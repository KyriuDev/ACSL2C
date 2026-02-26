package visitors;

import constants.CBinaryOperator;
import constants.CDeclarationSpecifier;
import constants.CType;
import constants.CUnaryOperator;
import misc.Utils;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.c.ICASTSimpleDeclSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.c.*;

import java.util.Arrays;

public class RecursiveVisitor
{
	/**
	 * Name:        RecursiveVisitor.java
	 * Content:	    This class defines a recursive AST visitor that allows "simpler" (in the sense of "more similar to
	 *              what we have in mind") traversal of the AST, by using a recursion-based DFS.
	 * Author:      Quentin Nivon
	 * Email:       quentin.nivon@uol.de
	 * Creation:    25/02/26
	 */

	private static final boolean ISOLATE_FUNCTION_BLOCKS = true;
	private final IASTNode rootNode;

	//Constructors

	public RecursiveVisitor(final IASTNode rootNode)
	{
		this.rootNode = rootNode;
	}

	//Public methods

	public void printAST()
	{
		final StringBuilder builder = new StringBuilder();
		this.printAST(this.rootNode, 0, builder);
		System.out.println(builder);
	}

	//Private methods

	private void printAST(final IASTNode node,
					 	  final int depth,
						  final StringBuilder builder)
	{
		final String nodeData = this.addNodeSpecificDataV2(node);

		builder.append(Utils.addLeadingTabulations(depth))
				.append(String.format("- Node \"%s\" has ", nodeData.isEmpty() ? node.toString() : nodeData));

		if (node.getChildren() != null
			&& node.getChildren().length > 0)
		{
			builder.append(String.format("%d children:\n", node.getChildren().length));
		}
		else
		{
			builder.append("no child.\n");
		}

		for (final IASTNode child : node.getChildren())
		{
			if (ISOLATE_FUNCTION_BLOCKS
				&& node instanceof CASTTranslationUnit)
			{
				builder.append("\n");
			}

			printAST(child, depth + 1, builder);
		}
	}

	private String addNodeSpecificDataV2(final IASTNode node)
	{
		if (node instanceof CASTFunctionDefinition)
		{
			final CASTFunctionDefinition functionDefinition = (CASTFunctionDefinition) node;
		}
		else if (node instanceof CASTSimpleDeclSpecifier)
		{
			final CASTSimpleDeclSpecifier simpleDeclSpecifier = (CASTSimpleDeclSpecifier) node;

			return CDeclarationSpecifier.convertEclipseCDTTypesToThis(simpleDeclSpecifier.getStorageClass()).getSpecifier()
					+ (simpleDeclSpecifier.getStorageClass() == IASTDeclSpecifier.sc_unspecified ? "" : " ")
					+ CType.convertEclipseCDTTypesToThis(simpleDeclSpecifier.getType()).getType();

		}
		else if (node instanceof CASTFunctionDeclarator)
		{
			final CASTFunctionDeclarator functionDeclarator = (CASTFunctionDeclarator) node;
			functionDeclarator.getFunctionScope();
		}
		else if (node instanceof CASTCompoundStatement)
		{
			final CASTCompoundStatement compoundStatement = (CASTCompoundStatement) node;
		}
		else if (node instanceof CASTDeclarationStatement)
		{
			final CASTDeclarationStatement declarationStatement = (CASTDeclarationStatement) node;
		}
		else if (node instanceof CASTSimpleDeclaration)
		{
			final CASTSimpleDeclaration simpleDeclaration = (CASTSimpleDeclaration) node;
		}
		else if (node instanceof CASTDeclarator)
		{
			final CASTDeclarator declarator = (CASTDeclarator) node;
		}
		else if (node instanceof CASTEqualsInitializer)
		{
			final CASTEqualsInitializer equalsInitializer = (CASTEqualsInitializer) node;
			return CBinaryOperator.ASSIGNMENT.getOperator();
		}
		else if (node instanceof CASTBinaryExpression)
		{
			final CASTBinaryExpression binaryExpression = (CASTBinaryExpression) node;
			return CBinaryOperator.convertEclipseCDTBinaryOperatorToThis(binaryExpression.getOperator()).getOperator();
		}
		else if (node instanceof CASTIdExpression)
		{
			final CASTIdExpression idExpression = (CASTIdExpression) node;
		}
		else if (node instanceof CASTReturnStatement)
		{
			final CASTReturnStatement returnStatement = (CASTReturnStatement) node;
			return "return";
		}
		else if (node instanceof CASTTranslationUnit)
		{
			final CASTTranslationUnit translationUnit = (CASTTranslationUnit) node;
		}
		else if (node instanceof CASTParameterDeclaration)
		{
			final CASTParameterDeclaration parameterDeclaration = (CASTParameterDeclaration) node;
		}
		else if (node instanceof CASTFunctionCallExpression)
		{
			final CASTFunctionCallExpression functionCallExpression = (CASTFunctionCallExpression) node;
		}
		else if (node instanceof CASTWhileStatement)
		{
			return "while";
		}
		else if (node instanceof CASTExpressionStatement)
		{
			final CASTExpressionStatement expressionStatement = (CASTExpressionStatement) node;
		}
		else if (node instanceof CASTUnaryExpression)
		{
			final CASTUnaryExpression unaryExpression = (CASTUnaryExpression) node;
			return CUnaryOperator.convertEclipseCDTUnaryOperatorToThis(unaryExpression.getOperator()).getOperator();
		}
		else if (node instanceof CASTName)
		{
			return node.toString();
		}
		else
		{
			System.out.printf("Node type \"%s\" is not supported yet.%n", node.toString());
			/*throw new UnsupportedOperationException(
				String.format("Node type \"%s\" is not supported yet.", node.toString())
			);*/
		}

		return "";
	}

	private String addNodeSpecificData(final IASTNode node)
	{
		if (node instanceof IASTName)
		{
			final IASTName iastName = (IASTName) node;
			return iastName.resolveBinding().getName();
		}
		else if (node instanceof IASTParameterDeclaration)
		{
			final IASTParameterDeclaration iastParameterDeclaration = (IASTParameterDeclaration) node;
			return String.format("%s/%s", iastParameterDeclaration.getDeclarator().toString(), iastParameterDeclaration.getDeclSpecifier().toString());
		}
		else if (node instanceof IASTDeclaration)
		{
			final IASTDeclaration declaration = (IASTDeclaration) node;

			if (declaration instanceof IASTFunctionDefinition)
			{
				final IASTFunctionDefinition iastFunctionDefinition = (IASTFunctionDefinition) declaration;

				if (iastFunctionDefinition instanceof CASTFunctionDefinition)
				{
					final CASTFunctionDefinition castFunctionDefinition = (CASTFunctionDefinition) iastFunctionDefinition;

					//We obtained an object of type "CASTFunctionDefinition"
				}
			}
		}
		else if (node instanceof IASTAttributeOwner)
		{
			final IASTAttributeOwner attributeOwner = (IASTAttributeOwner) node;

			if (attributeOwner instanceof IASTDeclSpecifier)
			{
				final IASTDeclSpecifier declSpecifier = (IASTDeclSpecifier) attributeOwner;

				if (declSpecifier instanceof IASTSimpleDeclSpecifier)
				{
					final IASTSimpleDeclSpecifier simpleDeclSpecifier = (IASTSimpleDeclSpecifier) declSpecifier;

					if (simpleDeclSpecifier instanceof ICASTSimpleDeclSpecifier)
					{
						final ICASTSimpleDeclSpecifier iCASTSimpleDeclSpecifier = (ICASTSimpleDeclSpecifier) simpleDeclSpecifier;

						if (iCASTSimpleDeclSpecifier instanceof CASTSimpleDeclSpecifier)
						{
							final CASTSimpleDeclSpecifier castSimpleDeclSpecifier = (CASTSimpleDeclSpecifier) iCASTSimpleDeclSpecifier;

							//We obtained an object of type "CASTSimpleDeclSpecifier"
						}
					}
				}
			}
			else if (attributeOwner instanceof IASTDeclarator)
			{
				final IASTDeclarator iastDeclarator = (IASTDeclarator) attributeOwner;

				if (iastDeclarator instanceof IASTFunctionDeclarator)
				{
					final IASTFunctionDeclarator iastFunctionDeclarator = (IASTFunctionDeclarator) iastDeclarator;

					if (iastFunctionDeclarator instanceof IASTStandardFunctionDeclarator)
					{
						final IASTStandardFunctionDeclarator iastStandardFunctionDeclarator = (IASTStandardFunctionDeclarator) iastFunctionDeclarator;

						if (iastStandardFunctionDeclarator instanceof CASTFunctionDeclarator)
						{
							final CASTFunctionDeclarator castFunctionDeclarator = (CASTFunctionDeclarator) iastStandardFunctionDeclarator;

							//We obtained an object of type "CASTFunctionDeclarator"
						}
					}
				}
			}
			else if (node instanceof IASTStatement)
			{
				final IASTStatement iastStatement = (IASTStatement) node;

				if (iastStatement instanceof IASTReturnStatement)
				{
					final IASTReturnStatement returnStatement = (IASTReturnStatement) iastStatement;

					if (returnStatement instanceof CASTReturnStatement)
					{

					}
				}
				else if (iastStatement instanceof IASTCompoundStatement)
				{
					final IASTCompoundStatement iastCompoundStatement = (IASTCompoundStatement) iastStatement;

					if (iastCompoundStatement instanceof CASTCompoundStatement)
					{
						final CASTCompoundStatement castCompoundStatement = (CASTCompoundStatement) iastCompoundStatement;

						//We obtained an object of type "CASTCompoundStatement"
					}
				}
				else if (iastStatement instanceof IASTDeclarationStatement)
				{
					final IASTDeclarationStatement iastDeclarationStatement = (IASTDeclarationStatement) iastStatement;

					if (iastDeclarationStatement instanceof CASTDeclarationStatement)
					{
						final CASTDeclarationStatement castDeclarationStatement = (CASTDeclarationStatement) iastDeclarationStatement;

						//We obtained an object of type "CASTDeclarationStatement"
					}
				}
			}
		}

		return "";
	}
}
