package constants;

/**
 * Name:        Color.java
 * Content:	    This class defines several ANSI colours allowing one to modify the colour of a text displayed
 * 				on the command line.
 * 				The message must be preceded by the desired colour's ANSI sequence and followed by the ANSI_RESET
 * 				sequence to go back to black (like Amy Winehouse).
 * 				They can also be used through their statically defined utility functions.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/02/26
 */

public class Color
{
	private Color()
	{

	}

	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_WHITE = "\u001B[37m";
	public static final String ANSI_YELLOW = "\u001B[33m";

	//Static utility functions

	public static String getBlueMessage(final String message)
	{
		return Color.getColoredMessage(message, Color.ANSI_BLUE);
	}

	public static String getCyanMessage(final String message)
	{
		return Color.getColoredMessage(message, Color.ANSI_CYAN);
	}

	public static String getGreenMessage(final String message)
	{
		return Color.getColoredMessage(message, Color.ANSI_GREEN);
	}

	public static String getPurpleMessage(final String message)
	{
		return Color.getColoredMessage(message, Color.ANSI_PURPLE);
	}

	public static String getRedMessage(final String message)
	{
		return Color.getColoredMessage(message, Color.ANSI_RED);
	}

	public static String getWhiteMessage(final String message)
	{
		return Color.getColoredMessage(message, Color.ANSI_WHITE);
	}

	public static String getYellowMessage(final String message)
	{
		return Color.getColoredMessage(message, Color.ANSI_YELLOW);
	}

	//Private methods

	private static String getColoredMessage(final String message,
											final String color)
	{
		return color + message + Color.ANSI_RESET;
	}
}
