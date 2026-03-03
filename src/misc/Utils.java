package misc;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;

/**
 * Name:        Utils.java
 * Content:	    This class defines several utility functions used in the context of this project.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    25/02/26
 */


public class Utils
{
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
		final StringBuilder builder = new StringBuilder();

		for (int i = 0; i < nbTabulations; i++)
		{
			builder.append("	");
		}

		return builder.toString();
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
}
