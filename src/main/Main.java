package main;

import constants.CProgram;
import dto.CComment;
import misc.CommandLineParser;
import misc.Pair;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTTranslationUnit;
import parsing.CommentsHandler;
import parsing.Parser;
import visitors.RecursiveVisitor;
import visitors.Visitors;
import writing.Writer;

import java.util.Map;

/**
 *    Name:        Main.java
 *    Content:     Parsing of C programs enhanced with ACSL contracts and (partial) folding of such programs
 *    Author:      Quentin Nivon
 *    Email:       quentin.nivon@uol.de
 *    Creation:    25/02/26
 */

public class Main
{
	public static void main(final String[] args) throws Exception
	{
		final CommandLineParser commandLineParser = new CommandLineParser(args);

		final Parser parser = new Parser(CProgram.PROGRAM_TEST_PARSING_WRITING);
		final CASTTranslationUnit translationUnit = (CASTTranslationUnit) parser.parse();

		System.out.println("----------------- LINEAR PROGRAM -------------------\n");

		translationUnit.accept(Visitors.getCPrintVisitor());

		System.out.println("\n----------------- RECURSIVE PROGRAM -------------------\n");

		final RecursiveVisitor recursiveVisitor = new RecursiveVisitor(translationUnit);
		recursiveVisitor.printAST();

		System.out.println("\n----------------- COMMENTS HANDLING -------------------\n");

		final CommentsHandler commentsHandler = new CommentsHandler(translationUnit);
		commentsHandler.computeCommentsPrecedingAndSucceedingNodes();
		commentsHandler.displayMapping();

		System.out.println("\n----------------- WRITING TO FILE -------------------\n");

		final Writer writer = new Writer(translationUnit, commandLineParser);
		writer.writeToFile();
	}
}