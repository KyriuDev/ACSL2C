package misc;

import constants.Color;
import constants.CommandLineOption;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Name:        CommandLineParser.java
 * Content:	    This class defines a simple parser for command line arguments. It is helpful to manage elements provided
 *              in a different order, and to avoid forcing a specific order of the arguments.
 *              It basically maps a command line option from CommandLineOption.java to an Object that can eventually
 *              be cast to its real type.
 * Author:      Quentin Nivon
 * Email:       quentin.nivon@uol.de
 * Creation:    26/02/26
 */

public class CommandLineParser
{
    private final Map<CommandLineOption, Object> commands;

    //Constructors

    public CommandLineParser(final String[] args)
	{
        this.commands = new HashMap<>();
        if (args == null) return;

        this.parse(args);

        if (this.commands.containsKey(CommandLineOption.HELP))
        {
            System.out.println(this.helpMessage());
            this.commands.clear();
        }

       // this.retrieveArgsFromWorkingDirectory();

        if (!this.verifyArgs())
        {
            throw new IllegalStateException("Necessary arguments are missing. Please make sure you have specified the" +
                    " following elements: C file, or that the working directory that you specified contains these" +
                    " elements.");
        }
    }

    //Public methods

    public void put(CommandLineOption commandLineOption,
                    Object o)
    {
        this.commands.put(commandLineOption, o);
    }

    public Object get(CommandLineOption commandLineOption)
    {
        return this.commands.get(commandLineOption);
    }

    public boolean containsKey(final CommandLineOption key)
    {
        return this.commands.containsKey(key);
    }

    //Private methods

    private String helpMessage()
    {
        //TODO
        return "";
    }

    private void parse(final String[] commandLineArgs)
    {
        //Check first for help or overwrites
        for (final String arg : commandLineArgs)
        {
            if (arg.equalsIgnoreCase("-h")
                || arg.equalsIgnoreCase("--h")
                || arg.equalsIgnoreCase("-help")
                || arg.equalsIgnoreCase("--help"))
            {
                this.commands.put(CommandLineOption.HELP, null);
                return;
            }
            else if (arg.equalsIgnoreCase("-f")
                    || arg.equalsIgnoreCase("--f")
                    || arg.equalsIgnoreCase("-force")
                    || arg.equalsIgnoreCase("--force"))
            {
                this.commands.put(CommandLineOption.OVERWRITE, true);
            }
        }

        //Manage the remaining elements
        for (final String arg : commandLineArgs)
        {
            if (this.isCFile(arg))
            {
                if (this.commands.containsKey(CommandLineOption.C_FILE))
                {
                    if ((boolean) this.commands.getOrDefault(CommandLineOption.OVERWRITE, false))
                    {
                        System.out.println(Color.getYellowMessage(String.format(
                            "WARNING: A .c file has already been parsed (%s). As overwriting has been permitted, " +
                            "the old one will be replaced by the new one (%s).",
                            ((File) this.commands.get(CommandLineOption.C_FILE)).getAbsolutePath(),
                            arg
                        )));

                        this.commands.put(CommandLineOption.C_FILE, new File(arg));
                    }
                    else
                    {
                        System.out.println(Color.getYellowMessage(String.format(
                            "WARNING: A .c file has already been parsed (%s). As overwriting has not been " +
                            "permitted, the current one (%s) will be ignored.",
                            ((File) this.commands.get(CommandLineOption.C_FILE)).getAbsolutePath(),
                            arg
                        )));
                    }
                }
                else
                {
                    this.commands.put(CommandLineOption.C_FILE, new File(arg));
                }
            }
            else if (this.isDir(arg))
            {
                if (this.commands.containsKey(CommandLineOption.WORKING_DIRECTORY))
                {
                    if ((boolean) this.commands.getOrDefault(CommandLineOption.OVERWRITE, false))
                    {
                        System.out.println(Color.getYellowMessage(String.format(
                            "WARNING: A working directory has already been parsed (%s). As overwriting has been " +
                            "permitted, the old one will be replaced by the new one (%s).",
                            ((File) this.commands.get(CommandLineOption.WORKING_DIRECTORY)).getAbsolutePath(),
                            arg
                        )));

                        this.commands.put(CommandLineOption.WORKING_DIRECTORY, new File(arg));
                    }
                    else
                    {
                        System.out.println(Color.getYellowMessage(String.format(
                            "WARNING: A working directory has already been parsed (%s). As overwriting has not been " +
                            "permitted, the current one (%s) will be ignored.",
                            ((File) this.commands.get(CommandLineOption.WORKING_DIRECTORY)).getAbsolutePath(),
                            arg
                        )));
                    }
                }
                else
                {
                    this.commands.put(CommandLineOption.WORKING_DIRECTORY, new File(arg));
                }
            }
        }
	}

    private void retrieveArgsFromWorkingDirectory()
    {
        if (this.commands.get(CommandLineOption.WORKING_DIRECTORY) == null
            && this.commands.get(CommandLineOption.C_FILE) == null)
        {
            return;
        }

        if (this.commands.get(CommandLineOption.WORKING_DIRECTORY) != null)
        {
            //We got a working directory, let's check if there are some interesting stuff (like C files) inside.
            final File workingDirectory = (File) this.commands.get(CommandLineOption.WORKING_DIRECTORY);

            for (final File file : Objects.requireNonNull(workingDirectory.listFiles()))
            {
                if (this.isCFile(file.getAbsolutePath()))
                {
                    if (this.commands.containsKey(CommandLineOption.C_FILE))
                    {
                        if ((boolean) this.commands.getOrDefault(CommandLineOption.OVERWRITE, false))
                        {
                            System.out.println(Color.getYellowMessage(String.format(
                                "WARNING: A .c file has already been parsed (%s). As overwriting has been permitted, " +
                                "the old one will be replaced by the new one (%s).",
                                ((File) this.commands.get(CommandLineOption.C_FILE)).getAbsolutePath(),
                                file.getAbsolutePath()
                            )));

                            this.commands.put(CommandLineOption.C_FILE, file);
                        }
                        else
                        {
                            System.out.println(Color.getYellowMessage(String.format(
                                "WARNING: A .c file has already been parsed (%s). As overwriting has not been " +
                                "permitted, the current one (%s) will be ignored.",
                                ((File) this.commands.get(CommandLineOption.C_FILE)).getAbsolutePath(),
                                file.getAbsolutePath()
                            )));
                        }
                    }
                    else
                    {
                        this.commands.put(CommandLineOption.C_FILE, file);
                    }
                }
            }
        }
        else
        {
            /*
                We got a C file, but we don't have any working directory yet.
                Let's build one from the C file location.
             */
            final File cFile = (File) this.commands.get(CommandLineOption.C_FILE);
            final String workingDirectoryPath = cFile.getParent();

            if (workingDirectoryPath != null)
            {
                this.commands.put(CommandLineOption.WORKING_DIRECTORY, new File(workingDirectoryPath));
            }
        }
    }

    private boolean verifyArgs()
    {
        return this.commands.get(CommandLineOption.WORKING_DIRECTORY) != null
                && this.commands.get(CommandLineOption.C_FILE) != null;
    }

    private boolean isCFile(final String arg)
    {
		return arg.endsWith(".c")
				&& new File(arg).isFile();
	}

    private boolean isDir(final String arg)
    {
        return new File(arg).isDirectory();
    }
}
