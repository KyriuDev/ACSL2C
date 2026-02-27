package writing;

import constants.*;
import exceptions.UnhandledElementException;
import misc.CommandLineParser;
import misc.Utils;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.c.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;

/**
 * Name:        Writer.java
 * Content:     This class aims at writing back to a file a C program that has been parsed by Parser.java.
 * 				TODO: Manage the (ACSL) comments with SYNTAX
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    27/02/26
 */

public class Writer
{
	private static final String GENERATED_C_FILE_NAME = "generated.c";
	private final CASTTranslationUnit astRootNode;
	private final CommandLineParser commandLineParser;

	//Constructors

	public Writer(final CASTTranslationUnit astRootNode,
				  final CommandLineParser commandLineParser)
	{
		this.astRootNode = astRootNode;
		this.commandLineParser = commandLineParser;
	}

	//Public methods

	/**
	 * This is the principal method of the class, in the sense that it is the only one that is available from the
	 * outside.
	 * It basically writes to the file GENERATED_C_FILE_NAME located in the working directory the C program
	 * corresponding to the AST whose root node is astRootNode.
	 *
	 * @throws FileNotFoundException if the File given to the PrintWriter is invalid/unusable.
	 */
	public void writeToFile() throws FileNotFoundException, UnhandledElementException
	{
		final File outputFile = new File(Paths.get(
			((File) this.commandLineParser.get(CommandLineOption.WORKING_DIRECTORY)).getAbsolutePath(),
			GENERATED_C_FILE_NAME
		).toString());
		final PrintWriter printWriter = new PrintWriter(outputFile);

		this.dumpFile(printWriter);

		printWriter.flush();
		printWriter.close();
	}

	//Private methods

	/**
	 * This method is the initial dumping method called.
	 * It is in charge of handling the topmost elements of the AST, without considering the CASTTranslationUnit.
	 * <p>
	 * For now, the topmost elements of an Eclipse-CDT AST can be:
	 *     - A CASTSimpleDeclaration, which corresponds to a function declaration without definition (only the
	 * 	     signature of the function is given), or to a variable definition.
	 *     - A CASTFunctionDefinition, which corresponds to a function definition.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 */
	private void dumpFile(final PrintWriter printWriter) throws UnhandledElementException
	{
		boolean first = true;

		for (final IASTNode iastNode : this.astRootNode.getChildren())
		{
			if (!first)
			{
				printWriter.println();
			}

			if (iastNode instanceof CASTSimpleDeclaration)
			{
				this.dumpSimpleDeclaration(printWriter, (CASTSimpleDeclaration) iastNode, 0);
				printWriter.print(Char.RIGHT_BRACKET);
				printWriter.print(Char.SEMI_COLON);
			}
			else if (iastNode instanceof CASTFunctionDefinition)
			{
				this.dumpFunctionDefinition(printWriter, (CASTFunctionDefinition) iastNode);
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as topmost element of a C program.",
						iastNode.toString()
					)
				);
			}

