package misc;

import constants.Xml;
import org.apache.commons.io.FileUtils;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Name:        Utils.java
 * Content:	    This class defines several utility functions used in the context of this project.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    25/02/26
 */

public class Utils
{
	private static final String TEMP_DIR_PROPERTY = "java.io.tmpdir";

	private Utils()
	{

	}

	public static boolean iastNodeHasChildren(final IASTNode iastNode)
	{
		return iastNode.getChildren() != null && iastNode.getChildren().length != 0;
	}

	/**
	 * This function returns the starting line number of the IASTNode given as parameter, or -1 if the given node does
	 * not have any line number associated to it.
	 *
	 * @param node the node to retrieve the starting line number from.
	 * @return the starting line number of the given node if available, -1 otherwise.
	 */
	public static int getStartingLineNumberOf(final IASTNode node)
	{
		return node.getFileLocation() == null ? -1 : node.getFileLocation().getStartingLineNumber();
	}

	/**
	 * This function returns the ending line number of the IASTNode given as parameter, or -1 if the given node does
	 * not have any line number associated to it.
	 *
	 * @param node the node to retrieve the ending line number from.
	 * @return the ending line number of the given node if available, -1 otherwise.
	 */
	public static int getEndingLineNumberOf(final IASTNode node)
	{
		return node.getFileLocation() == null ? -1 : node.getFileLocation().getEndingLineNumber();
	}

	/**
	 * This function simply returns "nbTabulations" tabulations.
	 *
	 * @param nbTabulations the number of tabulations to insert
	 * @return "nbTabulations" tabulations
	 */
	public static String addLeadingTabulations(final int nbTabulations)
	{
		return "	".repeat(Math.max(0, nbTabulations));
	}

	/**
	 * This function is a wrapper for Utils.isAnInt(String).
	 *
	 * @param c the char to check
	 * @return true if c is a valid integer value, false otherwise.
	 */
	public static boolean isAnInt(final char c)
	{
		return Utils.isAnInt(String.valueOf(c));
	}

	/**
	 * This function checks whether the given string is a valid integer value.
	 *
	 * @param s the string to check.
	 * @return true if s is a valid integer value, false otherwise.
	 */
	public static boolean isAnInt(final String s)
	{
		try
		{
			Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			return false;
		}

		return true;
	}

	/**
	 * This method corresponds to the String.join(List<T>) Python method.
	 *
	 * @param elements the elements to concatenate.
	 * @return a string corresponding to the concatenation of the input elements
	 */
	public static String join(final List<?> elements,
							  final String separatorToUse)
	{
		final StringBuilder builder = new StringBuilder();
		String separator = "";

		for (Object element : elements)
		{
			builder.append(separator)
					.append(element.toString());

			separator = separatorToUse;
		}

		return builder.toString();
	}

	/**
	 * This method asks the JVM to find or create a usable temporary directory.
	 *
	 * @return the absolute path of the temporary directory if it could be created/found, null otherwise.
	 */
	public static String getTempDir()
	{
		//Get a temporary directory and clean it.
		final String tmpDir;

		try
		{
			tmpDir = Files.createTempDirectory("").toFile().getAbsolutePath();
			final String tmpDirsLocation = System.getProperty(TEMP_DIR_PROPERTY);
			assert tmpDir.startsWith(tmpDirsLocation);
			final File tempDir = new File(tmpDir);
			FileUtils.cleanDirectory(tempDir);
		}
		catch (final IOException e)
		{
			return null;
		}

		return tmpDir;
	}

	/**
	 * This method is used to insert the opening tag characters '<' and '>' respectively at the beginning and at the
	 * end of the string given as input.
	 *
	 * @param s the string to surround by tag characters.
	 * @return the "tagged" version of the string.
	 */
	public static String tagify(final String s)
	{
		return Xml.OPENING_TAG + s + Xml.CLOSING_TAG;
	}

	/**
	 * This method creates a string composed of nbTabs tabulations.
	 *
	 * @param nbTabs the number of tabulations to insert.
	 * @return a string composed of nbTabs tabulations.
	 */
	public static String repeatTabs(final int nbTabs)
	{
		final StringBuilder builder = new StringBuilder();

		for (int i = 0; i < nbTabs; i++)
		{
			builder.append("\t");
		}

		return builder.toString();
	}
}
