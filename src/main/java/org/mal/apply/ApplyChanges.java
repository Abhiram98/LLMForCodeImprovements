package org.mal.apply;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.projectstructure.Improvement;
import org.mal.projectstructure.JavaProject;
import org.mal.utils.FetchProjectsAndData;
import org.mal.utils.FileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mal.utils.FileIO.*;

public class ApplyChanges {
    int numberOfUnsuccessfulCompiles = 0;
    int totalParsableImprovementFiles = 0;
    int totalUnParsableImprovementFiles = 0;

    public static String replaceContent(String originalString, int start, int end, String newContent) {
        StringBuilder sb = new StringBuilder(originalString);
        return sb.delete(start, end).insert(start, newContent).toString();
    }

    public void processImprovements() throws Exception {

        List<JavaProject> allProjects = FetchProjectsAndData.getAllProjectData();
        for(JavaProject jp: allProjects){
            incoperateChangesAndCompile(jp);
        }

        System.out.println("Final Status:");
        System.out.println("totalParsableImprovementFiles="+totalParsableImprovementFiles);
        System.out.println("totalUnParsableImprovementFiles="+totalUnParsableImprovementFiles);
        System.out.println("numberOfUnsuccessfulCompiles="+numberOfUnsuccessfulCompiles);
    }

    private void incoperateChangesAndCompile(JavaProject project) throws Exception {
        if (prepareProjectForImprovements(project)) {
            for (Improvement imp : project.getAllImprovements()) {
//            if (!validJSON(json)) {
//                totalUnParsableImprovementFiles += 1;
//                continue;
//            }
                project.clean();
                totalParsableImprovementFiles += 1;

                Path path = Path.of(Configurations.PROJECT_REPOSITORY + imp.getFilePath());
                Path backupPath = path.resolveSibling(path.getFileName() + ".bak");
                backupFile(path, backupPath);
                String oldFileContent = FileIO.readStringFromFile(Configurations.PROJECT_REPOSITORY +
                        imp.getFilePath());
                String newFileContent = replaceContent(
                        oldFileContent,
                        imp.getForMethod().getStartLine(),
                        imp.getForMethod().getEndLine(),
                        imp.getImprovedMethod().getImprovementFromLLM());
                // Apply improvement
                modifyFileContent(path, newFileContent);  // add the changed method
//                    MavenCommandResult result = compileProject(projectName);
                BuildCommandResult result = project.build();
                int count = 0;
                if (result.getExitCode() != 0) {
                    FileIO.writeLinesToFile(Configurations.COMPILE_ERRORS + project.getProjectName() + "/improvements-" + count + ".txt", result.getOutput());
                    numberOfUnsuccessfulCompiles += 1;
                }
                restoreFile(backupPath, path);  // revert the change
            }
            System.out.println("Total parsable improvement files: " + totalParsableImprovementFiles);
            System.out.println("Total unparsable improvement files: " + totalUnParsableImprovementFiles);
            System.out.println("Total unsuccessful compiles: " + numberOfUnsuccessfulCompiles);
        }

    }

    private static BuildCommandResult compileProject(String projectName) {
        MavenCommandRunner.mvnClean(
                new File(Configurations.PROJECT_REPOSITORY + projectName + "/"),
                "default");
        return MavenCommandRunner.mvnCompile(
                new File(Configurations.PROJECT_REPOSITORY + projectName + "/"),
                "default");
    }

    private boolean prepareProjectForImprovements(JavaProject project) throws MavenOperationException {
        File projectDirectory = new File(Configurations.PROJECT_REPOSITORY + project.getProjectName() + "/");

        // Ensure the project directory exists and is a directory
        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            throw new MavenOperationException("Project directory does not exist or is not a directory: " + projectDirectory.getPath());
        }

        // Attempt to clean the project
//        BuildCommandResult cleanResult = MavenCommandRunner.mvnClean(projectDirectory);
        BuildCommandResult cleanResult = project.clean();
        if (cleanResult.getExitCode() != 0) {
            // Including more specific details from Maven output can be helpful for debugging
            throw new MavenOperationException("Failed to clean the project: " + project.getProjectName() + ". Error: " + cleanResult.getOutput());
        }

        // Attempt to compile the project
//        BuildCommandResult compileResult = MavenCommandRunner.mvnCompile(projectDirectory);
        BuildCommandResult compileResult = project.build();
        if (compileResult.getExitCode() != 0) {
            throw new MavenOperationException("Failed to compile the project: " + project.getProjectName() + ". Error: " + compileResult.getOutput());
        }

        return true; // Project is cleaned and compiled successfully
    }

    private boolean validJSON(Path json) throws IOException {
        JSONObject object = FileIO.readJSONObjectFromFile(json);
        JSONArray ims = object.getJSONArray("Method_Improvements");
        for (Object o : ims) {
            JSONObject im = (JSONObject) o;
            if (im.keySet().contains("error")) {
                if (im.getString("error").equals("An unexpected error occurred")) {
                    return false;
                }
            }
        }
        return true;
    }

    private Improvement getImprovement(Path json) throws IOException {
        JSONObject object = FileIO.readJSONObjectFromFile(json);
        String filePath = (String) object.get("File_Path");
        Integer startC = (Integer) object.get("Start");
        Integer startE = (Integer) object.get("Stop");
        JSONArray arrayImv = (JSONArray) object.get("Method_Improvements");
        StringBuilder improvedCode = new StringBuilder();
        for (Object o : arrayImv) {
            JSONObject ob = (JSONObject) o;
            improvedCode.append(ob.getString("Final code"));
        }
        return new Improvement(improvedCode.toString(), startC, startE, filePath,
                "", "", "");
    }

    private void improveFile(String filePath, String newContent) {
        Path path = Path.of(filePath);
        Path backupPath = path.resolveSibling(path.getFileName() + ".bak");
    }

    // Custom exception class for Maven operation failures
    class MavenOperationException extends Exception {
        public MavenOperationException(String message) {
            super(message);
        }
    }
}
