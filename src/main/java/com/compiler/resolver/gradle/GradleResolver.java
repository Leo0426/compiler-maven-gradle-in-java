package com.compiler.resolver.gradle;

import com.google.gson.Gson;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.compiler.resolver.maven.MavenResolver.OUTPUT_FILE;
import static com.compiler.support.FileChecker.getGradleBuilders;


/**
 * gradleResolver
 *
 * @author LeoLu
 * @since 2021-03-08
 **/
public class GradleResolver {

    /**
     * get gradle dependencies.
     *
     * @param file gradle project path.
     * @return List<String>
     */
    public static List<String> getGradleDependenciesList(File file) {

        List<String> resultList = new ArrayList<>();

        ProjectConnection connect = GradleConnector.newConnector().forProjectDirectory(file).connect();
        List<IdeaSingleEntryLibraryDependency> collect = connect.model(IdeaProject.class).get()
                .getModules().stream().flatMap(e -> e.getDependencies().stream()
                        .filter(x -> x instanceof IdeaSingleEntryLibraryDependency)
                        .map(x -> (IdeaSingleEntryLibraryDependency) x))
                .collect(Collectors.toList());

        connect.close();

        for (IdeaSingleEntryLibraryDependency ideaSingleEntryLibraryDependency : collect) {
            resultList.add(ideaSingleEntryLibraryDependency.getFile().getName());
        }
        Gson gson = new Gson();
        String gradleDependencies = gson.toJson(resultList);
        writeToGradleFile(gradleDependencies);
        return resultList;
    }

    public static void writeToGradleFile(String json) {
        File file = new File(OUTPUT_FILE);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(json);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get gradle dependencies from path.
     *
     * @param path path
     * @return List<String>
     */
    public static List<String> execGradleProject(String path) {
        List<String> gradleBuilders = getGradleBuilders(path);
        List<String> gradleBuildersList = new ArrayList<>();
        for (String gradle : gradleBuilders) {
            String substring = gradle.substring(0, gradle.lastIndexOf(File.separator));
            List<String> mavenDependenciesList = getGradleDependenciesList(new File(substring));
            gradleBuildersList.addAll(mavenDependenciesList);
        }
        return gradleBuildersList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * get gradle dependencies from path.
     *
     * @param path path
     * @return List<String>
     */
    public static List<String> execGradleProjectForSimple(String path) {
        String substring = path.substring(0, path.lastIndexOf(File.separator));
        return getGradleDependenciesList(new File(substring));
    }
}
