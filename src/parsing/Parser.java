package parsing;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Parser
{
	/**
	 * Name:        Parser.java
	 * Content:     This class aims at parsing a given C program and at returning its corresponding AST.
	 * 				It basically wraps the parser of Eclipse-CDT with utility functions and hides the details to the
	 * 				end user.
	 * 				Note that the implementation of the parser in Eclipse-CDT seems not to consider the comments.
	 * 				TODO: Manage the (ACSL) comments with SYNTAX
	 * Author:      Quentin Nivon
	 * Email:       quentin.nivon@uol.de
	 * Creation:    25/02/26
	 */

	private final String program;

	//Constructors

	public Parser(final String program)
	{
		this.program = program;
	}

	public Parser(final File file)
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
			builder.append(line);
		}

		scanner.close();

		this.program = builder.toString();
	}

	//Public methods

	public IASTTranslationUnit parse() throws Exception
	{
		return this.parse(this.program.toCharArray());
	}

	//Private methods

	private IASTTranslationUnit parse(final char[] programToParse) throws Exception
	{
		final FileContent fileContent = FileContent.create("/Path/ToResolveIncludePaths.cpp", programToParse);
		final Map<String, String> macroDefinitions = new HashMap<>();
		final String[] includeSearchPaths = new String[0];
		final IScannerInfo scannerInfo = new ScannerInfo(macroDefinitions, includeSearchPaths);
		final IncludeFileContentProvider fileContentProvider = IncludeFileContentProvider.getEmptyFilesProvider();
		final IIndex index = null;
		final int options = ILanguage.OPTION_IS_SOURCE_UNIT;
		final IParserLogService logService = new DefaultLogService();

		return GCCLanguage.getDefault().getASTTranslationUnit(
			fileContent,
			scannerInfo,
			fileContentProvider,
			index,
			options,
			logService
		);
	}
}
