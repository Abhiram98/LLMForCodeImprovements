package org.mal.apply;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.FileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mal.FileIO.*;

public class ApplyChanges {
    int numberOfUnsuccessfulCompiles = 0;
    int totalParsableImprovementFiles = 0;
    int totalUnParsableImprovementFiles = 0;

    public static String replaceContent(String originalString, int start, int end, String newContent) {
        StringBuilder sb = new StringBuilder(originalString);
        return sb.delete(start, end).insert(start, newContent).toString();
    }

    public void processImprovements() throws Exception {

        try {
            JSONObject selectedProjects = FileIO.readJSONObjectFromFile(Configurations.DATA_FOLDER,
                    Configurations.PROJECT_META_FILE);
            for(String key: selectedProjects.keySet()){
                JSONObject obj = (JSONObject) selectedProjects.get(key);
                String projectName = ((JSONObject) obj.get("github")).get("repository").toString();
                String projectPath = Configurations.PROJECT_REPOSITORY + projectName;
                String sourcePath = ((JSONObject)((JSONObject)
                                        obj.get("systems"))
                                            .get("build_info"))
                                            .get("basepath").toString();
                String buildCommand = "";
                String cleanCommand = "";
                if (new File(Configurations.DATA_FOLDER+ "applied-"+ projectName).exists()){
                    System.out.println("already applied");
                    continue;
                }
                try {
                     buildCommand = ((JSONObject) ((JSONObject)
                            obj.get("systems"))
                            .get("build_info"))
                            .get("command").toString();
                } catch (JSONException e){
                    System.out.println("Failed to find specific command");
                }
                try {
                    cleanCommand = ((JSONObject) ((JSONObject)
                            obj.get("systems"))
                            .get("build_info"))
                            .get("clean_command").toString();
                } catch (JSONException e) {
                    System.out.println("Failed to find specific command");
                }

                    String javaVersion = ((JSONObject)((JSONObject)
                        obj.get("systems"))
                        .get("build_info"))
                        .get("java_version").toString();

                JSONObject buildTypes = (JSONObject)((JSONObject) obj.get("systems")).get("build_types");
                String buildSystem = "";
                for (String buildKey: buildTypes.keySet()){
                    if (buildTypes.get(buildKey).toString().equals("true")){
                        buildSystem = buildKey;
                    }
                }

                JavaProject jp = JavaProject.createProject(projectName, projectPath, sourcePath,
                                    buildSystem, buildCommand, cleanCommand, javaVersion);
                incoperateChangesAndCompile(jp);
                FileIO.writeLinesToFile(Configurations.DATA_FOLDER+ "applied-"+ projectName,
                        List.of("Done"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Final Status:");
        System.out.println("totalParsableImprovementFiles="+totalParsableImprovementFiles);
        System.out.println("totalUnParsableImprovementFiles="+totalUnParsableImprovementFiles);
        System.out.println("numberOfUnsuccessfulCompiles="+numberOfUnsuccessfulCompiles);
    }

    private void incoperateChangesAndCompile(JavaProject project) throws Exception {
        Path impromentsPath = Paths.get(Configurations.IMPROVEMENTS, project.projectName);
        if (!Files.exists(impromentsPath) || !Files.isDirectory(impromentsPath)) {
            throw new FileNotFoundException("Improvements cannot be found for " + project.projectName);
        }
//        int numberOfUnsuccessfulCompiles = 0;
//        int totalParsableImprovementFiles = 0;
//        int totalUnParsableImprovementFiles = 0;
        List<Path> jsons = FileIO.getAllFilesInDirectory(
                Path.of(Configurations.IMPROVEMENTS + "/" + project.projectName + "/"), "");

        for (Path json : jsons) {
            System.out.println(json);
            if (json.getFileName().toString().matches(Configurations.OUTPUT_REGEX)) {
                if (!validJSON(json)) {
                    totalUnParsableImprovementFiles += 1;
                    continue;
                }

                totalParsableImprovementFiles += 1;
                if (prepareProjectForImprovements(project)) {
                    Improvement improvement;
                    try {
                        improvement = getImprovement(json);
                    } catch (Exception e){
                        continue;
                    }
                    Path path = Path.of(Configurations.PROJECT_REPOSITORY + improvement.getFilePath());
                    Path backupPath = path.resolveSibling(path.getFileName() + ".bak");
                    backupFile(path, backupPath);
                    String oldFileContent = FileIO.readStringFromFile(Configurations.PROJECT_REPOSITORY +
                            improvement.getFilePath());
                    String newFileContent = replaceContent(oldFileContent, improvement.getStart(), improvement.getStop(), improvement.getImprovedCode());
                    // Apply improvement
                    modifyFileContent(path, newFileContent);  // add the changed method
//                    MavenCommandResult result = compileProject(projectName);
                    BuildCommandResult result = project.build();

                    if (result.getExitCode() != 0) {
                        FileIO.writeLinesToFile(Configurations.COMPILE_ERRORS + project.projectName + "/" + json.getFileName() + ".txt", result.getOutput());
                        numberOfUnsuccessfulCompiles += 1;
                    }
                    restoreFile(backupPath, path);  // revert the change
                }
                System.out.println("Total parsable improvement files: " + totalParsableImprovementFiles);
                System.out.println("Total unparsable improvement files: " + totalUnParsableImprovementFiles);
                System.out.println("Total unsuccessful compiles: " + numberOfUnsuccessfulCompiles);
            }

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
        File projectDirectory = new File(Configurations.PROJECT_REPOSITORY + project.projectName + "/");

        // Ensure the project directory exists and is a directory
        if (!projectDirectory.exists() || !projectDirectory.isDirectory()) {
            throw new MavenOperationException("Project directory does not exist or is not a directory: " + projectDirectory.getPath());
        }

        // Attempt to clean the project
//        BuildCommandResult cleanResult = MavenCommandRunner.mvnClean(projectDirectory);
        BuildCommandResult cleanResult = project.clean();
        if (cleanResult.getExitCode() != 0) {
            // Including more specific details from Maven output can be helpful for debugging
            throw new MavenOperationException("Failed to clean the project: " + project.projectName + ". Error: " + cleanResult.getOutput());
        }

        // Attempt to compile the project
//        BuildCommandResult compileResult = MavenCommandRunner.mvnCompile(projectDirectory);
        BuildCommandResult compileResult = project.build();
        if (compileResult.getExitCode() != 0) {
            throw new MavenOperationException("Failed to compile the project: " + project.projectName + ". Error: " + compileResult.getOutput());
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
        return new Improvement(improvedCode.toString(), startC, startE, filePath);
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
