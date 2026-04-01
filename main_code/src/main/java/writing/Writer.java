package writing;

import ast.AbstractSyntaxNode;
import ast.c.*;
import constants.*;
import constants.c.*;
import dto.CComment;
import exceptions.UnhandledElementException;
import misc.CommandLineParser;
import misc.Utils;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.internal.core.dom.parser.c.*;
import parsing.CommentsHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PipedReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
	private static final boolean ADD_INSIGHTFUL_COMMENTS = true;
	private static final boolean GENERATE_FROM_ECLIPSE_CDT_TREE = false;
	private static final String GENERATED_C_FILE_NAME = "generated.c";
	private final CASTTranslationUnit eclipseCdtAstRootNode;
	private final CBaseNode internalAstRootNode;
	private final IASTPreprocessorIncludeStatement[] includeDirectives;
	private final IASTPreprocessorMacroDefinition[] macroDefinitions;
	private final CommandLineParser commandLineParser;
	private final CommentsHandler commentsHandler;
	private final List<CComment> trailingComments;
	//Boolean value for controlling the necessity of inserting a line jump, avoids multiple consecutive line jumps
	private boolean lineAlreadyJumped;

	//Constructors

	public Writer(final CASTTranslationUnit eclipseCdtAstRootNode,
				  final CommandLineParser commandLineParser,
				  final CommentsHandler commentsHandler)
	{
		this(
			eclipseCdtAstRootNode,
			null,
			eclipseCdtAstRootNode.getIncludeDirectives(),
			eclipseCdtAstRootNode.getMacroDefinitions(),
			commandLineParser,
			commentsHandler,
			null
		);
	}

	public Writer(final CBaseNode internalAstRootNode,
				  final IASTPreprocessorIncludeStatement[] includeStatements,
				  final IASTPreprocessorMacroDefinition[] macroDefinitions,
	              final CommandLineParser commandLineParser,
	              final List<CComment> trailingComments)
	{
		this(
			null,
			internalAstRootNode,
			includeStatements,
			macroDefinitions,
			commandLineParser,
			null,
			trailingComments
		);
	}

	private Writer(final CASTTranslationUnit eclipseCdtAstRootNode,
	               final CBaseNode internalAstRootNode,
	               final IASTPreprocessorIncludeStatement[] includeStatements,
	               final IASTPreprocessorMacroDefinition[] macroDefinitions,
	               final CommandLineParser commandLineParser,
	               final CommentsHandler commentsHandler,
				   final List<CComment> trailingComments)
	{
		this.eclipseCdtAstRootNode = eclipseCdtAstRootNode;
		this.internalAstRootNode = internalAstRootNode;
		this.includeDirectives = includeStatements;
		this.macroDefinitions = macroDefinitions;
		this.commandLineParser = commandLineParser;
		this.commentsHandler = commentsHandler;
		this.trailingComments = trailingComments;
		this.lineAlreadyJumped = false;
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
		//Then, we write the eventual include directives
		if (this.includeDirectives.length != 0)
		{
			if (ADD_INSIGHTFUL_COMMENTS)
			{
				printWriter.println("//Include directives");
				printWriter.println();
			}

			for (final IASTPreprocessorIncludeStatement includeDirective : this.includeDirectives)
			{
				this.dumpIncludeDirective(printWriter, includeDirective);
			}

			printWriter.println();
		}

		//Then, we write the eventual macro definitions
		if (this.macroDefinitions.length != 0)
		{
			if (ADD_INSIGHTFUL_COMMENTS)
			{
				printWriter.println("//Macro definitions");
				printWriter.println();
			}

			for (final IASTPreprocessorMacroDefinition macroDefinition : this.macroDefinitions)
			{
				this.dumpMacroDefinition(printWriter, macroDefinition);
			}

			printWriter.println();
		}

		if (GENERATE_FROM_ECLIPSE_CDT_TREE)
		{
			//First, we write the eventual leading comments of the program
			this.dumpLeadingComments(printWriter);

			//Then, we write the program itself
			boolean first = true;

			for (final IASTNode iastNode : this.eclipseCdtAstRootNode.getChildren())
			{
				if (!first)
				{
					printWriter.println();
				}

				if (iastNode instanceof CASTSimpleDeclaration)
				{
					this.dumpSimpleDeclaration(printWriter, (CASTSimpleDeclaration) iastNode, 0);
					printWriter.print(Char.CLOSING_BRACKET);
					printWriter.print(Char.SEMI_COLON);

					final List<CComment> commentsAppearingAfterDefinition = this.commentsHandler.getCommentsFollowingNodes().get(iastNode);

					if (commentsAppearingAfterDefinition != null)
					{
						commentsAppearingAfterDefinition.sort(Comparator.comparingInt(CComment::getOriginalSourceCodeStartingLine));

						if (commentsAppearingAfterDefinition.get(0).getOriginalSourceCodeStartingLine() != iastNode.getFileLocation().getEndingLineNumber())
						{
							printWriter.println();
						}

						for (final CComment comment : commentsAppearingAfterDefinition)
						{
							printWriter.println();
							printWriter.print(comment.getContent());
						}
					}
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

				if (this.commentsHandler.getCommentsFollowingNodes().get(iastNode) == null)
				{
					printWriter.println();
				}
				first = false;
			}
		}
		else
		{
			/*
				Then we sort the program's main elements and write them in the following order:
					- Function declarations
					- Global variables
					- Function definitions
			 */
			final ArrayList<SimpleDeclarationNode> functionDeclarations = new ArrayList<>();
			final ArrayList<SimpleDeclarationNode> globalVariables = new ArrayList<>();
			final ArrayList<FunctionDefinitionNode> functionDefinitions = new ArrayList<>();

			for (final AbstractSyntaxNode rootChild : this.internalAstRootNode.getChildren())
			{
				if (rootChild instanceof FunctionDefinitionNode)
				{
					functionDefinitions.add((FunctionDefinitionNode) rootChild);
				}
				else if (rootChild instanceof SimpleDeclarationNode)
				{
					if (rootChild.hasSuccessorOfType(FunctionDeclaratorNode.class))
					{
						//This is a function declaration
						functionDeclarations.add((SimpleDeclarationNode) rootChild);
					}
					else
					{
						//This is a global variable
						globalVariables.add((SimpleDeclarationNode) rootChild);
					}
				}
				else
				{
					throw new UnhandledElementException(String.format(
							"Node type \"%s\" is not yet supported as root element of the program!",
							rootChild.toString()
					));
				}
			}

			//Write the function declarations
			if (!functionDeclarations.isEmpty())
			{
				if (ADD_INSIGHTFUL_COMMENTS)
				{
					printWriter.println("//Function declarations");
					printWriter.println();
				}

				for (final SimpleDeclarationNode functionDeclaration : functionDeclarations)
				{
					this.dumpSimpleDeclaration(printWriter, functionDeclaration, 0);
					printWriter.println(Char.SEMI_COLON);
				}

				printWriter.println();
			}

			//Write the global variables
			if (!globalVariables.isEmpty())
			{
				if (ADD_INSIGHTFUL_COMMENTS)
				{
					printWriter.println("//Global variables");
					printWriter.println();
				}

				for (final SimpleDeclarationNode globalVariable : globalVariables)
				{
					this.dumpSimpleDeclaration(printWriter, globalVariable, 0);
					printWriter.println(Char.SEMI_COLON);
				}

				printWriter.println();
			}

			//Write the function definitions
			if (!functionDefinitions.isEmpty())
			{
				if (ADD_INSIGHTFUL_COMMENTS)
				{
					printWriter.println("//Function definitions");
					printWriter.println();
				}

				boolean first = true;

				for (final FunctionDefinitionNode functionDefinitionNode : functionDefinitions)
				{
					if (!first)
					{
						printWriter.println();
					}

					this.dumpFunctionDefinition(printWriter, functionDefinitionNode, 0);
					first = false;
				}
			}

			//Write the trailing comments if any
			this.trailingComments.sort(Comparator.comparingInt(CComment::getOriginalSourceCodeStartingLine));

			for (final CComment trailingComment : this.trailingComments)
			{
				printWriter.println();
				printWriter.println(trailingComment.getContent());
			}
		}
	}

	private void dumpIncludeDirective(final PrintWriter printWriter,
									  final IASTPreprocessorIncludeStatement includeDirective)
	{
		printWriter.println(includeDirective);
	}

	private void dumpMacroDefinition(final PrintWriter printWriter,
									 final IASTPreprocessorMacroDefinition macroDefinition)
	{
		printWriter.print(CKeyword.MACRO_DEFINITION);
		printWriter.print(Char.SPACE);
		printWriter.print(macroDefinition.getName().toString());
		printWriter.print(Char.SPACE);
		printWriter.println(macroDefinition.getExpansion());
	}

	/**
	 * This method dumps the eventual leading comments of the C program given as input.
	 * Their computation is handled by the CommentsHandler.
	 * Note that in the current implementation of this function, the original disposition of these comments is not
	 * supported, except for the order of appearance (e.g., line numbers can be different).
	 *
	 * @param printWriter the print writer used to write the program.
	 */
	private void dumpLeadingComments(final PrintWriter printWriter)
	{
		final List<CComment> leadingComments = this.commentsHandler.getLeadingComments();
		leadingComments.sort(Comparator.comparingInt(CComment::getOriginalSourceCodeStartingLine));

		for (final CComment comment : leadingComments)
		{
			printWriter.println(comment.getContent());
		}

		if (!leadingComments.isEmpty())
		{
			printWriter.println();
		}
	}

	private void dumpSimpleDeclaration(final PrintWriter printWriter,
									   final SimpleDeclarationNode simpleDeclaration,
									   final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, simpleDeclaration, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		String separator = String.valueOf(Char.SPACE);

		for (final AbstractSyntaxNode child : simpleDeclaration.getChildren())
		{
			if (child instanceof SimpleDeclSpecifierNode)
			{
				this.dumpSimpleDeclarationSpecifier(printWriter, (SimpleDeclSpecifierNode) child, 0);
			}
			else if (child instanceof TypedefNameSpecifierNode)
			{
				this.dumpTypedefNameSpecifier(printWriter, (TypedefNameSpecifierNode) child, 0);
			}
			else if (child instanceof ElaboratedTypeSpecifierNode)
			{
				this.dumpElaboratedTypeSpecifier(printWriter, (ElaboratedTypeSpecifierNode) child, 0);
			}
			else if (child instanceof FunctionDeclaratorNode)
			{
				printWriter.print(Char.SPACE);
				this.dumpFunctionDeclarator(printWriter, (FunctionDeclaratorNode) child, 0);
			}
			else if (child instanceof DeclaratorNode)
			{
				printWriter.print(separator);
				this.dumpDeclarator(printWriter, (DeclaratorNode) child, 0);
				separator = Str.COMA_AND_SPACE;
			}
			else if (child instanceof CompositeTypeSpecifierNode)
			{
				this.dumpCompositeTypeSpecifier(printWriter, (CompositeTypeSpecifierNode) child, nbTabs);
			}
			else if (child instanceof ArrayDeclaratorNode)
			{
				printWriter.print(separator);
				this.dumpArrayDeclarator(printWriter, (ArrayDeclaratorNode) child, nbTabs);
				separator = Str.COMA_AND_SPACE;
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a SimpleDeclarationNode!",
					child.toString()
				));
			}
		}

		//printWriter.println(Char.SEMI_COLON);
	}

	private void dumpCompositeTypeSpecifier(final PrintWriter printWriter,
											final CompositeTypeSpecifierNode compositeTypeSpecifier,
											final int nbTabs) throws UnhandledElementException
	{
		//TODO Personal taste: I add a line break before the definition for readability
		printWriter.println();

		this.dumpPrecedingCommentsIfAny(printWriter, compositeTypeSpecifier, nbTabs);

		if (compositeTypeSpecifier.getStorageClass() != CStorageClass.UNSPECIFIED)
		{
			printWriter.print(compositeTypeSpecifier.getStorageClass().toString());
			printWriter.print(Char.SPACE);
		}

		//TODO Check if composite type specifier is necessarily struct or not
		printWriter.print(CKeyword.STRUCT);
		printWriter.print(Char.SPACE);

		//Get struct name
		final NameNode nameNode = (NameNode) compositeTypeSpecifier.removeFirstChildAndForceParent();
		this.dumpName(printWriter, nameNode, 0);
		printWriter.print(Str.SPACE_AND_OPENING_CURVY_BRACKET);

		for (final AbstractSyntaxNode child : compositeTypeSpecifier.getChildren())
		{
			printWriter.println();

			if (child instanceof SimpleDeclarationNode)
			{
				this.dumpSimpleDeclaration(printWriter, (SimpleDeclarationNode) child, nbTabs + 1);
				printWriter.print(Char.SEMI_COLON);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a CompositeTypeSpecifier!",
					child.toString()
				));
			}
		}

		printWriter.println();
		printWriter.print(Char.CLOSING_CURVY_BRACKET);
		//printWriter.print(Char.SEMI_COLON); TODO this needs a bit more fine-grained analysis for function-nested structs
	}

	private void dumpSimpleDeclarationSpecifier(final PrintWriter printWriter,
												final SimpleDeclSpecifierNode simpleDeclarationSpecifier,
												final int nbTabs)
	{
		this.dumpPrecedingCommentsIfAny(printWriter, simpleDeclarationSpecifier, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		//Write first the storage class if any (extern, static, auto, register, ...)
		if (simpleDeclarationSpecifier.getStorageClass() != CStorageClass.UNSPECIFIED)
		{
			printWriter.print(simpleDeclarationSpecifier.getStorageClass().toString());
			printWriter.print(Char.SPACE);
		}

		//Then write the type qualifiers if any (const, volatile, restrict, _Atomic, ...)
		if (!simpleDeclarationSpecifier.getTypeQualifiers().isEmpty())
		{
			simpleDeclarationSpecifier.sortQualifiers();

			for (final CTypeQualifier typeQualifier : simpleDeclarationSpecifier.getTypeQualifiers())
			{
				printWriter.print(typeQualifier.toString());
				printWriter.print(Char.SPACE);
			}
		}

		//And finally write the type specifier (int, char, _Bool, ...)
		printWriter.print(simpleDeclarationSpecifier.getType().toString());
	}

	private void dumpTypedefNameSpecifier(final PrintWriter printWriter,
										  final TypedefNameSpecifierNode typedefNameSpecifier,
										  final int nbTabs)
	{
		this.dumpPrecedingCommentsIfAny(printWriter, typedefNameSpecifier, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		//Write first the storage class if any (extern, static, auto, register, ...)
		if (typedefNameSpecifier.getStorageClass() != CStorageClass.UNSPECIFIED)
		{
			printWriter.print(typedefNameSpecifier.getStorageClass().toString());
			printWriter.print(Char.SPACE);
		}

		//Then write the type qualifiers if any (const, volatile, restrict, _Atomic, ...)
		if (!typedefNameSpecifier.getTypeQualifiers().isEmpty())
		{
			typedefNameSpecifier.sortQualifiers();

			for (final CTypeQualifier typeQualifier : typedefNameSpecifier.getTypeQualifiers())
			{
				printWriter.print(typeQualifier.toString());
				printWriter.print(Char.SPACE);
			}
		}

		//And finally write the type specifier (int, char, _Bool, ...)
		printWriter.print(typedefNameSpecifier.getTypeName());
	}

	private void dumpFunctionDeclarator(final PrintWriter printWriter,
										final FunctionDeclaratorNode functionDeclarator,
										final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, functionDeclarator, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		String separator = Str.EMPTY_STRING;

		for (final AbstractSyntaxNode child : functionDeclarator.getChildren())
		{
			if (child instanceof NameNode)
			{
				this.dumpName(printWriter, (NameNode) child, 0);
				printWriter.print(Char.OPENING_BRACKET);
			}
			else if (child instanceof ParameterDeclarationNode)
			{
				printWriter.print(separator);
				this.dumpParameterDeclaration(printWriter, (ParameterDeclarationNode) child);
				separator = Str.COMA_AND_SPACE;
			}
			else if (child instanceof PointerNode)
			{
				this.dumpPointer(printWriter, (PointerNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a FunctionDeclaratorNode!",
					child.toString()
				));
			}
		}

		printWriter.print(Char.CLOSING_BRACKET);
	}

	private void dumpName(final PrintWriter printWriter,
						  final NameNode name,
						  final int nbTabs)
	{
		this.dumpPrecedingCommentsIfAny(printWriter, name, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(name.getValue());
	}

	private void dumpPointer(final PrintWriter printWriter,
	                         final PointerNode pointer,
							 final int nbTabs)
	{
		this.dumpPrecedingCommentsIfAny(printWriter, pointer, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CUnaryOperator.INDIRECTION);
	}

	private void dumpParameterDeclaration(final PrintWriter printWriter,
										  final ParameterDeclarationNode parameterDeclaration) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, parameterDeclaration, 0);

		for (final AbstractSyntaxNode child : parameterDeclaration.getChildren())
		{
			if (child instanceof SimpleDeclSpecifierNode)
			{
				this.dumpSimpleDeclarationSpecifier(printWriter, (SimpleDeclSpecifierNode) child, 0);
			}
			else if (child instanceof TypedefNameSpecifierNode)
			{
				this.dumpTypedefNameSpecifier(printWriter, (TypedefNameSpecifierNode) child, 0);
			}
			else if (child instanceof ElaboratedTypeSpecifierNode)
			{
				this.dumpElaboratedTypeSpecifier(printWriter, (ElaboratedTypeSpecifierNode) child, 0);
			}
			else if (child instanceof DeclaratorNode)
			{
				printWriter.print(Char.SPACE);
				this.dumpDeclarator(printWriter, (DeclaratorNode) child, 0);
			}
			else if (child instanceof ArrayDeclaratorNode)
			{
				printWriter.print(Char.SPACE);
				this.dumpArrayDeclarator(printWriter, (ArrayDeclaratorNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a ParameterDeclarationNode!",
					child.toString()
				));
			}
		}
	}

	private void dumpArrayDeclarator(final PrintWriter printWriter,
									 final ArrayDeclaratorNode arrayDeclarator,
									 final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, arrayDeclarator, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		for (final AbstractSyntaxNode child : arrayDeclarator.getChildren())
		{
			if (child instanceof PointerNode)
			{
				this.dumpPointer(printWriter, (PointerNode) child, 0);
			}
			else if (child instanceof NameNode)
			{
				this.dumpName(printWriter, (NameNode) child, 0);
			}
			else if (child instanceof ArrayModifierNode)
			{
				this.dumpArrayModifier(printWriter, (ArrayModifierNode) child);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of an ArrayDeclaratorNode!",
					child.toString()
				));
			}
		}
	}

	private void dumpArrayModifier(final PrintWriter printWriter,
								   final ArrayModifierNode arrayModifier) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, arrayModifier, 0);
		printWriter.print(Char.OPENING_SQUARE_BRACKET);

		if (arrayModifier.hasChildren())
		{
			final AbstractSyntaxNode child = arrayModifier.getFirstChild();

			if (child instanceof BinaryExpressionNode)
			{
				this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of an ArrayModifierNode!",
					child.toString()
				));
			}
		}

		printWriter.print(Char.CLOSING_SQUARE_BRACKET);
	}

	private void dumpDeclarator(final PrintWriter printWriter,
								final DeclaratorNode declarator,
								final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, declarator, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		for (final AbstractSyntaxNode child : declarator.getChildren())
		{
			if (child instanceof PointerNode)
			{
				this.dumpPointer(printWriter, (PointerNode) child, 0);
			}
			else if (child instanceof NameNode)
			{
				this.dumpName(printWriter, (NameNode) child, 0);
			}
			else if (child instanceof EqualsInitializerNode)
			{
				this.dumpEqualsInitializer(printWriter, (EqualsInitializerNode) child);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a DeclaratorNode!",
					child.toString()
				));
			}
		}
	}

	private void dumpEqualsInitializer(final PrintWriter printWriter,
									   final EqualsInitializerNode equalsInitializer) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, equalsInitializer, 0);

		printWriter.print(Char.SPACE);
		printWriter.print(CBinaryOperator.ASSIGNMENT);
		printWriter.print(Char.SPACE);

		final AbstractSyntaxNode child = equalsInitializer.getFirstChild();

		if (child instanceof BinaryExpressionNode)
		{
			this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) child, 0);
		}
		else if (child instanceof LiteralExpressionNode)
		{
			this.dumpLiteralExpression(printWriter, (LiteralExpressionNode) child, 0);
		}
		else if (child instanceof FunctionCallExpressionNode)
		{
			this.dumpFunctionCallExpression(printWriter, (FunctionCallExpressionNode) child, 0);
		}
		else if (child instanceof CastExpressionNode)
		{
			this.dumpCastExpression(printWriter, (CastExpressionNode) child, 0);
		}
		else if (child instanceof UnaryExpressionNode)
		{
			this.dumpUnaryExpression(printWriter, (UnaryExpressionNode) child, 0);
		}
		else if (child instanceof IdExpressionNode)
		{
			this.dumpIdExpression(printWriter, (IdExpressionNode) child, 0);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as child of an EqualsInitializerNode!",
				child.toString()
			));
		}
	}

	private void dumpFunctionDefinition(final PrintWriter printWriter,
										final FunctionDefinitionNode functionDefinition,
										final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, functionDefinition, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		for (final AbstractSyntaxNode child : functionDefinition.getChildren())
		{
			if (child instanceof SimpleDeclSpecifierNode)
			{
				this.dumpSimpleDeclarationSpecifier(printWriter, (SimpleDeclSpecifierNode) child, 0);
			}
			else if (child instanceof TypedefNameSpecifierNode)
			{
				this.dumpTypedefNameSpecifier(printWriter, (TypedefNameSpecifierNode) child, 0);
			}
			else if (child instanceof ElaboratedTypeSpecifierNode)
			{
				this.dumpElaboratedTypeSpecifier(printWriter, (ElaboratedTypeSpecifierNode) child, 0);
			}
			else if (child instanceof FunctionDeclaratorNode)
			{
				printWriter.print(Char.SPACE);
				this.dumpFunctionDeclarator(printWriter, (FunctionDeclaratorNode) child, 0);
			}
			else if (child instanceof CompoundStatementNode)
			{
				printWriter.print(Char.SPACE);
				printWriter.println(Char.OPENING_CURVY_BRACKET);
				this.dumpCompoundStatement(printWriter, (CompoundStatementNode) child, nbTabs + 1);
				printWriter.println();
				printWriter.println(Char.CLOSING_CURVY_BRACKET);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a FunctionDefinitionNode!",
					child.toString()
				));
			}
		}
	}

	private void dumpElaboratedTypeSpecifier(final PrintWriter printWriter,
											 final ElaboratedTypeSpecifierNode elaboratedTypeSpecifier,
											 final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, elaboratedTypeSpecifier, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(elaboratedTypeSpecifier.getTypeSpecifier().toString());
		printWriter.print(Char.SPACE);

		final AbstractSyntaxNode child = elaboratedTypeSpecifier.getFirstChild();

		if (child instanceof NameNode)
		{
			this.dumpName(printWriter, (NameNode) child, 0);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as child of an ElaboratedTypeSpecifierNode!",
				child.toString()
			));
		}
	}

	private void dumpCompoundStatement(final PrintWriter printWriter,
									   final CompoundStatementNode compoundStatement,
									   final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, compoundStatement, nbTabs);

		boolean first = true;

		for (final AbstractSyntaxNode child : compoundStatement.getChildren())
		{
			if (!first)
			{
				printWriter.println();
			}

			if (child instanceof ExpressionStatementNode)
			{
				this.dumpExpressionStatement(printWriter, (ExpressionStatementNode) child, nbTabs);
				printWriter.print(Char.SEMI_COLON);
			}
			else if (child instanceof DeclarationStatementNode)
			{
				this.dumpDeclarationStatement(printWriter, (DeclarationStatementNode) child, nbTabs);
				printWriter.print(Char.SEMI_COLON);
			}
			else if (child instanceof ForStatementNode)
			{
				this.dumpForStatement(printWriter, (ForStatementNode) child, nbTabs);
			}
			else if (child instanceof IfStatementNode)
			{
				this.dumpIfStatement(printWriter, (IfStatementNode) child, nbTabs);
			}
			else if (child instanceof ReturnStatementNode)
			{
				this.dumpReturnStatement(printWriter, (ReturnStatementNode) child, nbTabs, true);
			}
			else if (child instanceof LabelStatementNode)
			{
				this.dumpLabelStatement(printWriter, (LabelStatementNode) child, nbTabs - 1);
			}
			else if (child instanceof GotoStatementNode)
			{
				this.dumpGotoStatement(printWriter, (GotoStatementNode) child, nbTabs);
			}
			else if (child instanceof WhileStatementNode)
			{
				this.dumpWhileStatement(printWriter, (WhileStatementNode) child, nbTabs);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a CompoundStatementNode!",
					child.toString()
				));
			}

			first = false;
		}
	}

	private void dumpWhileStatement(final PrintWriter printWriter,
									final WhileStatementNode whileStatement,
									final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, whileStatement, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.WHILE);
		printWriter.print(Str.SPACE_AND_OPENING_BRACKET);

		//Manage "while" condition
		final AbstractSyntaxNode whileCondition = whileStatement.removeFirstChildAndForceParent();

		if (whileCondition instanceof BinaryExpressionNode)
		{
			this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) whileCondition, 0);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as condition of a WhileStatementNode!",
				whileCondition.toString()
			));
		}

		printWriter.print(Char.CLOSING_BRACKET);
		printWriter.println(Str.SPACE_AND_OPENING_CURVY_BRACKET);

		//Manage "while" body
		final AbstractSyntaxNode whileBody = whileStatement.getFirstChild();

		if (whileBody instanceof CompoundStatementNode)
		{
			this.dumpCompoundStatement(printWriter, (CompoundStatementNode) whileBody, nbTabs + 1);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as body of a WhileStatementNode!",
				whileBody.toString()
			));
		}

		printWriter.println();
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(Char.CLOSING_CURVY_BRACKET);
	}

	private void dumpGotoStatement(final PrintWriter printWriter,
								   final GotoStatementNode gotoStatement,
								   final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, gotoStatement, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.GO_TO);
		printWriter.print(Char.SPACE);

		final AbstractSyntaxNode child = gotoStatement.getFirstChild();

		if (child instanceof NameNode)
		{
			this.dumpName(printWriter, (NameNode) child, 0);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as child of a GotoStatementNode!",
				child.toString()
			));
		}

		printWriter.print(Char.SEMI_COLON);
	}

	private void dumpLabelStatement(final PrintWriter printWriter,
									final LabelStatementNode labelStatement,
									final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, labelStatement, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		//Manage label name
		final AbstractSyntaxNode nameNode = labelStatement.removeFirstChildAndForceParent();

		if (nameNode instanceof NameNode)
		{
			this.dumpName(printWriter, (NameNode) nameNode, 0);
			printWriter.print(Char.COLON);
			printWriter.print(Str.SPACE_AND_OPENING_CURVY_BRACKET);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as first child of a LabelStatementNode!",
				nameNode.toString()
			));
		}

		//Manage next instructions
		for (final AbstractSyntaxNode child : labelStatement.getChildren())
		{
			printWriter.println();

			if (child instanceof ExpressionStatementNode)
			{
				this.dumpExpressionStatement(printWriter, (ExpressionStatementNode) child, nbTabs + 1);
			}
			else if (child instanceof CompoundStatementNode)
			{
				this.dumpCompoundStatement(printWriter, (CompoundStatementNode) child, nbTabs + 1);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a LabelStatementNode!",
					child.toString()
				));
			}
		}

		printWriter.println();
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(Char.CLOSING_CURVY_BRACKET);
	}

	private void dumpReturnStatement(final PrintWriter printWriter,
									 final ReturnStatementNode returnStatement,
									 final int nbTabs,
									 final boolean jumpLine) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, returnStatement, nbTabs);

		if (jumpLine)
		{
			printWriter.println();
		}

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.RETURN);

		if (returnStatement.hasChildren())
		{
			printWriter.print(Char.SPACE);

			final AbstractSyntaxNode child = returnStatement.getFirstChild();

			if (child instanceof LiteralExpressionNode)
			{
				this.dumpLiteralExpression(printWriter, (LiteralExpressionNode) child, 0);
			}
			else if (child instanceof BinaryExpressionNode)
			{
				this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) child, 0);
			}
			else if (child instanceof IdExpressionNode)
			{
				this.dumpIdExpression(printWriter, (IdExpressionNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a ReturnStatement!",
					child.toString()
				));
			}
		}

		printWriter.print(Char.SEMI_COLON);
	}

	private void dumpExpressionStatement(final PrintWriter printWriter,
										 final ExpressionStatementNode expressionStatement,
										 final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, expressionStatement, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		for (final AbstractSyntaxNode child : expressionStatement.getChildren())
		{
			if (child instanceof BinaryExpressionNode)
			{
				this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) child, 0);
			}
			else if (child instanceof FunctionCallExpressionNode)
			{
				this.dumpFunctionCallExpression(printWriter, (FunctionCallExpressionNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of an ExpressionStatementNode!",
					child.toString()
				));
			}
		}

		//printWriter.print(Char.SEMI_COLON);
	}

	/**
	 * In the AST generated by Eclipse-CDT, a for statement is represented by a node having between 2 and 4 children.
	 * The last child is always representing the body of the for loop.
	 * The first children represent the three statements of the loop header.
	 * If a statement is null, and is followed by another non-null statement, the for statement will have a
	 * NullStatementNode child.
	 * Otherwise, it will have no child, except if all the statements are null in which case the first child of the for
	 * statement will be a NullStatementNode too.
	 * Examples:
	 * <p>
	 * - for (int i = 0; i < 10; i++) {<stm>}
	 * 	 will be represented as a ForStatementNode with 4 children, one representing "int i = 0", one representing
	 * 	 "i < 10", one representing "i++", and one representing "<stm>".
	 * <p>
	 * - for (int i = 0; i < 10; ) {<stm>}
	 * 	 will be represented as a ForStatementNode with 3 children, one representing "int i = 0", one representing
	 * 	 "i < 10", and one representing "<stm>".
	 * <p>
	 * - for (; i < 10; ) {<stm>}
	 * 	 will be represented as a ForStatementNode with 3 children, a NullStatementNode representing the absence of
	 * 	 information for the first statement, one representing "i < 10", and one representing "<stm>".
	 * <p>
	 * - for (; ;) {<stm>}
	 * 	 will be represented as a ForStatementNode with 2 children, a NullStatementNode representing the absence of
	 * 	 information for the statements, and one representing "<stm>".
	 *
	 * @param printWriter the writer used to dump the C program
	 * @param forStatement the for statement to dump
	 */
	private void dumpForStatement(final PrintWriter printWriter,
								  final ForStatementNode forStatement,
								  final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, forStatement, nbTabs);

		printWriter.println();
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.FOR);
		printWriter.print(Char.SPACE);
		printWriter.print(Char.OPENING_BRACKET);

		//Remove the body of the for loop to manage it later
		final AbstractSyntaxNode forBody = forStatement.removeLastChildAndForceParent();

		//Manage the header of the for loop
		int nbChildrenManaged = 0;
		String separator = Str.EMPTY_STRING;

		for (final AbstractSyntaxNode child : forStatement.getChildren())
		{
			printWriter.print(separator);
			separator = Str.SEMI_COLON_AND_SPACE;

			if (child instanceof NullStatementNode)
			{
				//We don't do anything
				if (nbChildrenManaged == 2)
				{
					throw new RuntimeException("Third children of a for statement should never be null!");
				}
			}
			else if (child instanceof ExpressionStatementNode)
			{
				this.dumpExpressionStatement(printWriter, (ExpressionStatementNode) child, 0);
			}
			else if (child instanceof BinaryExpressionNode)
			{
				this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) child, 0);
			}
			else if (child instanceof UnaryExpressionNode)
			{
				this.dumpUnaryExpression(printWriter, (UnaryExpressionNode) child, 0);
			}
			else if (child instanceof DeclarationStatementNode)
			{
				this.dumpDeclarationStatement(printWriter, (DeclarationStatementNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled in the header of a ForStatementNode!",
					child.toString()
				));
			}

			nbChildrenManaged++;
		}

		if (nbChildrenManaged == 0)
		{
			throw new RuntimeException("There should be at least one header child!");
		}
		else if (nbChildrenManaged == 1)
		{
			//Add the two missing semicolons
			printWriter.print(Str.SEMI_COLON_AND_SPACE);
			printWriter.print(Char.SEMI_COLON);
		}
		else if (nbChildrenManaged == 2)
		{
			//Add the missing semicolon
			printWriter.print(Char.SEMI_COLON);
		}

		printWriter.print(Char.CLOSING_BRACKET);
		printWriter.print(Char.SPACE);
		printWriter.println(Char.OPENING_CURVY_BRACKET);

		//Dump the body
		if (forBody instanceof ExpressionStatementNode)
		{
			this.dumpExpressionStatement(printWriter, (ExpressionStatementNode) forBody, nbTabs + 1);
			printWriter.print(Char.SEMI_COLON);
		}
		else if (forBody instanceof CompoundStatementNode)
		{
			this.dumpCompoundStatement(printWriter, (CompoundStatementNode) forBody, nbTabs + 1);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as body of a ForStatementNode!",
				forBody.toString()
			));
		}

		//Close the for
		printWriter.println();
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(Char.CLOSING_CURVY_BRACKET);
		printWriter.println();
	}

	private void dumpIfStatement(final PrintWriter printWriter,
								 final IfStatementNode ifStatement,
								 final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, ifStatement, nbTabs);

		printWriter.println();
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.IF);
		printWriter.print(Str.SPACE_AND_OPENING_BRACKET);

		//Retrieve "if" condition and manage it
		final AbstractSyntaxNode ifCondition = ifStatement.removeFirstChildAndForceParent();

		if (ifCondition instanceof ArraySubscriptExpressionNode)
		{
			this.dumpArraySubscriptExpressionNode(printWriter, (ArraySubscriptExpressionNode) ifCondition, 0);
		}
		else if (ifCondition instanceof BinaryExpressionNode)
		{
			this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) ifCondition, 0);
		}
		else if (ifCondition instanceof UnaryExpressionNode)
		{
			this.dumpUnaryExpression(printWriter, (UnaryExpressionNode) ifCondition, 0);
		}
		else if (ifCondition instanceof FunctionCallExpressionNode)
		{
			this.dumpFunctionCallExpression(printWriter, (FunctionCallExpressionNode) ifCondition, 0);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as condition of an IfStatementNode!",
				ifCondition.toString()
			));
		}

		printWriter.print(Char.CLOSING_BRACKET);
		printWriter.println(Str.SPACE_AND_OPENING_CURVY_BRACKET);

		//Retrieve "if" body and manage it
		final AbstractSyntaxNode ifBody = ifStatement.removeFirstChildAndForceParent();

		if (ifBody instanceof CompoundStatementNode)
		{
			this.dumpCompoundStatement(printWriter, (CompoundStatementNode) ifBody, nbTabs + 1);
		}
		else if (ifBody instanceof ExpressionStatementNode)
		{
			this.dumpExpressionStatement(printWriter, (ExpressionStatementNode) ifBody, nbTabs + 1);
		}
		else if (ifBody instanceof ReturnStatementNode)
		{
			this.dumpReturnStatement(printWriter, (ReturnStatementNode) ifBody, nbTabs + 1, false);
		}
		else if (ifBody instanceof GotoStatementNode)
		{
			this.dumpGotoStatement(printWriter, (GotoStatementNode) ifBody, nbTabs + 1);
		}
		else if (ifBody instanceof LabelStatementNode)
		{
			this.dumpLabelStatement(printWriter, (LabelStatementNode) ifBody, nbTabs + 1);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as body of an IfStatementNode!",
				ifBody.toString()
			));
		}

		printWriter.println();
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(Char.CLOSING_CURVY_BRACKET);

		//Manage eventual "else if" or "else"
		for (final AbstractSyntaxNode child : ifStatement.getChildren())
		{
			if (child instanceof CompoundStatementNode)
			{
				//This is an "else"
				printWriter.print(Char.SPACE);
				printWriter.print(CKeyword.ELSE);
				printWriter.println(Str.SPACE_AND_OPENING_CURVY_BRACKET);

				this.dumpCompoundStatement(printWriter, (CompoundStatementNode) child, nbTabs + 1);

				printWriter.println();
				printWriter.print(Utils.addLeadingTabulations(nbTabs));
				printWriter.print(Char.CLOSING_CURVY_BRACKET);
			}
			else if (child instanceof ExpressionStatementNode)
			{
				//This is an "else" too...
				printWriter.print(Char.SPACE);
				printWriter.print(CKeyword.ELSE);
				printWriter.println(Str.SPACE_AND_OPENING_CURVY_BRACKET);

				this.dumpExpressionStatement(printWriter, (ExpressionStatementNode) child, nbTabs + 1);

				printWriter.println();
				printWriter.print(Utils.addLeadingTabulations(nbTabs));
				printWriter.print(Char.CLOSING_CURVY_BRACKET);
			}
			else if (child instanceof IfStatementNode)
			{
				//This is an "else if"
				printWriter.print(Char.SPACE);
				printWriter.print(CKeyword.ELSE_IF);
				printWriter.println(Str.SPACE_AND_OPENING_CURVY_BRACKET);

				this.dumpIfStatement(printWriter, (IfStatementNode) child, nbTabs + 1);

				printWriter.println();
				printWriter.print(Utils.addLeadingTabulations(nbTabs));
				printWriter.print(Char.CLOSING_CURVY_BRACKET);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as \"else if\" or \"else\" block of an IfStatementNode!",
					child.toString()
				));
			}
		}

		printWriter.println();
		//printWriter.println();
	}

	private void dumpDeclarationStatement(final PrintWriter printWriter,
										  final DeclarationStatementNode declarationStatement,
										  final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, declarationStatement, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		for (final AbstractSyntaxNode child : declarationStatement.getChildren())
		{
			if (child instanceof SimpleDeclarationNode)
			{
				this.dumpSimpleDeclaration(printWriter, (SimpleDeclarationNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a DeclarationStatementNode!",
					child.toString()
				));
			}
		}
	}

	private void dumpBinaryExpression(final PrintWriter printWriter,
									  final BinaryExpressionNode binaryExpression,
									  final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, binaryExpression, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		boolean leftPartHandled = false;

		for (final AbstractSyntaxNode child : binaryExpression.getChildren())
		{
			if (leftPartHandled)
			{
				//We have dumped the left handside of the expression, add the operator and the right handside
				printWriter.print(Char.SPACE);
				printWriter.print(binaryExpression.getBinaryOperator().toString());
				printWriter.print(Char.SPACE);
			}

			if (child instanceof IdExpressionNode)
			{
				this.dumpIdExpression(printWriter, (IdExpressionNode) child, 0);
			}
			else if (child instanceof LiteralExpressionNode)
			{
				this.dumpLiteralExpression(printWriter, (LiteralExpressionNode) child, 0);
			}
			else if (child instanceof ArraySubscriptExpressionNode)
			{
				this.dumpArraySubscriptExpressionNode(printWriter, (ArraySubscriptExpressionNode) child, 0);
			}
			else if (child instanceof UnaryExpressionNode)
			{
				this.dumpUnaryExpression(printWriter, (UnaryExpressionNode) child, 0);
			}
			else if (child instanceof FunctionCallExpressionNode)
			{
				this.dumpFunctionCallExpression(printWriter, (FunctionCallExpressionNode) child, 0);
			}
			else if (child instanceof BinaryExpressionNode)
			{
				this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) child, 0);
			}
			else if (child instanceof FieldReferenceNode)
			{
				this.dumpFieldReference(printWriter, (FieldReferenceNode) child, 0);
			}
			else if (child instanceof TypeIdExpressionNode)
			{
				this.dumpTypeIdExpressionNode(printWriter, (TypeIdExpressionNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a BinaryExpressionNode!",
					child.toString()
				));
			}

			leftPartHandled = true;
		}
	}

	private void dumpFieldReference(final PrintWriter printWriter,
									final FieldReferenceNode fieldReference,
									final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, fieldReference, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		boolean leftPartHandled = false;

		for (final AbstractSyntaxNode child : fieldReference.getChildren())
		{
			if (leftPartHandled)
			{
				printWriter.print(CBinaryOperator.ARROW_FIELD_ACCESS);
			}

			if (child instanceof IdExpressionNode)
			{
				this.dumpIdExpression(printWriter, (IdExpressionNode) child, 0);
			}
			else if (child instanceof NameNode)
			{
				this.dumpName(printWriter, (NameNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a FieldReferenceNode!",
					child.toString()
				));
			}

			leftPartHandled = true;
		}
	}

	private void dumpUnaryExpression(final PrintWriter printWriter,
									 final UnaryExpressionNode unaryExpression,
									 final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, unaryExpression, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		if (unaryExpression.getOperator() == CUnaryOperator.BRACKETS)
		{
			printWriter.print(Char.OPENING_BRACKET);
		}
		else if (unaryExpression.getOperator().isPrefixOperator())
		{
			printWriter.print(unaryExpression.getOperator().toString());
		}

		final AbstractSyntaxNode unaryExpressionChild = unaryExpression.getFirstChild();

		if (unaryExpressionChild instanceof UnaryExpressionNode)
		{
			this.dumpUnaryExpression(printWriter, (UnaryExpressionNode) unaryExpressionChild, 0);
		}
		else if (unaryExpressionChild instanceof LiteralExpressionNode)
		{
			this.dumpLiteralExpression(printWriter, (LiteralExpressionNode) unaryExpressionChild, 0);
		}
		else if (unaryExpressionChild instanceof BinaryExpressionNode)
		{
			this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) unaryExpressionChild, 0);
		}
		else if (unaryExpressionChild instanceof IdExpressionNode)
		{
			this.dumpIdExpression(printWriter, (IdExpressionNode) unaryExpressionChild, 0);
		}
		else if (unaryExpressionChild instanceof CastExpressionNode)
		{
			this.dumpCastExpression(printWriter, (CastExpressionNode) unaryExpressionChild, 0);
		}
		else
		{
			throw new UnhandledElementException(String.format(
				"Node type \"%s\" is not yet handled as child of a UnaryExpressionNode!",
				unaryExpressionChild.toString()
			));
		}

		if (unaryExpression.getOperator() == CUnaryOperator.BRACKETS)
		{
			printWriter.print(Char.CLOSING_BRACKET);
		}
		else if (unaryExpression.getOperator().isSuffixOperator())
		{
			printWriter.print(unaryExpression.getOperator().toString());
		}
	}

	private void dumpCastExpression(final PrintWriter printWriter,
									final CastExpressionNode castExpressionNode,
									final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, castExpressionNode, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(Char.OPENING_BRACKET);
		boolean parenthesisNotClosedYet = true;

		for (final AbstractSyntaxNode child : castExpressionNode.getChildren())
		{
			if (child instanceof TypeIdNode)
			{
				this.dumpTypeId(printWriter, (TypeIdNode) child, 0);
			}
			else if (child instanceof FunctionCallExpressionNode)
			{
				this.dumpFunctionCallExpression(printWriter, (FunctionCallExpressionNode) child, 0);
			}
			else if (child instanceof ArraySubscriptExpressionNode)
			{
				this.dumpArraySubscriptExpressionNode(printWriter, (ArraySubscriptExpressionNode) child, 0);
			}
			else if (child instanceof IdExpressionNode)
			{
				this.dumpIdExpression(printWriter, (IdExpressionNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a CastExpressionNode!",
					child.toString()
				));
			}

			if (parenthesisNotClosedYet)
			{
				parenthesisNotClosedYet = false;
				printWriter.print(Str.CLOSING_BRACKET_AND_SPACE);
			}
		}
	}

	private void dumpTypeId(final PrintWriter printWriter,
							final TypeIdNode typeId,
							final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, typeId, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		for (final AbstractSyntaxNode child : typeId.getChildren())
		{
			if (child instanceof TypedefNameSpecifierNode)
			{
				this.dumpTypedefNameSpecifier(printWriter, (TypedefNameSpecifierNode) child, 0);
			}
			else if (child instanceof DeclaratorNode)
			{
				this.dumpDeclarator(printWriter, (DeclaratorNode) child, 0);
			}
			else if (child instanceof ElaboratedTypeSpecifierNode)
			{
				this.dumpElaboratedTypeSpecifier(printWriter, (ElaboratedTypeSpecifierNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a TypeIdNode!",
					child.toString()
				));
			}
		}
	}

	private void dumpFunctionCallExpression(final PrintWriter printWriter,
	                                        final FunctionCallExpressionNode functionCallExpression,
	                                        final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, functionCallExpression, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		//Retrieve called function name node
		final IdExpressionNode functionNameNode = (IdExpressionNode) functionCallExpression.removeFirstChildAndForceParent();
		this.dumpIdExpression(printWriter, functionNameNode, 0);
		printWriter.print(Char.OPENING_BRACKET);

		//Manage function arguments
		String separator = Str.EMPTY_STRING;

		for (final AbstractSyntaxNode child : functionCallExpression.getChildren())
		{
			printWriter.print(separator);
			separator = Str.COMA_AND_SPACE;

			if (child instanceof IdExpressionNode)
			{
				this.dumpIdExpression(printWriter, (IdExpressionNode) child, 0);
			}
			else if (child instanceof ArraySubscriptExpressionNode)
			{
				this.dumpArraySubscriptExpressionNode(printWriter, (ArraySubscriptExpressionNode) child, 0);
			}
			else if (child instanceof LiteralExpressionNode)
			{
				this.dumpLiteralExpression(printWriter, (LiteralExpressionNode) child, 0);
			}
			else if (child instanceof BinaryExpressionNode)
			{
				this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) child, 0);
			}
			else if (child instanceof TypeIdExpressionNode)
			{
				this.dumpTypeIdExpressionNode(printWriter, (TypeIdExpressionNode) child, 0);
			}
			else if (child instanceof FieldReferenceNode)
			{
				this.dumpFieldReference(printWriter, (FieldReferenceNode) child, 0);
			}
			else if (child instanceof UnaryExpressionNode)
			{
				this.dumpUnaryExpression(printWriter, (UnaryExpressionNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a FunctionCall!",
					child.toString()
				));
			}
		}

		printWriter.print(Char.CLOSING_BRACKET);
	}

	private void dumpTypeIdExpressionNode(final PrintWriter printWriter,
										  final TypeIdExpressionNode typeIdExpression,
										  final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, typeIdExpression, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		for (final AbstractSyntaxNode child : typeIdExpression.getChildren())
		{
			if (child instanceof TypeIdNode)
			{
				this.dumpTypeId(printWriter, (TypeIdNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of a TypeIdExpressionNode!",
					child.toString()
				));
			}
		}
	}

	private void dumpArraySubscriptExpressionNode(final PrintWriter printWriter,
												  final ArraySubscriptExpressionNode arraySubscriptExpression,
												  final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, arraySubscriptExpression, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		boolean leftPartHandled = false;

		for (final AbstractSyntaxNode child : arraySubscriptExpression.getChildren())
		{
			if (leftPartHandled)
			{
				printWriter.print(Char.OPENING_SQUARE_BRACKET);
			}

			if (child instanceof IdExpressionNode)
			{
				this.dumpIdExpression(printWriter, (IdExpressionNode) child, 0);
			}
			else if (child instanceof UnaryExpressionNode)
			{
				this.dumpUnaryExpression(printWriter, (UnaryExpressionNode) child, 0);
			}
			else if (child instanceof FunctionCallExpressionNode)
			{
				this.dumpFunctionCallExpression(printWriter, (FunctionCallExpressionNode) child, 0);
			}
			else if (child instanceof LiteralExpressionNode)
			{
				this.dumpLiteralExpression(printWriter, (LiteralExpressionNode) child, 0);
			}
			else if (child instanceof BinaryExpressionNode)
			{
				this.dumpBinaryExpression(printWriter, (BinaryExpressionNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of an ArraySubscriptExpressionNode!",
					child.toString()
				));
			}

			leftPartHandled = true;
		}

		printWriter.print(Char.CLOSING_SQUARE_BRACKET);
	}

	private void dumpLiteralExpression(final PrintWriter printWriter,
									   final LiteralExpressionNode literalExpression,
									   final int nbTabs)
	{
		this.dumpPrecedingCommentsIfAny(printWriter, literalExpression, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(literalExpression.getValue());
	}

	private void dumpIdExpression(final PrintWriter printWriter,
								  final IdExpressionNode idExpressionNode,
								  final int nbTabs) throws UnhandledElementException
	{
		this.dumpPrecedingCommentsIfAny(printWriter, idExpressionNode, nbTabs);

		printWriter.print(Utils.addLeadingTabulations(nbTabs));

		for (final AbstractSyntaxNode child : idExpressionNode.getChildren())
		{
			if (child instanceof NameNode)
			{
				this.dumpName(printWriter, (NameNode) child, 0);
			}
			else
			{
				throw new UnhandledElementException(String.format(
					"Node type \"%s\" is not yet handled as child of an IdExpressionNode!",
					child.toString()
				));
			}
		}
	}

	/**
	 * This method dumps the comments preceding the given node to the file, adjusting the position of the comment with
	 * regards to the number of tabulations of the given file.
	 * Note that this method is too simplistic at the moment and thus may not place comments properly if they are
	 * originally located at tricky places (e.g., between "if" and "else if" blocks, etc.), or if they appear
	 * after the last instruction of a block structure (e.g., function, while, if, for, etc.).
	 *
	 * @param printWriter
	 * @param node
	 * @param nbTabs
	 */
	private void dumpPrecedingCommentsIfAny(final PrintWriter printWriter,
											final CBaseNode node,
											final int nbTabs)
	{
		if (!node.getPrecedingComments().isEmpty())
		{
			for (final CComment comment : node.sortAndGetPrecedingComments())
			{
				printWriter.print(Utils.addLeadingTabulations(nbTabs));
				printWriter.println(comment.getContent().trim());
			}
		}
	}

	//These methods were all for the Eclipse-CDT version of the tree, they are replaced by their internal version ones.

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
	@Deprecated
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
				printWriter.print(Char.CLOSING_BRACKET);
				printWriter.print(Char.SPACE);
				printWriter.println(Char.OPENING_CURVY_BRACKET);
			}
			else if (child instanceof CASTCompoundStatement)
			{
				this.dumpCompoundStatement(printWriter, (CASTCompoundStatement) child, 1);
			}
		}

		printWriter.print(Char.CLOSING_CURVY_BRACKET);

		final List<CComment> commentsAppearingAfterDefinition = this.commentsHandler.getCommentsFollowingNodes().get(definition);

		if (commentsAppearingAfterDefinition != null)
		{
			//There are some comments to be written
			commentsAppearingAfterDefinition.sort(Comparator.comparingInt(CComment::getOriginalSourceCodeStartingLine));

			if (commentsAppearingAfterDefinition.get(0).getOriginalSourceCodeStartingLine() != definition.getFileLocation().getEndingLineNumber())
			{
				printWriter.println();
			}

			for (final CComment comment : commentsAppearingAfterDefinition)
			{
				printWriter.println();
				printWriter.print(comment.getContent());
			}
		}
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
	@Deprecated
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
	@Deprecated
	private void dumpSimpleDeclarationSpecifier(final PrintWriter printWriter,
												final CASTSimpleDeclSpecifier declSpecifier,
												final int nbTabs,
												final boolean indent)
	{
		if (indent)
		{
			printWriter.print(Utils.addLeadingTabulations(nbTabs));
		}

		final CStorageClass declarationSpecifier = CStorageClass.convertEclipseCDTTypesToThis(declSpecifier.getStorageClass());
		printWriter.print(declarationSpecifier.getStorageClass());

		if (declarationSpecifier != CStorageClass.UNSPECIFIED)
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
	@Deprecated
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
				printWriter.print(Char.OPENING_BRACKET);
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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

		final List<CComment> commentsAfterStatement = this.commentsHandler.getCommentsFollowingNodes().get(compoundStatement);

		if (commentsAfterStatement != null)
		{
			commentsAfterStatement.sort(Comparator.comparingInt(CComment::getOriginalSourceCodeStartingLine));

			for (final CComment comment : commentsAfterStatement)
			{
				printWriter.print(Utils.addLeadingTabulations(nbTabs));
				printWriter.print(comment.getContent());
				printWriter.println();
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
	@Deprecated
	private void dumpWhileStatement(final PrintWriter printWriter,
									final CASTWhileStatement whileStatement,
									final int nbTabs) throws UnhandledElementException
	{
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.WHILE);
		printWriter.print(Char.SPACE);
		printWriter.print(Char.OPENING_BRACKET);

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
							"While object \"%s\" has multiple headers (CASTBinaryExpression/CASTIdExpression objects).",
							child
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

				printWriter.print(Char.CLOSING_BRACKET);
				printWriter.print(Char.SPACE);
				printWriter.println(Char.OPENING_CURVY_BRACKET);
			}
			else if (child instanceof CASTCompoundStatement)
			{
				//Body
				if (bodyManaged)
				{
					throw new UnhandledElementException(
						String.format(
							"While object \"%s\" has multiple bodies (CASTCompoundStatement objects).",
							child
						)
					);
				}

				bodyManaged = true;

				this.dumpCompoundStatement(printWriter, (CASTCompoundStatement) child, nbTabs + 1);
				printWriter.print(Utils.addLeadingTabulations(nbTabs));
				printWriter.print(Char.CLOSING_CURVY_BRACKET);
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

		final List<CComment> commentsAfterWhile = this.commentsHandler.getCommentsFollowingNodes().get(whileStatement);

		if (commentsAfterWhile == null)
		{
			printWriter.println();
		}
		else
		{
			commentsAfterWhile.sort(Comparator.comparingInt(CComment::getOriginalSourceCodeStartingLine));
			int nbTabsToConsider = 0;

			if (commentsAfterWhile.get(0).getOriginalSourceCodeStartingLine() != whileStatement.getFileLocation().getEndingLineNumber())
			{
				printWriter.println();
				nbTabsToConsider = nbTabs;
			}

			boolean first = true;

			for (final CComment comment : commentsAfterWhile)
			{
				printWriter.print(Utils.addLeadingTabulations(first ? nbTabsToConsider : nbTabs));
				printWriter.println(comment.getContent());
				first = false;
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
	@Deprecated
	private void dumpReturnStatement(final PrintWriter printWriter,
									 final CASTReturnStatement returnStatement,
									 final int nbTabs) throws UnhandledElementException
	{
		printWriter.println();
		printWriter.print(Utils.addLeadingTabulations(nbTabs));
		printWriter.print(CKeyword.RETURN);

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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
				this.dumpFunctionCallExpression(printWriter, (CASTFunctionCallExpression) child, nbTabs, true);
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

			printWriter.print(Char.SEMI_COLON);

			final List<CComment> commentsAfterExpressionStatement = this.commentsHandler.getCommentsFollowingNodes().get(expressionStatement);

			if (commentsAfterExpressionStatement == null)
			{
				printWriter.println();
			}
			else
			{
				commentsAfterExpressionStatement.sort(Comparator.comparingInt(CComment::getOriginalSourceCodeStartingLine));
				int nbTabsToConsider = 0;

				if (commentsAfterExpressionStatement.get(0).getOriginalSourceCodeStartingLine() != expressionStatement.getFileLocation().getEndingLineNumber())
				{
					printWriter.println();
					nbTabsToConsider = nbTabs;
				}

				boolean first = true;

				for (final CComment comment : commentsAfterExpressionStatement)
				{
					printWriter.print(Utils.addLeadingTabulations(first ? nbTabsToConsider : nbTabs));
					printWriter.println(comment.getContent());
					first = false;
				}
			}
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
	@Deprecated
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
	@Deprecated
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

		printWriter.print(Char.SEMI_COLON);

		final List<CComment> commentsAfterDeclaration = this.commentsHandler.getCommentsFollowingNodes().get(declarationStatement);

		if (commentsAfterDeclaration != null)
		{
			commentsAfterDeclaration.sort(Comparator.comparingInt(CComment::getOriginalSourceCodeStartingLine));
			int nbTabsToConsider = 0;

			if (commentsAfterDeclaration.get(0).getOriginalSourceCodeStartingLine() != declarationStatement.getFileLocation().getEndingLineNumber())
			{
				printWriter.println();
				nbTabsToConsider = nbTabs;
			}

			for (final CComment comment : commentsAfterDeclaration)
			{
				printWriter.print(Utils.addLeadingTabulations(nbTabsToConsider));
				printWriter.println(comment.getContent());
			}
		}
		else
		{
			printWriter.println();
		}
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
	@Deprecated
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
				this.dumpFunctionCallExpression(printWriter, (CASTFunctionCallExpression) child, nbTabs, false);
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
	@Deprecated
	private void dumpFunctionCallExpression(final PrintWriter printWriter,
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
					printWriter.print(Char.OPENING_BRACKET);
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

		printWriter.print(Char.CLOSING_BRACKET);
	}
}
