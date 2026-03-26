package constants;

import java.util.Set;

/**
 * Name:        CommandLineOption.java
 * Content:	    This enum defines the type of elements that the CommandLineParser.java class can receive.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/02/26
 */

public enum CommandLineOption
{
	WORKING_DIRECTORY("--working-directory=", "--working-dir=", "-wdir="),
	OVERWRITE("-f", "--force", "--overwrite"),
	HELP("-h", "--help"),
	C_FILE("--c-file=")
	;

	private final Set<String> optionNames;

	CommandLineOption(final String... optionNames)
	{
		this.optionNames = Set.of(optionNames);
	}

	public Set<String> getOptionNames()
	{
		return this.optionNames;
	}
}
