package misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Name:        CommandManager.java
 * Content:     This class defines a simple interface for calling commands as if they were started from the shell.
 * 				It handles both the standard input and output, and also the return value of the command.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    16/03/26
 */

public class CommandManager
{
	private final String command;
	private final ArrayList<String> args;
	private final StringBuilder stdOut;
	private final StringBuilder stdErr;
	private final File workingDirectory;
	private final File outputRedirectionFile;
	private int returnValue;

	public CommandManager(final String command,
						  final File workingDirectory,
						  final String... args)
	{
		this(command, workingDirectory, Arrays.asList(args));
	}

	public CommandManager(final String command,
						  final File workingDirectory,
						  final List<String> args)
	{
		this(command, workingDirectory, null, args);
	}

	public CommandManager(final String command,
						  final File workingDirectory,
						  final File outputRedirectionFile,
						  final String... args)
	{
		this(command, workingDirectory, outputRedirectionFile, Arrays.asList(args));
	}

	public CommandManager(final String command,
						  final File workingDirectory,
						  final File outputRedirectionFile,
						  final List<String> args)
	{
		this.command = command;
		this.args = new ArrayList<>(args);
		this.workingDirectory = workingDirectory;
		this.outputRedirectionFile = outputRedirectionFile;
		this.stdOut = new StringBuilder();
		this.stdErr = new StringBuilder();
	}

	public int execute() throws IOException, InterruptedException
	{
		//Build the command
		final ArrayList<String> command = new ArrayList<>();
		command.add(this.command);
		command.addAll(this.args);
		System.out.println("Command to execute: \"" + Utils.join(command, " ") + "\".");

		//Create the process builder
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(this.workingDirectory);

		if (this.outputRedirectionFile != null)
		{
			processBuilder.redirectOutput(this.outputRedirectionFile);
		}

		//Start the process
		final Process process = processBuilder.start();

		//Read stdout
		final InputStreamReader stdOutInputStreamReader = new InputStreamReader(process.getInputStream());
		final BufferedReader stdoutBufferedReader = new BufferedReader(stdOutInputStreamReader);

		String line;

		while ((line = stdoutBufferedReader.readLine()) != null)
		{
			this.stdOut.append(line).append("\n");
		}

		stdOutInputStreamReader.close();
		stdoutBufferedReader.close();

		//Read stderr
		final InputStreamReader stdErrInputStreamReader = new InputStreamReader(process.getErrorStream());
		final BufferedReader stdErrBufferedReader = new BufferedReader(stdErrInputStreamReader);

		while ((line = stdErrBufferedReader.readLine()) != null)
		{
			this.stdErr.append(line).append("\n");
		}

		stdErrInputStreamReader.close();
		stdErrBufferedReader.close();
		this.returnValue = process.waitFor();

		return this.returnValue;
	}

	public String stdOut()
	{
		return this.stdOut.toString();
	}

	public String stdErr()
	{
		return this.stdErr.toString();
	}

	public int returnValue()
	{
		return this.returnValue;
	}
}
