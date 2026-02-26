package main;

import constants.CProgram;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTTranslationUnit;
import parsing.Parser;
import visitors.RecursiveVisitor;
import visitors.Visitors;

/**
 *    Name:        main.main.Main.java
 *    Content:     Parsing of C programs enhanced with ACSL contracts and (partial) folding of such programs
 *    Author:      Quentin Nivon
 *    Email:       quentin.nivon@uol.de
 *    Creation:    25/02/26
 */

public class Main
{
	public static void main(final String[] args) throws Exception
	{
		//final CommandLineParser commandLineParser = new CommandLineParser(args);

		final Parser parser = new Parser(CProgram.PROGRAM_SPIN_PAPER);
		final CASTTranslationUnit translationUnit = (CASTTranslationUnit) parser.parse();

		System.out.println("----------------- LINEAR PROGRAM -------------------\n");

		translationUnit.accept(Visitors.getCPrintVisitor());

		System.out.println("\n----------------- RECURSIVE PROGRAM -------------------\n");

		final RecursiveVisitor recursiveVisitor = new RecursiveVisitor(translationUnit);
		recursiveVisitor.printAST();
	}
}