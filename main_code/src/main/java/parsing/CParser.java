package parsing;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.core.runtime.CoreException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Name:        CParser.java
 * Content:     This class aims at parsing a given C program and at returning its corresponding AST.
 *				It basically wraps the parser of Eclipse-CDT with utility functions and hides the details to the
 * 				end user.
 * 				In addition to the "classical" elements that can be view directly by traversing the AST, one can access
 * 				the following information of the C file that is not directly stored in the AST:
 * 				- C Comments via IASTTranslationUnit.getComments();
 * 				- C Include directives (#include <my_file>) via IASTTranslationUnit.getIncludeDirectives();
 * 				- C Macro definitions (#DEFINE <name> <value>) via IASTTranslationUnit.getMacroDefinitions();
 * 				TODO: Manage the (ACSL) comments with SYNTAX
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    25/02/26
 */

public class CParser
{
	private final String program;

	//Constructors

	public CParser(final String program)
	{
		this.program = program;
	}

	public CParser(final File file)
	{
		final Scanner scanner;

		try
		{
			scanner = new Scanner(file);
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		final StringBuilder builder = new StringBuilder();

		while (scanner.hasNextLine())
		{
			final String line = scanner.nextLine();
			builder.append(line)
					.append("\n");
		}

		scanner.close();

		this.program = builder.toString();
		System.out.println("Read program:\n\n" + this.program);
	}

	//Public methods

	public IASTTranslationUnit parse() throws CoreException
	{
		return this.parse(this.program.toCharArray());
	}

	//Private methods

	/**
	 * This method aims at parsing a C program using the Eclipse-CDT plugin facilities.
	 * It corresponds to Listing 2 of the paper "Using the Eclipse C/C++ Development Tooling as a Robust, Fully
	 * Functional, Actively Maintained, Open Source C++ Parser" from Piatov, Janes, Sillitti and Succi.
	 * The only change that I made to it is the use of "GCCLanguage" instead of "GPPLanguage" in the return statement.
	 * I currently (27/02/26) do not know what all the given options are used for, but it seems to work.
	 * TODO: Try to find a way to avoid macros to be replaced by their actual values during parsing
	 *
	 * @param programToParse the array of chars corresponding to the program to parse
	 * @return an IASTTranslationUnit, the root node of the generated AST
	 * @throws CoreException if the parsing of the C file fails.
	 */
	private IASTTranslationUnit parse(final char[] programToParse) throws CoreException
	{
		final FileContent fileContent = FileContent.create("/Path/ToResolveIncludePaths.cpp", programToParse);
		final Map<String, String> macroDefinitions = new HashMap<>();
		final String[] includeSearchPaths = new String[0];
		final IScannerInfo scannerInfo = new ScannerInfo(macroDefinitions, includeSearchPaths);
		final IncludeFileContentProvider fileContentProvider = IncludeFileContentProvider.getEmptyFilesProvider();
		final IIndex index = null;
		final int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		final IParserLogService logService = new DefaultLogService();

		final IASTTranslationUnit translationUnit = GCCLanguage.getDefault().getASTTranslationUnit(
			fileContent,
			scannerInfo,
			fileContentProvider,
			index,
			options,
			logService
		);

		return translationUnit;
	}
}