			printWriter.println();
			first = false;
		}
	}

	/**
	 * This method is in charge of dumping the CASTFunctionDefinition objects.
	 * <p>
	 * For now, CASTFunctionDefinition objects are composed of:
	 * - A CASTSimpleDeclSpecifier, which contains the function return type and scope;
	 * - A CASTFunctionDeclarator, which contains the name of the function, and the information of its parameters;
	 * - A CASTCompoundStatement, which is a compound statement corresponding to the body of the function.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param definition the function definition to handle.
	 */
	private void dumpFunctionDefinition(final PrintWriter printWriter,
										final CASTFunctionDefinition definition) throws UnhandledElementException
	{
		for (final IASTNode child : definition.getChildren())
		{
			if (child instanceof CASTSimpleDeclSpecifier)
			{
				this.dumpSimpleDeclarationSpecifier(printWriter, (CASTSimpleDeclSpecifier) child, 0, false);
			}
			else if (child instanceof CASTFunctionDeclarator)
			{
				this.dumpFunctionDeclarator(printWriter, (CASTFunctionDeclarator) child, 0);
				printWriter.print(Char.RIGHT_BRACKET);
				printWriter.print(Char.SPACE);
				printWriter.println(Char.LEFT_CURVY_BRACKET);
			}
			else if (child instanceof CASTCompoundStatement)
			{
				this.dumpCompoundStatement(printWriter, (CASTCompoundStatement) child, 1);
			}
		}

		printWriter.print(Char.RIGHT_CURVY_BRACKET);
	}

	/**
	 * This method is in charge of dumping the CASTSimpleDeclaration objects.
	 * <p>
	 * For now, CASTSimpleDeclaration objects are composed of:
	 * - A CASTSimpleDeclSpecifier, which corresponds to a variable/parameter/function declaration;
	 * - A CASTFunctionDeclarator, which corresponds to the signature of a function;
	 * - A CASTDeclarator, which corresponds to a variable declaration (parameter, new variable).
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param declaration the declaration to handle.
	 * @param nbTabs the number of tabs to insert before the declaration itself (for good-looking code purposes).
	 */
	private void dumpSimpleDeclaration(final PrintWriter printWriter,
									   final CASTSimpleDeclaration declaration,
									   final int nbTabs) throws UnhandledElementException
	{
		for (final IASTNode child : declaration.getChildren())
		{
			if (child instanceof CASTSimpleDeclSpecifier)
			{
				this.dumpSimpleDeclarationSpecifier(printWriter, (CASTSimpleDeclSpecifier) child, nbTabs, true);
			}
			else if (child instanceof CASTFunctionDeclarator)
			{
				this.dumpFunctionDeclarator(printWriter, (CASTFunctionDeclarator) child, nbTabs);
			}
			else if (child instanceof CASTDeclarator)
			{
				this.dumpDeclarator(printWriter, (CASTDeclarator) child, nbTabs);
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTSimpleDeclaration element.",
						child.toString()
					)
				);
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTSimpleDeclSpecifier objects.
	 * <p>
	 * Such objects are (for now at least) "final objects" in the sense that they self-contain all the information
	 * that must be dumped from them, thus they do not perform any recursive call on their children.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param declSpecifier the declaration specifier to handle.
	 * @param nbTabs the number of tabs to insert before the declaration specifier itself (for good-looking code
	 *        purposes).
	 * @param indent a boolean indicating whether the declaration specifier should be indented or not.
	 */
	private void dumpSimpleDeclarationSpecifier(final PrintWriter printWriter,
												final CASTSimpleDeclSpecifier declSpecifier,
												final int nbTabs,
												final boolean indent)
	{
		if (indent)
		{
			printWriter.print(Utils.addLeadingTabulations(nbTabs));
		}

		final CDeclarationSpecifier declarationSpecifier = CDeclarationSpecifier.convertEclipseCDTTypesToThis(declSpecifier.getStorageClass());
		printWriter.print(declarationSpecifier.getSpecifier());

		if (declarationSpecifier != CDeclarationSpecifier.UNSPECIFIED)
		{
			printWriter.print(Char.SPACE);
		}

		final CType type = CType.convertEclipseCDTTypesToThis(declSpecifier.getType());
		printWriter.print(type.getType());
	}

	/**
	 * This method is in charge of dumping the CASTFunctionDeclarator objects.
	 * <p>
	 * For now, CASTFunctionDeclarator objects are composed of:
	 * - A CASTName, which is a string literal corresponding to the name of the function;
	 * - A CASTParameterDeclaration, which corresponds to the function's parameter declaration.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param functionDeclarator the declaration to handle.
	 * @param nbTabs the number of tabs to insert before the declaration itself (for good-looking code purposes).
	 */
	private void dumpFunctionDeclarator(final PrintWriter printWriter,
										final CASTFunctionDeclarator functionDeclarator,
										final int nbTabs) throws UnhandledElementException
	{
		String parametersSeparator = "";

		for (final IASTNode child : functionDeclarator.getChildren())
		{
			if (child instanceof CASTName)
			{
				printWriter.print(Char.SPACE);
				this.dumpName(printWriter, (CASTName) child);
				printWriter.print(Char.LEFT_BRACKET);
			}
			else if (child instanceof CASTParameterDeclaration)
			{
				printWriter.print(parametersSeparator);
				this.dumpParameter(printWriter, (CASTParameterDeclaration) child, nbTabs);
				parametersSeparator = Str.COMA_AND_SPACE;
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTFunctionDeclarator element.",
						child.toString()
					)
				);
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTParameterDeclaration objects.
	 * <p>
	 * For now, CASTParameterDeclaration objects are composed of:
	 * - A CASTSimpleDeclSpecifier, which corresponds to a variable/parameter/function declaration;
	 * - A CASTDeclarator, which corresponds to a variable declaration (parameter, new variable).
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param parameterDeclaration the parameter declaration to handle.
	 * @param nbTabs the number of tabs to insert before the parameter declaration itself (for good-looking code
	 *       		 purposes).
	 */
	private void dumpParameter(final PrintWriter printWriter,
							   final CASTParameterDeclaration parameterDeclaration,
							   final int nbTabs) throws UnhandledElementException
	{
		for (final IASTNode child : parameterDeclaration.getChildren())
		{
			if (child instanceof CASTSimpleDeclSpecifier)
			{
				this.dumpSimpleDeclarationSpecifier(printWriter, (CASTSimpleDeclSpecifier) child, nbTabs, false);
			}
			else if (child instanceof CASTDeclarator)
			{
				this.dumpDeclarator(printWriter, (CASTDeclarator) child, nbTabs);
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTParameterDeclaration element.",
						child.toString()
					)
				);
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTDeclarator objects.
	 * <p>
	 * For now, CASTDeclarator objects are composed of:
	 * - A CASTName, which is a string literal corresponding to a variable name, a noninteger variable value, etc;
	 * - A CASTEqualsInitializer, which corresponds to a variable initialization.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param declarator the declarator to handle.
	 * @param nbTabs the number of tabs to insert before the declarator itself (for good-looking code purposes).
	 */
	private void dumpDeclarator(final PrintWriter printWriter,
								final CASTDeclarator declarator,
								final int nbTabs) throws UnhandledElementException
	{
		for (final IASTNode child : declarator.getChildren())
		{
			if (child instanceof CASTName)
			{
				final CASTName name = (CASTName) child;

				if (name.getSimpleID() != null
					&& name.getSimpleID().length != 0)
				{
					printWriter.print(Char.SPACE);
					this.dumpName(printWriter, (CASTName) child);
				}
			}
			else if (child instanceof CASTEqualsInitializer)
			{
				this.dumpEqualsInitializer(printWriter, (CASTEqualsInitializer) child, nbTabs);
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTDeclarator element.",
						child.toString()
					)
				);
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTCompoundStatement objects.
	 * <p>
	 * For now, CASTCompoundStatement objects are composed of:
	 * - A CASTWhileStatement, which corresponds to a While statement;
	 * - A CASTReturnStatement, which corresponds to a Return statement;
	 * - A CASTExpressionStatement, which corresponds to a classical statement (x--, sum(x,y), 2 * y, etc.);
	 * - A CASTDeclarationStatement, which corresponds to a variable declaration statement (x = 78, etc.).
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param compoundStatement the compound statement to handle.
	 * @param nbTabs the number of tabs to insert before the compound statement itself (for good-looking code purposes).
	 */
	private void dumpCompoundStatement(final PrintWriter printWriter,
									   final CASTCompoundStatement compoundStatement,
									   final int nbTabs) throws UnhandledElementException
	{
		for (final IASTNode child : compoundStatement.getChildren())
		{
			if (child instanceof CASTWhileStatement)
			{
				this.dumpWhileStatement(printWriter, (CASTWhileStatement) child, nbTabs);
			}
			else if (child instanceof CASTReturnStatement)
			{
				this.dumpReturnStatement(printWriter, (CASTReturnStatement) child, nbTabs);
			}
			else if (child instanceof CASTExpressionStatement)
			{
				this.dumpExpressionStatement(printWriter, (CASTExpressionStatement) child, nbTabs);
			}
			else if (child instanceof CASTDeclarationStatement)
			{
				this.dumpDeclarationStatement(printWriter, (CASTDeclarationStatement) child, nbTabs);
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTWhileStatement objects.
	 * <p>
	 * For now, CASTWhileStatement objects are composed of:
	 * - A CASTBinaryExpression, which is a binary expression corresponding to the While condition;
	 * - A CASTIdExpression, which is an id-expression corresponding to the While condition;
	 * - A CASTCompoundStatement, which is a compound statement corresponding to the body of the While.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param whileStatement the while statement to handle.
	 * @param nbTabs the number of tabs to insert before the while statement itself (for good-looking code purposes).
	 */
	private void dumpWhileStatement(final PrintWriter printWriter,
									final CASTWhileStatement whileStatement,
									final int nbTabs) throws UnhandledElementException
	{
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.WHILE.getKeyword());
		printWriter.print(Char.SPACE);
		printWriter.print(Char.LEFT_BRACKET);

		boolean headerManaged = false;
		boolean bodyManaged = false;

		for (final IASTNode child : whileStatement.getChildren())
		{
			if (child instanceof CASTBinaryExpression
				|| child instanceof CASTIdExpression)
			{
				//Header
				if (headerManaged)
				{
					throw new UnhandledElementException(
						String.format(
							"While object \"%s\" has multiple headers (CASTBinaryExpression/CASTIdExpression).",
							child.toString()
						)
					);
				}

				headerManaged = true;

				if (child instanceof CASTBinaryExpression)
				{
					this.dumpBinaryExpression(printWriter, (CASTBinaryExpression) child, nbTabs, false);
				}
				else
				{
					this.dumpIdExpression(printWriter, (CASTIdExpression) child);
				}

				printWriter.print(Char.RIGHT_BRACKET);
				printWriter.print(Char.SPACE);
				printWriter.println(Char.LEFT_CURVY_BRACKET);
			}
			else if (child instanceof CASTCompoundStatement)
			{
				//Body
				if (bodyManaged)
				{
					throw new UnhandledElementException(
						String.format(
							"While object \"%s\" has multiple bodies (CASTCompoundStatement).",
							child.toString()
						)
					);
				}

				bodyManaged = true;

				this.dumpCompoundStatement(printWriter, (CASTCompoundStatement) child, nbTabs + 1);
				printWriter.print(Utils.addLeadingTabulations(nbTabs));
				printWriter.println(Char.RIGHT_CURVY_BRACKET);
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTWhileStatement element.",
						child.toString()
					)
				);
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTReturnStatement objects.
	 * <p>
	 * For now, CASTReturnStatement objects are composed of:
	 * - A CASTBinaryExpression, which is a binary expression computing the return value;
	 * - A CASTIdExpression, which is an id-expression containing the return value.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program.
	 * @param returnStatement the return statement to handle.
	 * @param nbTabs the number of tabs to insert before the return statement itself (for good-looking code purposes).
	 */
	private void dumpReturnStatement(final PrintWriter printWriter,
									 final CASTReturnStatement returnStatement,
									 final int nbTabs) throws UnhandledElementException
	{
		printWriter.println();
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.RETURN.getKeyword());

		if (returnStatement.getChildren() != null
			&& returnStatement.getChildren().length != 0)
		{
			printWriter.print(Char.SPACE);

			for (final IASTNode child : returnStatement.getChildren())
			{
				if (child instanceof CASTIdExpression)
				{
					this.dumpIdExpression(printWriter, (CASTIdExpression) child);
				}
				else if (child instanceof CASTBinaryExpression)
				{
					this.dumpBinaryExpression(printWriter, (CASTBinaryExpression) child, nbTabs, false);
				}
			}
		}

		printWriter.println(Char.SEMI_COLON);
	}

	/**
	 * This method is in charge of dumping the CASTBinaryExpression objects.
	 * <p>
	 * For now, CASTBinaryExpression objects are composed of:
	 * - A CASTIdExpression, which is an id-expression corresponding to the left (resp. right) hand side of the
	 * 	 expression;
	 * - A string literal representing an integer value, corresponding to the left (resp. right) hand side of the
	 * 	 expression;
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program;
	 * @param binaryExpression the binary expression to handle;
	 * @param nbTabs the number of tabs to insert before the binary expression itself (for good-looking code purposes);
	 * @param indent a boolean indicating whether the binary expression should be indented or not.   
	 */
	private void dumpBinaryExpression(final PrintWriter printWriter,
									  final CASTBinaryExpression binaryExpression,
									  final int nbTabs,
									  final boolean indent) throws UnhandledElementException
	{
		if (indent)
		{
			printWriter.print(Utils.addLeadingTabulations(nbTabs));
		}

		boolean leftPartHandled = false;

		for (final IASTNode child : binaryExpression.getChildren())
		{
			if (child instanceof CASTIdExpression)
			{
				if (leftPartHandled)
				{
					printWriter.print(Char.SPACE);
					printWriter.print(
						CBinaryOperator.convertEclipseCDTBinaryOperatorToThis(binaryExpression.getOperator()).getOperator()
					);
					printWriter.print(Char.SPACE);
				}
				else
				{
					leftPartHandled = true;
				}

				this.dumpIdExpression(printWriter, (CASTIdExpression) child);
			}
			else
			{
				if (Utils.isAnInt(child.toString()))
				{
					if (leftPartHandled)
					{
						printWriter.print(Char.SPACE);
						printWriter.print(
							CBinaryOperator.convertEclipseCDTBinaryOperatorToThis(binaryExpression.getOperator()).getOperator()
						);
						printWriter.print(Char.SPACE);
					}
					else
					{
						leftPartHandled = true;
					}

					printWriter.print(child);
				}
				else
				{
					throw new UnhandledElementException(
						String.format(
							"Object \"%s\" is not yet handled as child of a CASTBinaryExpression element.",
							child.toString()
						)
					);
				}
			}
		}
	}


	/**
	 * This method is in charge of dumping the CASTIdExpression objects.
	 * <p>
	 * For now, CASTIdExpression objects are composed of:
	 * - A CASTName, which is a string literal corresponding to a variable name, a noninteger variable value, etc.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program;
	 * @param idExpression the id-expression to handle.
	 */
	private void dumpIdExpression(final PrintWriter printWriter,
								  final CASTIdExpression idExpression) throws UnhandledElementException
	{
		for (final IASTNode child : idExpression.getChildren())
		{
			if (child instanceof CASTName)
			{
				this.dumpName(printWriter, (CASTName) child);
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTIdExpression element.",
						child.toString()
					)
				);
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTName objects.
	 * <p>
	 * Such objects are (for now at least) "final objects" in the sense that they self-contain all the information
	 * that must be dumped from them, thus they do not perform any recursive call on their children.
	 *
	 * @param printWriter the print writer used to write the program;
	 * @param name the name to handle.
	 */
	private void dumpName(final PrintWriter printWriter,
						  final CASTName name)
	{
		if (name.getSimpleID() != null
			&& name.getSimpleID().length != 0)
		{
			printWriter.print(name.getSimpleID());
		}
	}

	/**
	 * This method is in charge of dumping the CASTExpressionStatement objects.
	 * <p>
	 * For now, CASTExpressionStatement objects are composed of:
	 * - A CASTUnaryExpression, which is a unary expression statement (x--, *p, etc.);
	 * - A CASTFunctionCallExpression, which is a function call statement (sum(x,y), diff(a, b), etc.);
	 * - A CASTBinaryExpression, which is a binary expression statement (x + y, 2 * z, etc.).
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program;
	 * @param expressionStatement the expression statement to handle;
	 * @param nbTabs the number of tabs to insert before the binary expression itself (for good-looking code purposes).
	 */
	private void dumpExpressionStatement(final PrintWriter printWriter,
										 final CASTExpressionStatement expressionStatement,
										 final int nbTabs) throws UnhandledElementException
	{
		for (final IASTNode child : expressionStatement.getChildren())
		{
			if (child instanceof CASTUnaryExpression)
			{
				this.dumpUnaryExpression(printWriter, (CASTUnaryExpression) child, nbTabs, true);
			}
			else if (child instanceof CASTFunctionCallExpression)
			{
				this.dumpFunctionCall(printWriter, (CASTFunctionCallExpression) child, nbTabs, true);
			}
			else if (child instanceof CASTBinaryExpression)
			{
				this.dumpBinaryExpression(printWriter, (CASTBinaryExpression) child, nbTabs, true);
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTExpressionStatement element.",
						child.toString()
					)
				);
			}

			printWriter.println(Char.SEMI_COLON);
		}
	}

	/**
	 * This method is in charge of dumping the CASTUnaryExpression objects.
	 * <p>
	 * For now, CASTUnaryExpression objects are composed of:
	 * - A unary operator (*, ++, --, -, !, etc.)
	 * - A value (79494, 12.5, "foo", etc.)
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program;
	 * @param unaryExpression the unary expression to handle;
	 * @param nbTabs the number of tabs to insert before the unary expression itself (for good-looking code purposes);
	 * @param indent a boolean value indicating whether the unary expression should be indented or not.   
	 */
	private void dumpUnaryExpression(final PrintWriter printWriter,
									 final CASTUnaryExpression unaryExpression,
									 final int nbTabs,
									 final boolean indent) throws UnhandledElementException
	{
		if (unaryExpression.getChildren().length != 1)
		{
			throw new UnhandledElementException(
				String.format(
					"Unary expressions were expected to have exactly one child, got %d",
					unaryExpression.getChildren().length
				)
			);
		}

		if (!(unaryExpression.getOperand() instanceof CASTIdExpression)
			&& !Utils.isAnInt(unaryExpression.getOperand().toString()))
		{
			throw new UnhandledElementException(
				String.format(
					"Object \"%s\" is not yet handled as child of a CASTUnaryExpression element.",
					unaryExpression.getOperand().toString()
				)
			);
		}

		if (indent)
		{
			printWriter.print(Utils.addLeadingTabulations(nbTabs));
		}

		if (Utils.isAnInt(unaryExpression.getOperand().toString()))
		{
			printWriter.print(unaryExpression.getOperand().toString());
		}
		else
		{
			final CUnaryOperator unaryOperator = CUnaryOperator.convertEclipseCDTUnaryOperatorToThis(unaryExpression.getOperator());

			if (unaryOperator.isPrefixOperator())
			{
				printWriter.print(unaryOperator.getOperator());
				this.dumpIdExpression(printWriter, (CASTIdExpression) unaryExpression.getOperand());
			}
			else
			{
				this.dumpIdExpression(printWriter, (CASTIdExpression) unaryExpression.getOperand());
				printWriter.print(unaryOperator.getOperator());
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTDeclarationStatement objects.
	 * <p>
	 * For now, CASTDeclarationStatement objects are composed of:
	 *     - A CASTSimpleDeclaration, which corresponds to a function declaration without definition (only the
	 * 	     signature of the function is given), or to a variable definition.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program;
	 * @param declarationStatement the declaration statement to handle;
	 * @param nbTabs the number of tabs to insert before the declaration statement itself (for good-looking code
	 *               purposes).
	 */
	private void dumpDeclarationStatement(final PrintWriter printWriter,
										  final CASTDeclarationStatement declarationStatement,
										  final int nbTabs) throws UnhandledElementException
	{
		for (final IASTNode child : declarationStatement.getChildren())
		{
			if (child instanceof CASTSimpleDeclaration)
			{
				this.dumpSimpleDeclaration(printWriter, (CASTSimpleDeclaration) child, nbTabs);
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTDeclarationStatement element.",
						child.toString()
					)
				);
			}
		}

		printWriter.println(Char.SEMI_COLON);
	}

	/**
	 * This method is in charge of dumping the CASTEqualsInitializer objects.
	 * <p>
	 * For now, CASTEqualsInitializer objects are composed of:
	 *     - A CASTFunctionCallExpression, which is a function call whose result is assigned to the variable;
	 *     - A CASTBinaryExpression, which is a binary expression whose result is assigned to the variable;
	 *     - A CASTUnaryExpression, which is a unary expression whose result is assigned to the variable;
	 *     - A CASTIdExpression, which is an id-expression whose result is assigned to the variable.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program;
	 * @param equalsInitializer the equals initializer to handle;
	 * @param nbTabs the number of tabs to insert before the equals initializer itself (for good-looking code purposes).
	 */
	private void dumpEqualsInitializer(final PrintWriter printWriter,
									   final CASTEqualsInitializer equalsInitializer,
									   final int nbTabs) throws UnhandledElementException
	{
		printWriter.print(Char.SPACE);
		printWriter.print(CBinaryOperator.ASSIGNMENT.getOperator());
		printWriter.print(Char.SPACE);

		for (final IASTNode child : equalsInitializer.getChildren())
		{
			if (child instanceof CASTFunctionCallExpression)
			{
				this.dumpFunctionCall(printWriter, (CASTFunctionCallExpression) child, nbTabs, false);
			}
			else if (child instanceof CASTBinaryExpression)
			{
				this.dumpBinaryExpression(printWriter, (CASTBinaryExpression) child, nbTabs, false);
			}
			else if (child instanceof CASTUnaryExpression)
			{
				this.dumpUnaryExpression(printWriter, (CASTUnaryExpression) child, nbTabs, false);
			}
			else if (child instanceof CASTIdExpression)
			{
				this.dumpIdExpression(printWriter, (CASTIdExpression) child);
			}
			else
			{
				if (Utils.isAnInt(child.toString()))
				{
					printWriter.print(child);
				}
				else
				{
					throw new UnhandledElementException(
						String.format(
							"Object \"%s\" is not yet handled as child of a CASTEqualsInitializer element.",
							child.toString()
						)
					);
				}
			}
		}
	}

	/**
	 * This method is in charge of dumping the CASTFunctionCallExpression objects.
	 * <p>
	 * For now, CASTFunctionCallExpression objects are composed of:
	 *     - A CASTIdExpression, which contains either the name of the function, or its parameters.
	 * <p>
	 * When this function encounters an unhandled object, it throws an UnhandledElementException to inform the user
	 * (actually, mostly the developer... :-)) that the current version of Writer does not know how to deal with the
	 * given object at the given location.
	 * Ideally, the ultimate version of this code should never raise such an exception.
	 *
	 * @param printWriter the print writer used to write the program;
	 * @param functionCallExpression the function call expression to handle;
	 * @param nbTabs the number of tabs to insert before the function call expression itself (for good-looking code
	 *               purposes);
	 * @param indent a boolean value indicating whether the function call expression should be indented or not.
	 */
	private void dumpFunctionCall(final PrintWriter printWriter,
								  final CASTFunctionCallExpression functionCallExpression,
								  final int nbTabs,
								  final boolean indent) throws UnhandledElementException
	{
		if (indent)
		{
			printWriter.print(Utils.addLeadingTabulations(nbTabs));
		}

		boolean functionNameHandled = false;
		String separator = Str.EMPTY_STRING;

		for (final IASTNode child : functionCallExpression.getChildren())
		{
			if (child instanceof CASTIdExpression)
			{
				if (functionNameHandled)
				{
					printWriter.print(separator);
					this.dumpIdExpression(printWriter, (CASTIdExpression) child);
					separator = Str.COMA_AND_SPACE;
				}
				else
				{
					this.dumpIdExpression(printWriter, (CASTIdExpression) child);
					printWriter.print(Char.LEFT_BRACKET);
					functionNameHandled = true;
				}
			}
			else
			{
				throw new UnhandledElementException(
					String.format(
						"Object \"%s\" is not yet handled as child of a CASTFunctionCallExpression element.",
						child.toString()
					)
				);
			}
		}

		printWriter.print(Char.RIGHT_BRACKET);
	}
}
