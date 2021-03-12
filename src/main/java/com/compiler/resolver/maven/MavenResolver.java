package com.compiler.resolver.maven;

import com.compiler.MavenOperationException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.compiler.support.FileChecker.getMavenPoms;

/**
 * MavenResolver
 *
 * @author LeoLu
 * @since 2021-03-08
 **/
public class MavenResolver {

    /**
     * get dependencies tree.
     */
    private static final String DEPENDENCY_LIST = "dependency:list";

    public static final String OUTPUT_FILE = getPath() + File.separator + "dependenciesList.txt";

    private static final Pattern PATTERN = Pattern.compile("(?:compile|runtime):(.*)");

    /**
     * get maven dependencies from pom.
     *
     * @param pomFile pomFile.
     * @return List of dependencies.
     * @throws MavenInvocationException mavenInvocationException
     * @throws MavenOperationException  MavenOperationException
     * @throws IOException              IOException
     */
    public static List<String> getMavenDependenciesList(File pomFile) throws MavenInvocationException,
            MavenOperationException, IOException {
        List<String> resultList = new ArrayList<>();

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pomFile);
        request.setGoals(Collections.singletonList(DEPENDENCY_LIST));
        Properties properties = new Properties();
        // redirect output to a file
        properties.setProperty("outputFile", OUTPUT_FILE);
        // with paths
        properties.setProperty("outputAbsoluteArtifactFilename", "true");
        // only runtime (scope compile + runtime)
        properties.setProperty("includeScope", "runtime");
        request.setProperties(properties);

        Invoker invoker = new DefaultInvoker();
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new MavenOperationException("Build failed.");
        }

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(OUTPUT_FILE))) {
            while (!"The following files have been resolved:".equals(reader.readLine())) ;
            String line;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Matcher matcher = PATTERN.matcher(line);
                if (matcher.find()) {
                    // group 1 contains the path to the file
                    String group = matcher.group(1);
                    resultList.add(group.substring(group.lastIndexOf(File.separator) + 1));
                }
            }
        }
        return resultList;
    }

    /**
     * get maven dependencies from path.
     *
     * @param path path
     * @return List<String>
     */
    public static List<String> execMavenProject(String path) throws MavenOperationException,
            MavenInvocationException, IOException {
        List<String> mavenPoms = getMavenPoms(path);
        List<String> mavenDependenciesListAll = new ArrayList<>();
        for (String mavenPom : mavenPoms) {
            List<String> mavenDependenciesList = getMavenDependenciesList(new File(mavenPom));
            mavenDependenciesListAll.addAll(mavenDependenciesList);
        }
        return mavenDependenciesListAll.stream().distinct().collect(Collectors.toList());
    }

    /**
     * get maven dependencies from path.
     *
     * @param path path
     * @return List<String>
     */
    public static List<String> execMavenProjectForSimple(String path) throws MavenOperationException,
            MavenInvocationException, IOException {
        return getMavenDependenciesList(new File(path));
    }

    public static void writeToFile(String json) {
        File file = new File(getPath() + File.separator + "cveReport.json");
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
     * get exec path.
     *
     * @return String path.
     */
    public static String getPath() {
        URL url = MavenResolver.class.getProtectionDomain().getCodeSource().getLocation();
        String filePath = null;
        try {
            filePath = URLDecoder.decode(url.getPath(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert filePath != null;
        if (filePath.endsWith(".jar")) {
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        }

        File file = new File(filePath);
        filePath = file.getAbsolutePath();
        return filePath;
    }
}
