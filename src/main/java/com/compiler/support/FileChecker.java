package com.compiler.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * checker util.
 *
 * @author LeoLu
 * @since 2021-03-08
 **/
public class FileChecker {

    public static final String POM_FILE = "pom.xml";
    public static final String GRADLE_FILE = "build.gradle";

    /**
     * get any builder properties.
     *
     * @param path path.
     * @return List<String>
     */
    public static List<String> getBuilderFiles(String path) {
        return Stream.concat(getMavenPoms(path).stream(), getGradleBuilders(path).stream())
                .distinct().collect(Collectors.toList());
    }

    /**
     * is maven : true or false
     *
     * @param path path
     * @return boolean
     */
    public static List<String> getMavenPoms(String path) {
        List<String> mavenPoms = new ArrayList<>();
        getAllFileName(path, mavenPoms, POM_FILE);
        return mavenPoms;
    }

    /**
     * is gradle : true or false
     *
     * @param path path
     * @return boolean
     */
    public static List<String> getGradleBuilders(String path) {
        List<String> mavenPoms = new ArrayList<>();
        getAllFileName(path, mavenPoms, GRADLE_FILE);
        return mavenPoms;
    }


    public static void getAllFileName(String path, List<String> fileNameList, String compilerName) {
        File file = new File(path);
        File[] tempList = file.listFiles();

        assert tempList != null;
        for (File value : tempList) {
            if (value.isFile() && value.getName().equalsIgnoreCase(compilerName)) {
                fileNameList.add(value.getPath());
            }
            if (value.isDirectory()) {
                getAllFileName(value.getAbsolutePath(), fileNameList, compilerName);
            }
        }
    }
}
