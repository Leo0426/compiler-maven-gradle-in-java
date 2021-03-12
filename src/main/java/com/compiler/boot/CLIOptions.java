package com.compiler.boot;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * sca cli options.
 *
 * @author LeoLu
 * @since 2021-03-08
 **/
public class CLIOptions {

    protected static final String USAGE = "[options] in project.";

    /**
     * create Options object
     */
    private final Options options = new Options();

    /**
     * project path.
     */
    private final static String PROJECT_PATH = "path";

    /**
     * help
     */
    private final static String HELP = "h";

    /**
     * project type.
     */
    private final static String PROJECT_TYPE = "projectType";

    /**
     * output path
     */
    private final static String OUTPUT_PATH = "outputPath";


    private String projectPath;

    public CLIOptions(String[] args) throws ParseException {
        initOptions();
        parseOptions(args);
    }

    private void parseOptions(String[] args) throws ParseException {
        CommandLine commandParameters = new DefaultParser().parse(options, args);
        if (commandParameters.hasOption(HELP)) {
            dumpUsage();
        }
        if (commandParameters.hasOption(PROJECT_PATH)) {
            this.projectPath = commandParameters.getOptionValue(PROJECT_PATH);
        }
    }

    /**
     * options management.
     */
    private void initOptions() {
        options.addOption(Option.builder(HELP).longOpt("help")
                .hasArg(false).desc("command parameter.").required(false).build());
        options.addOption(Option.builder(PROJECT_PATH).longOpt("projectPath")
                .hasArg(true).desc("the project path.").build());
        options.addOption(Option.builder(OUTPUT_PATH)
                .hasArg(true).desc("file out writ direction.").required(false).build());
        options.addOption(Option.builder(PROJECT_TYPE)
                .hasArg(true).desc("Gradle, Maven, Ant etc.").required(false).build());
    }

    /**
     * help doc.
     */
    private void dumpUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(USAGE, options);
        System.exit(0);
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
