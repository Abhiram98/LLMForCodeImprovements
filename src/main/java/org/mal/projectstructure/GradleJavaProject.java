package org.mal.projectstructure;

import org.mal.apply.BuildCommandResult;
import org.mal.apply.GradleCommandRunner;

import java.io.File;

public class GradleJavaProject extends JavaProject {
    public GradleJavaProject(String projectName, String projectPath, String buildSystem, String buildCommand, String javaHome) throws IllegalArgumentException {
        super(projectName, projectPath, buildSystem, buildCommand, javaHome);

        assert buildSystem.equals("gradle");
    }

    @Override
    public BuildCommandResult build(){
        GradleCommandRunner.gradleClean(new File(projectPath + "/"), javaHome);
        return GradleCommandRunner.gradleCompile(new File(projectPath + "/"), javaHome);
    }

    @Override
    public BuildCommandResult clean(){
        return GradleCommandRunner.gradleClean(new File(projectPath + "/"), javaHome);
    }
}
