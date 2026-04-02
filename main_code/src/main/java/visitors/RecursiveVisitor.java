package visitors;

import constants.*;
import constants.c.*;
import exceptions.UnhandledElementException;
import misc.Utils;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.c.*;

/**
 * Name:        RecursiveVisitor.java
 * Content:	    This class defines a recursive AST visitor that allows "simpler" (in the sense of "more similar to
 * 				what we have in mind") traversal of the AST, by using a recursion-based DFS.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    25/02/26
 */

public class RecursiveVisitor
{
	private static final boolean ISOLATE_FUNCTION_BLOCKS = true;
	private final IASTNode rootNode;

	//Constructors

	public RecursiveVisitor(final IASTNode rootNode)
	{
		this.rootNode = rootNode;
	}

	//Public methods

	public void printAST() throws UnhandledElementException
	{
		final StringBuilder builder = new StringBuilder();
		this.printComments(this.rootNode);
		this.printAST(this.rootNode, 0, builder);
		System.out.println(builder);
	}

	//Private methods

	/**
	 * This method analyses the root node of the AST (which should normally be of type IASTTranslationUnit) and outputs
	 * on the standard output all the comments that were found in the program, along with their information (offset,
	 * length, starting line number, ending line number).
	 *
	 * @param rootNode the root node of the AST;
	 * @throws UnhandledElementException if the root node is not an IASTTranslationUnit.
	 */
	private void printComments(final IASTNode rootNode) throws UnhandledElementException
	{
		if (!(rootNode instanceof IASTTranslationUnit))
		{
			throw new UnhandledElementException(
				Color.getRedMessage(String.format(
					"The root node of the given AST should be of type IASTTranslationUnit, but we got |%s|!",
					rootNode.toString()
				))
			);
		}

		final IASTComment[] comments = ((IASTTranslationUnit) rootNode).getComments();

		System.out.println("---------COMMENTS-----------");
		System.out.printf("%d comments were found:%n", comments.length);

		for (final IASTComment comment : comments)
		{
			System.out.printf(
				"\t- Comment \"%s\" has offset %d, length %d, starting line number %d, and ending line number %d%n",
				comment.toString(),
				comment.getFileLocation().getNodeOffset(),
				comment.getFileLocation().getNodeLength(),
				comment.getFileLocation().getStartingLineNumber(),
				comment.getFileLocation().getEndingLineNumber()
			);
		}

		System.out.println("---------END COMMENTS----------\n");
	}

