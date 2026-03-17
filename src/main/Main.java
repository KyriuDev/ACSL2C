package main;

import constants.c.CProgram;
import misc.CommandLineParser;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTTranslationUnit;
import parsing.ACSLParser;
import parsing.CommentsHandler;
import parsing.CParser;
import visitors.RecursiveVisitor;
import visitors.Visitors;
import writing.Writer;

import java.io.File;
import java.util.Collections;

/**
 *    Name:        Main.java
 *    Content:     Parsing of C programs enhanced with ACSL contracts and (partial) folding of such programs
 *    Author:      Quentin Nivon
 *    Email:       quentin.nivon@uol.de
 *    Creation:    25/02/26
 */

public class Main
{
	public static final boolean PERFORM_ALL_STEPS = false;
	public static final boolean PERFORM_ACSL_PARSING = true;

	public static void main(final String[] args) throws Exception
	{
		final CommandLineParser commandLineParser = new CommandLineParser(args);

		if (PERFORM_ALL_STEPS)
		{
			final CParser CParser = new CParser(CProgram.PROGRAM_1_WITH_ACSL_COMMENT);
			final CASTTranslationUnit translationUnit = (CASTTranslationUnit) CParser.parse();

			System.out.println("----------------- LINEAR PROGRAM -------------------\n");

			translationUnit.accept(Visitors.getCPrintVisitor());

			System.out.println("\n----------------- RECURSIVE PROGRAM -------------------\n");

			final RecursiveVisitor recursiveVisitor = new RecursiveVisitor(translationUnit);
			recursiveVisitor.printAST();

			System.out.println("\n----------------- COMMENTS HANDLING -------------------\n");

			final CommentsHandler commentsHandler = new CommentsHandler(translationUnit);
			commentsHandler.computeCommentsPrecedingAndSucceedingNodes();
			commentsHandler.displayMapping();

			System.out.println("\n----------------- GENERATING AST FROM JAVA -------------------\n");

			final ACSLParser parser = new ACSLParser(Collections.singletonList(commentsHandler.getRandomComment()));
			parser.parse();


			System.out.println("\n----------------- WRITING TO FILE -------------------\n");

			final Writer writer = new Writer(translationUnit, commandLineParser, commentsHandler);
			writer.writeToFile();
		}
		else
		{
			if (PERFORM_ACSL_PARSING)
			{
				final String BASE_PATH  = "/home/quentin/Documents/Post-doc/Frama-C/examples/working";
				final String FILE_NAME = "ex8.acsl";
				final File acslCommentFile = new File(BASE_PATH + File.separator + FILE_NAME);

				final ACSLParser parser = new ACSLParser(acslCommentFile);
				parser.parse();
			}
		}
	}
}