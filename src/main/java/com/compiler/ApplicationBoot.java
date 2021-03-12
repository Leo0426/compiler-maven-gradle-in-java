package com.compiler;

import com.compiler.boot.CLIOptions;
import com.google.gson.Gson;
import org.apache.commons.cli.ParseException;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.compiler.resolver.gradle.GradleResolver.execGradleProjectForSimple;
import static com.compiler.resolver.maven.MavenResolver.execMavenProjectForSimple;
import static com.compiler.resolver.maven.MavenResolver.writeToFile;
import static com.compiler.support.FileChecker.GRADLE_FILE;
import static com.compiler.support.FileChecker.POM_FILE;
import static com.compiler.support.FileChecker.getBuilderFiles;

/**
 * bootstrap.
 *
 * @author LeoLu
 * @since 2021-03-04
 **/
public class ApplicationBoot {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationBoot.class);

    public static void main(String[] args) {
        try {
            logger.info("start analysis...");
            logger.info("parameter are : {}", Arrays.asList(args).toString());
            CLIOptions cliOptions = new CLIOptions(args);
            String projectPath = cliOptions.getProjectPath();
            List<String> builderFiles = getBuilderFiles(projectPath);
            List<String> strings = collectionDependencies(builderFiles);
            Gson gson = new Gson();
            String report = gson.toJson(strings);
            writeToFile(report);
        } catch (ParseException | MavenOperationException | MavenInvocationException | IOException e) {
            logger.error("main exec get an exception : " + e.getMessage());
        }
        logger.info("Finished analysis.");
    }

    private static List<String> collectionDependencies(List<String> builderFiles)
            throws MavenOperationException, MavenInvocationException, IOException {
        List<String> result = new ArrayList<>();
        for (String e : builderFiles) {
            if (e.endsWith(POM_FILE)) {
                result.addAll(execMavenProjectForSimple(e));
            } else if (e.endsWith(GRADLE_FILE)) {
                result.addAll(execGradleProjectForSimple(e));
            }
        }
        return result.stream().distinct().collect(Collectors.toList());
    }

}
