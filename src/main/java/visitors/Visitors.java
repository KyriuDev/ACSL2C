package visitors;

import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.*;
import org.eclipse.cdt.internal.core.dom.parser.ASTAmbiguousNode;

import java.util.Arrays;

/**
 * Name:        Visitors.java
 * Content:	    Multiple kinds of AST visitors as implemented in "org.eclipse.cdt.core.dom.ast.ASTVisitor.java".
 * 				If I understood correctly, this is a kind of "linear" traversal, which does not exactly suits our
 * 				needs. It will probably not be used except potentially for testing purposes.
 * 				The class separates between C and C++ visitors (if any), reason why some methods are not callable
 * 				depending on the used visitors (this is also a way to verify that the concept has been correctly
 * 				understood).
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    25/02/26
 */

public class Visitors
{
	//Constructors

	private Visitors()
	{

	}
	
	//C Visitors

	/**
	 * This method implements a simple "print visitor" that prints the type and the name of the nodes that it
	 * encounters, along with a string representation of its children (if any).
	 * The boolean parameter of the visitor is set to "true" to force the visit of every node.
	 * It will probably mostly be used for debugging purposes.
	 *
	 * @return the visitor
	 */
	public static ASTVisitor getCPrintVisitor()
	{
		return new ASTVisitor(true)
		{
			@Override
			public int visit(final IASTName name)
			{
				System.out.printf(
					"- IASTName %s has the following children: %s%n",
					name.toString(),
					Arrays.toString(name.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTStatement statement)
			{
				System.out.printf(
					"- IASTStatement %s has the following children: %s%n",
					statement.toString(),
					Arrays.toString(statement.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTDeclaration declaration)
			{
				System.out.printf(
					"- IASTDeclaration %s has the following children: %s%n",
					declaration.toString(),
					Arrays.toString(declaration.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTTranslationUnit tu)
			{
				System.out.printf(
					"- IASTTranslationUnit %s has the following children: %s%n",
					tu.toString(),
					Arrays.toString(tu.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTInitializer initializer)
			{
				System.out.printf(
					"- IASTInitializer %s has the following children: %s%n",
					initializer.toString(),
					Arrays.toString(initializer.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTParameterDeclaration parameterDeclaration)
			{
				System.out.printf(
					"- IASTParameterDeclaration %s has the following children: %s%n",
					parameterDeclaration.toString(),
					Arrays.toString(parameterDeclaration.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTDeclarator declarator)
			{
				System.out.printf(
					"- IASTDeclarator %s has the following children: %s%n",
					declarator.toString(),
					Arrays.toString(declarator.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTDeclSpecifier declSpec)
			{
				System.out.printf(
					"- IASTDeclSpecifier %s has the following children: %s%n",
					declSpec.toString(),
					Arrays.toString(declSpec.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			/**
			 * @since 5.1
			 */
			@Override
			public int visit(final IASTArrayModifier arrayModifier)
			{
				System.out.printf(
					"- IASTArrayModifier %s has the following children: %s%n",
					arrayModifier.toString(),
					Arrays.toString(arrayModifier.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			/**
			 * @since 5.1
			 */
			@Override
			public int visit(final IASTPointerOperator ptrOperator)
			{
				System.out.printf(
					"- IASTPointerOperator %s has the following children: %s%n",
					ptrOperator.toString(),
					Arrays.toString(ptrOperator.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			/**
			 * @since 5.4
			 */
			@Override
			public int visit(final IASTAttribute attribute)
			{
				System.out.printf(
					"- IASTAttribute %s has the following children: %s%n",
					attribute.toString(),
					Arrays.toString(attribute.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			/**
			 * @since 5.7
			 */
			@Override
			public int visit(final IASTAttributeSpecifier specifier)
			{
				System.out.printf(
					"- IASTAttributeSpecifier %s has the following children: %s%n",
					specifier.toString(),
					Arrays.toString(specifier.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			/**
			 * @since 5.4
			 */
			@Override
			public int visit(final IASTToken token)
			{
				System.out.printf(
					"- IASTToken %s has the following children: %s%n",
					token.toString(),
					Arrays.toString(token.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTExpression expression)
			{
				System.out.printf(
					"- IASTExpression %s has the following children: %s%n",
					expression.toString(),
					Arrays.toString(expression.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTTypeId typeId)
			{
				System.out.printf(
					"- IASTTypeId %s has the following children: %s%n",
					typeId.toString(),
					Arrays.toString(typeId.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTEnumerationSpecifier.IASTEnumerator enumerator)
			{
				System.out.printf(
					"- IASTEnumerationSpecifier.IASTEnumerator %s has the following children: %s%n",
					enumerator.toString(),
					Arrays.toString(enumerator.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			@Override
			public int visit(final IASTProblem problem)
			{
				System.out.printf(
					"- IASTProblem %s has the following children: %s%n",
					problem.toString(),
					Arrays.toString(problem.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			/**
			 * @since 5.3
			 */
			@Override
			public int visit(final ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier baseSpecifier)
			{
				throw new UnsupportedOperationException(
					"Type \"ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier\" should not appear in C programs " +
					"(reserved to C++)."
				);
			}

			/**
			 * @since 5.3
			 */
			@Override
			public int visit(final ICPPASTNamespaceDefinition namespaceDefinition)
			{
				throw new UnsupportedOperationException(
					"Type \"ICPPASTNamespaceDefinition\" should not appear in C programs (reserved to C++)."
				);
			}

			/**
			 * @since 5.3
			 */
			@Override
			public int visit(final ICPPASTTemplateParameter templateParameter)
			{
				throw new UnsupportedOperationException(
					"Type \"ICPPASTTemplateParameter\" should not appear in C programs (reserved to C++)."
				);
			}

			/**
			 * @since 5.3
			 */
			@Override
			public int visit(final ICPPASTCapture capture)
			{
				throw new UnsupportedOperationException(
					"Type \"ICPPASTCapture\" should not appear in C programs (reserved to C++)."
				);
			}

			/**
			 * @since 5.3
			 */
			@Override
			public int visit(final ICASTDesignator designator)
			{
				System.out.printf(
					"- ICASTDesignator %s has the following children: %s%n",
					designator.toString(),
					Arrays.toString(designator.getChildren())
				);

				return ASTVisitor.PROCESS_CONTINUE;
			}

			/**
			 * @since 6.0
			 */
			@Override
			public int visit(final ICPPASTDesignator designator)
			{
				throw new UnsupportedOperationException(
					"Type \"ICPPASTDesignator\" should not appear in C programs (reserved to C++)."
				);
			}

			/**
			 * @since 5.7
			 */
			@Override
			public int visit(final ICPPASTVirtSpecifier virtSpecifier)
			{
				throw new UnsupportedOperationException(
					"Type \"ICPPASTVirtSpecifier\" should not appear in C programs (reserved to C++)."
				);
			}

			/**
			 * @since 5.7
			 */
			@Override
			public int visit(final ICPPASTClassVirtSpecifier classVirtSpecifier)
			{
				throw new UnsupportedOperationException(
					"Type \"ICPPASTClassVirtSpecifier\" should not appear in C programs (reserved to C++)."
				);
			}

			/**
			 * @since 5.8
			 */
			@Override
			public int visit(final ICPPASTDecltypeSpecifier decltypeSpecifier)
			{
				throw new UnsupportedOperationException(
					"Type \"ICPPASTDecltypeSpecifier\" should not appear in C programs (reserved to C++)."
				);
			}
		};
	}

	//C++ Visitors
}
