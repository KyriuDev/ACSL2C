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
        this.put(CommandLineOption.WORKING_DIRECTORY, new File("/home/quentin/test"));
        this.put(CommandLineOption.C_FILE, new File("/home/quentin/test/file.c"));

        if (args == null) return;

        this.parse(args);

        if (this.commands.containsKey(CommandLineOption.HELP))
        {
            System.out.println(this.helpMessage());
            this.commands.clear();
            return;
        }

        this.retrieveArgsFromWorkingDirectory();

        if (!this.verifyArgs())
        {
            throw new IllegalStateException(
                "Necessary arguments are missing. Please make sure you have specified the following elements: C " +
                "file, or that the working directory that you specified contains these elements."
            );
        }
    }

    //Public methods

    /**
     * This method is a wrapper for the Map.put(Object, Object) function, which allows any internal representation of
     * that given map.
     *
     * @param commandLineOption the considered command line option.
     * @param o the object to associate with it.
     */
    public void put(final CommandLineOption commandLineOption,
                    final Object o)
    {
        this.commands.put(commandLineOption, o);
    }

    /**
     * This method is a wrapper for the Map.get(Object) function, which allows any internal representation of that
     * given map.
     *
     * @param commandLineOption the considered command line option.
     */
    public Object get(final CommandLineOption commandLineOption)
    {
        return this.commands.get(commandLineOption);
    }

    /**
     * This method is a wrapper for the Map.getOrDefault(Object, Object) function, which allows any internal
     * representation of that given map.
     *
     * @param commandLineOption the considered command line option.
     * @param defaultValue the default value to associate to the command line option if it does not belong to the map.
     */
    public Object getOrDefault(final CommandLineOption commandLineOption,
                               final Object defaultValue)
    {
        return this.commands.getOrDefault(commandLineOption, defaultValue);
    }

    /**
     * This method is a wrapper for the Map.containsKey(Object) function, which allows any internal representation of
     * that given map.
     *
     * @param commandLineOption the considered command line option.
     */
    public boolean containsKey(final CommandLineOption commandLineOption)
    {
        return this.commands.containsKey(commandLineOption);
    }

    //Private methods

    /**
     * This method shows a helpful message on the command line to the user that does not know (yet) how to use this
     * program.
     *
     * @return the help message
     */
    private String helpMessage()
    {
        //TODO
        return "The helping message has not been implemented yet :-(";
    }

    /**
     * This method is used to parse the arguments given on the command line.
     * It first checks if the user asked for help, or agrees to (potentially) overwrite the files.
     * Then, it performs the remaining analysis to retrieve all the other elements.
     *
     * @param commandLineArgs the array of command line arguments
     */
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
                this.put(CommandLineOption.HELP, null);
                return;
            }
            else if (arg.equalsIgnoreCase("-f")
                    || arg.equalsIgnoreCase("--f")
                    || arg.equalsIgnoreCase("-force")
                    || arg.equalsIgnoreCase("--force"))
            {
                this.put(CommandLineOption.OVERWRITE, true);
            }
        }

        //Manage the remaining elements
        for (final String arg : commandLineArgs)
        {
            if (this.isCFile(arg))
            {
                if (this.containsKey(CommandLineOption.C_FILE))
                {
                    if ((boolean) this.getOrDefault(CommandLineOption.OVERWRITE, false))
                    {
                        System.out.println(Color.getYellowMessage(String.format(
                            "WARNING: A .c file has already been parsed (%s). As overwriting has been permitted, " +
                            "the old one will be replaced by the new one (%s).",
                            ((File) this.get(CommandLineOption.C_FILE)).getAbsolutePath(),
                            arg
                        )));

                        this.put(CommandLineOption.C_FILE, new File(arg));
                    }
                    else
                    {
                        System.out.println(Color.getYellowMessage(String.format(
                            "WARNING: A .c file has already been parsed (%s). As overwriting has not been " +
                            "permitted, the current one (%s) will be ignored.",
                            ((File) this.get(CommandLineOption.C_FILE)).getAbsolutePath(),
                            arg
                        )));
                    }
                }
                else
                {
                    this.put(CommandLineOption.C_FILE, new File(arg));
                }
            }
            else if (this.isDir(arg))
            {
                if (this.containsKey(CommandLineOption.WORKING_DIRECTORY))
                {
                    if ((boolean) this.getOrDefault(CommandLineOption.OVERWRITE, false))
                    {
                        System.out.println(Color.getYellowMessage(String.format(
                            "WARNING: A working directory has already been parsed (%s). As overwriting has been " +
                            "permitted, the old one will be replaced by the new one (%s).",
                            ((File) this.get(CommandLineOption.WORKING_DIRECTORY)).getAbsolutePath(),
                            arg
                        )));

                        this.put(CommandLineOption.WORKING_DIRECTORY, new File(arg));
                    }
                    else
                    {
                        System.out.println(Color.getYellowMessage(String.format(
                            "WARNING: A working directory has already been parsed (%s). As overwriting has not been " +
                            "permitted, the current one (%s) will be ignored.",
                            ((File) this.get(CommandLineOption.WORKING_DIRECTORY)).getAbsolutePath(),
                            arg
                        )));
                    }
                }
                else
                {
                    this.put(CommandLineOption.WORKING_DIRECTORY, new File(arg));
                }
            }
        }
	}

    /**
     * This method is used to retrieve the working directory if it was not specified, and the potential useful files
     * that it may contain if it was specified.
     * Once retrieved, they are correctly put inside the Map.
     */
    private void retrieveArgsFromWorkingDirectory()
    {
        if (this.get(CommandLineOption.WORKING_DIRECTORY) == null
            && this.get(CommandLineOption.C_FILE) == null)
        {
            return;
        }

        if (this.get(CommandLineOption.WORKING_DIRECTORY) != null)
        {
            //We got a working directory, let's check if there are some interesting stuff (like C files) inside.
            final File workingDirectory = (File) this.get(CommandLineOption.WORKING_DIRECTORY);

            for (final File file : Objects.requireNonNull(workingDirectory.listFiles()))
            {
                if (this.isCFile(file.getAbsolutePath()))
                {
                    if (this.containsKey(CommandLineOption.C_FILE))
                    {
                        if ((boolean) this.getOrDefault(CommandLineOption.OVERWRITE, false))
                        {
                            System.out.println(Color.getYellowMessage(String.format(
                                "WARNING: A .c file has already been parsed (%s). As overwriting has been permitted, " +
                                "the old one will be replaced by the new one (%s).",
                                ((File) this.get(CommandLineOption.C_FILE)).getAbsolutePath(),
                                file.getAbsolutePath()
                            )));

                            this.put(CommandLineOption.C_FILE, file);
                        }
                        else
                        {
                            System.out.println(Color.getYellowMessage(String.format(
                                "WARNING: A .c file has already been parsed (%s). As overwriting has not been " +
                                "permitted, the current one (%s) will be ignored.",
                                ((File) this.get(CommandLineOption.C_FILE)).getAbsolutePath(),
                                file.getAbsolutePath()
                            )));
                        }
                    }
                    else
                    {
                        this.put(CommandLineOption.C_FILE, file);
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
            final File cFile = (File) this.get(CommandLineOption.C_FILE);
            final String workingDirectoryPath = cFile.getParent();

            if (workingDirectoryPath != null)
            {
                this.put(CommandLineOption.WORKING_DIRECTORY, new File(workingDirectoryPath));
            }
        }
    }

    /**
     * This method is used to verify whether the information currently contained in the Map is sufficient for the
     * program to work.
     *
     * @return true if the required elements are present, false otherwise.
     */
    private boolean verifyArgs()
    {
        return this.get(CommandLineOption.WORKING_DIRECTORY) != null
                && this.get(CommandLineOption.C_FILE) != null;
    }

    /**
     * This method checks whether the given argument is (apparently, at least) a correct C file.
     * Here, apparently means that any existing file with the extensions ".c" will be considered as a correct C file.
     *
     * @param arg the argument to check.
     * @return true if the argument is apparently a correct C file, false otherwise.
     */
    private boolean isCFile(final String arg)
    {
		return arg.endsWith(".c")
				&& new File(arg).isFile();
	}

    /**
     * This method checks whether the given argument is a path to an existing directory.
     *
     * @param arg the argument to check.
     * @return true if the argument is indeed a path to an existing directory, false otherwise.
     */
    private boolean isDir(final String arg)
    {
        return new File(arg).isDirectory();
    }
}
