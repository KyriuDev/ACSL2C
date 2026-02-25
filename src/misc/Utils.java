package misc;

public class Utils
{
	/**
	 * Name:        Utils.java
	 * Content:	    This class defines several utility functions used in the context of this project.
	 * Author:      Quentin Nivon
	 * Email:       quentin.nivon@uol.de
	 * Creation:    25/02/26
	 */

	private Utils()
	{

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
}
