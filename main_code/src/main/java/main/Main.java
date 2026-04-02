package main;

import acsl_to_c.ACSL2ASTTranslator;
import acsl_to_c.Merger;
import ast.AbstractSyntaxTree;
import ast.c.nodes.CBaseNode;
import ast.c.EclipseCDT2Internal;
import constants.CommandLineOption;
import constants.c.CProgram;
import misc.CommandLineParser;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTTranslationUnit;
import parsing.ACSLParser;
import parsing.CParser;
import parsing.CommentsHandlerV2;
import visitors.RecursiveVisitor;
import visitors.Visitors;
import writing.Writer;

import java.io.File;
import java.util.Arrays;

/**
 *    Name:        Main.java
 *    Content:     Parsing of C programs enhanced with ACSL contracts and (partial) folding of such programs
 *    Author:      Quentin Nivon
 *    Email:       quentin.nivon@uol.de
 *    Creation:    25/02/26
 */

public class Main
{
	public static final boolean PARSE_FROM_FILE = true;
	public static final boolean PERFORM_ALL_STEPS = true;
	public static final boolean PERFORM_ACSL_PARSING = true;

	public static void main(final String[] args) throws Exception
	{
		System.out.println("Args: " + Arrays.toString(args));
		final CommandLineParser commandLineParser = new CommandLineParser(args);

		if (PERFORM_ALL_STEPS)
		{
			final CParser CParser;

			if (PARSE_FROM_FILE)
			{
				CParser = new CParser((File) commandLineParser.get(CommandLineOption.C_FILE));
			}
			else
			{
				CParser = new CParser(CProgram.SV_COMP_MEMSAFETY_960521_1_1);
			}

			final CASTTranslationUnit translationUnit = (CASTTranslationUnit) CParser.parse();
			System.out.println(Arrays.toString(translationUnit.getMacroDefinitions()));

			System.out.println("----------------- LINEAR PROGRAM -------------------\n");

			translationUnit.accept(Visitors.getCPrintVisitor());

			System.out.println("\n----------------- RECURSIVE PROGRAM -------------------\n");

			final RecursiveVisitor recursiveVisitor = new RecursiveVisitor(translationUnit);
			recursiveVisitor.printAST();

			System.out.println("\n----------------- COMMENTS HANDLING -------------------\n");

			final CommentsHandlerV2 commentsHandler = new CommentsHandlerV2(translationUnit);
			commentsHandler.computeCommentsPrecedingAndSucceedingNodes();
			commentsHandler.displayMapping();

			System.out.println("\n----------------- AST TRANSLATION TO INTERNAL FORMAT -----------------\n");

			final AbstractSyntaxTree cProgramTree = EclipseCDT2Internal.translate(translationUnit, commentsHandler.getCommentsPrecedingNodes());
			System.out.println("C program internal tree:\n\n" + cProgramTree.toString());

			System.out.println("\n----------------- ACSL CONTRACTS PARSING -----------------\n");

			final ACSLParser parser = new ACSLParser(cProgramTree);
			parser.parse();

			System.out.println("\n----------------- ACSL CONTRACTS TRANSLATION -----------------\n");

			final ACSL2ASTTranslator translator = new ACSL2ASTTranslator(cProgramTree);
			translator.translate();

			System.out.println("\n----------------- ACSL CONTRACTS AND C PROGRAM FUSION -----------------\n");

			final Merger merger = new Merger(cProgramTree, translator);
			merger.merge();

			System.out.println("\n----------------- WRITING TO FILE -------------------\n");

			final Writer writer = new Writer(
				(CBaseNode) cProgramTree.getRoot(),
				translationUnit.getIncludeDirectives(),
				translationUnit.getMacroDefinitions(),
				commandLineParser,
				commentsHandler.getTrailingComments()
			);
			writer.writeToFile();
		}
		else
		{
			if (PERFORM_ACSL_PARSING)
			{
				final String BASE_PATH  = "/home/quentin/Documents/Post-doc/Frama-C/examples/working";
				final String FILE_NAME = "controller-int-correct.acsl";
				final File acslCommentFile = new File(BASE_PATH + File.separator + FILE_NAME);

				final ACSLParser parser = new ACSLParser(acslCommentFile);
				parser.parse();
			}
		}
	}
}