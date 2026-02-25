import constants.CPrograms;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTEqualsInitializer;
import org.eclipse.cdt.internal.core.dom.parser.c.CASTTranslationUnit;
import parsing.Parser;
import visitors.RecursiveVisitor;
import visitors.Visitors;

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
		final Parser parser = new Parser(CPrograms.PROGRAM_1_WITH_LINE_BREAKS);
		final CASTTranslationUnit translationUnit = (CASTTranslationUnit) parser.parse();


		System.out.println("----------------- LINEAR PROGRAM -------------------\n");

		translationUnit.accept(Visitors.getCPrintVisitor());

		System.out.println("\n----------------- RECURSIVE PROGRAM -------------------\n");

		final RecursiveVisitor recursiveVisitor = new RecursiveVisitor(translationUnit);
		recursiveVisitor.printAST();
	}
}