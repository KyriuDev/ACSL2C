package exceptions;

/**
 * Name:        UnhandledElementException.java
 * Content:     This class defines the UnhandledElementException, which is used to report the fact that an element
 * 				is encountered at a location where it is not expected to.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    27/02/26
 */

public class UnhandledElementException extends Exception
{
	public UnhandledElementException()
	{
		super();
	}

	public UnhandledElementException(final String message)
	{
		super(message);
	}
}
