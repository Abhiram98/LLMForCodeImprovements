package org.mal.projectstructure;

import org.mal.apply.BuildCommandResult;
import org.mal.apply.MavenCommandRunner;

import java.io.File;

public class MavenJavaProject extends JavaProject {
    public MavenJavaProject(String projectName, String projectPath, String buildSystem, String buildCommand, String javaHome) throws IllegalArgumentException {
        super(projectName, projectPath, buildSystem, buildCommand, javaHome);
        assert buildSystem.equals("maven");
    }

    @Override
    public BuildCommandResult build(){
        MavenCommandRunner.mvnClean(new File(projectPath + "/"), javaHome);
        return MavenCommandRunner.mvnCompile(new File(projectPath + "/"), javaHome);
    }

    @Override
    public BuildCommandResult clean(){
        return MavenCommandRunner.mvnClean(new File(projectPath + "/"), javaHome);
    }

}
