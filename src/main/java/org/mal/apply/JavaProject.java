package org.mal.apply;

import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.FileIO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class JavaProject {
    String projectPath = "";
    String projectName = "";
    String buildCommand = "";
    String javaHome = "";
    String cleanCommand = "";


    public static String findJavaHome (String javaVersion)  throws IOException {

        JSONObject obj = FileIO.readJSONObjectFromFile(Path.of(Configurations.JAVA_HOME_RESOLVER));
        return obj.get(javaVersion).toString();
    }

    public static JavaProject createProject(String projectName, String projectPath,
                                String sourcePath, String buildSystem,
                                String buildCommand, String cleanCommand, String javaVersion){


        String relSourcePath = projectPath;
        if (!sourcePath.equals("")){
            relSourcePath = relSourcePath + "/" + sourcePath;
        }
        String javaHome = "";
        try {
            javaHome = JavaProject.findJavaHome(javaVersion);
        } catch (IOException e){
            System.out.println("Couldn't find java home path resolver.");
        }

        if (!buildCommand.equals("")){
          JavaProject jp = new JavaProject(projectName, projectPath, "custom",
                  buildCommand, javaHome);
          jp.cleanCommand = cleanCommand;
          return jp;
        } else if (buildSystem.equals("maven")){
            return new MavenJavaProject(projectName, relSourcePath,
                    buildSystem, buildCommand, javaHome);
        } else if (buildSystem.equals("gradle")) {
            return new GradleJavaProject(projectName, relSourcePath,
                    buildSystem, buildCommand, javaHome);
        } else{
            throw new IllegalArgumentException("Unkown build system");
        }
    }


    public JavaProject(String projectName, String projectPath, String buildSystem,
                       String buildCommand, String javaHome)
            throws IllegalArgumentException {
        this.projectPath = projectPath;
        this.projectName = projectName;
        this.buildCommand = buildCommand;
        this.javaHome = javaHome;

//        if (buildSystem.equals("maven")){
//            bs = new MavenBuildSystem(javaVersion);
//        } else if (buildSystem.equals("gradle")) {
//            bs = new GradleBuildSystem(javaVersion);
//        } else{
//            throw new IllegalArgumentException("Unkown build system");
//        }
    }

    public BuildCommandResult clean(){
        return CommandRunner.runCommand(cleanCommand, new File(projectPath+"/"), javaHome);
    }

    public BuildCommandResult build(){
        return CommandRunner.runCommand(buildCommand, new File(projectPath+"/"), javaHome);
    }
}