	/**
	 * This method recursively traverses the AST in a depth-first way and store the information of the encountered
	 * nodes in the given StringBuilder.
	 *
	 * @param node the node being managed
	 * @param depth the depth at which the node is located (represented by a number "depth" of tabulations)
	 * @param builder the builder containing the hierarchical representation of the tree
	 */
	private void printAST(final IASTNode node,
					 	  final int depth,
						  final StringBuilder builder)
	{
		final String nodeData = this.addNodeSpecificData(node);

		builder.append(Utils.addLeadingTabulations(depth))
				.append(String.format(
					"- Node \"%s\" (%s%s) has ",
					nodeData.isEmpty() ? node.toString() : nodeData,
					(!nodeData.isEmpty() ? node.toString() + " " : ""),
					node.getFileLocation() == null ? "" : "lines " + node.getFileLocation().getStartingLineNumber() + " to " + node.getFileLocation().getEndingLineNumber()
				));

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

	/**
	 * This method aims at inserting in textual representation of the AST the interesting information, that is,
	 * the function names, the operators used, the assignments, etc.
	 *
	 * @param node the considered node.
	 * @return the specific information of the considered node, or the empty string if there is no such information.
	 */
	private String addNodeSpecificData(final IASTNode node)
	{
		if (node instanceof CASTFunctionDefinition)
		{
			final CASTFunctionDefinition functionDefinition = (CASTFunctionDefinition) node;
		}
		else if (node instanceof CASTSimpleDeclSpecifier)
		{
			final CASTSimpleDeclSpecifier simpleDeclSpecifier = (CASTSimpleDeclSpecifier) node;

			return CStorageClass.convertEclipseCDTTypesToThis(simpleDeclSpecifier.getStorageClass()).getStorageClass()
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
		else if (node instanceof CASTPointer)
		{
			return "*";
		}
		else if (node instanceof CASTForStatement)
		{
			return "for";
		}
		else if (node instanceof CASTArraySubscriptExpression)
		{
			return "[]";
		}
		else if (node instanceof CASTIfStatement)
		{
			return "if";
		}
		else if (node instanceof CASTName)
		{
			return "CASTName: " + node;
		}
		else if (node instanceof CASTNullStatement)
		{
			return "null";
		}
		else if (node instanceof CASTTypedefNameSpecifier)
		{
			return CStorageClass.convertEclipseCDTTypesToThis(((CASTTypedefNameSpecifier) node).getStorageClass()).getStorageClass()
					+ (((CASTTypedefNameSpecifier) node).isConst() ? " const" : "")
					+ (((CASTTypedefNameSpecifier) node).isVolatile() ? " volatile" : "")
					+ (((CASTTypedefNameSpecifier) node).isInline() ? " inline" : "")
					+ (((CASTTypedefNameSpecifier) node).isRestrict() ? " restrict" : "")
					;
		}
		else if (node instanceof CASTLiteralExpression)
		{
			return String.format("Literal (%s)", node.toString());
		}
		else if (node instanceof CASTCompositeTypeSpecifier)
		{
			final CASTCompositeTypeSpecifier compositeTypeSpecifier = (CASTCompositeTypeSpecifier) node;
			return CStorageClass.convertEclipseCDTTypesToThis(compositeTypeSpecifier.getStorageClass()).toString();
		}
		else if (node instanceof CASTCastExpression)
		{
			final CASTCastExpression castExpression = (CASTCastExpression) node;
		}
		else if (node instanceof CASTElaboratedTypeSpecifier)
		{
			final CASTElaboratedTypeSpecifier elaboratedTypeSpecifier = (CASTElaboratedTypeSpecifier) node;

			return CElaboratedTypeSpecifier.convertEclipseCDTElaboratedTypeSpecifierToThis(elaboratedTypeSpecifier.getKind()).toString();
		}
		else if (node instanceof CASTTypeId)
		{
			final CASTTypeId typeId = (CASTTypeId) node;
		}
		else if (node instanceof CASTTypeIdExpression)
		{
			final CASTTypeIdExpression typeIdExpression = (CASTTypeIdExpression) node;

			return CTypeIdExpression.convertEclipseCDTTypeIdExpressionToThis(typeIdExpression.getOperator()).toString();
		}
		else if (node instanceof CASTFieldReference)
		{
			final CASTFieldReference fieldReference = (CASTFieldReference) node;
			return "(->)";
		}
		else if (node instanceof CASTLabelStatement)
		{
			final CASTLabelStatement labelStatement = (CASTLabelStatement) node;
			return "(\"goto\" label)";
		}
		else if (node instanceof CASTGotoStatement)
		{
			final CASTGotoStatement gotoStatement = (CASTGotoStatement) node;
		}
		else if (node instanceof CASTInitializerList)
		{
			final CASTInitializerList initializerList = (CASTInitializerList) node;
		}
		else if ((node instanceof CASTProblemDeclaration)
				|| (node instanceof CASTProblem))
		{
			//Badly parsed element => go red
			System.out.println(Color.getRedMessage(
				"Error: The C program is malformed, some of its content could not be read! The generated AST might" +
				" consequently be erroneous!"
			));
		}
		else
		{
			//Non-handled yet element => go yellow
			System.out.printf(Color.getYellowMessage(
				"Warning: Node type \"%s\" is not supported yet.%n"), node.toString()
			);
		}

		return "";
	}
}
