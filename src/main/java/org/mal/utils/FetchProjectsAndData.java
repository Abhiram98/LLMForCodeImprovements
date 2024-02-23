package org.mal.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.projectstructure.JavaMethod;
import org.mal.projectstructure.JavaProject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FetchProjectsAndData {
    public static List<String> getProjectNames(){
        JSONObject selectedProjects;
        try {
            selectedProjects = FileIO.readJSONObjectFromFile(
                    Configurations.DATA_FOLDER, Configurations.PROJECT_META_FILE);
        } catch (IOException e){
            System.out.println("Couldn't find file");
            return null;
        }

        List<String> projectNames = new ArrayList<>();
        for(String key: selectedProjects.keySet()){
            projectNames.add(selectedProjects.getJSONObject(key).
                    getJSONObject("github").getString("repository"));
        }
        return projectNames;

    }
    public static Integer getTotalMethodCount(){
        List<String> projectNames = getProjectNames();
        int count = 0;
        assert projectNames != null;
        for(String pname: projectNames){
            try {
                JSONArray arr = FileIO.readJSONArrayFromFile(
                        Configurations.DATA_FOLDER,
                        "selected_methods_" + pname + ".json");
                count += arr.length();

            }catch (IOException e){
                System.out.println("Can't find "+pname);
            }

        }
        return count;
    }


    public static List<JavaMethod> getAllMethodData(JavaProject javaProject){
        List<JavaMethod> allMethods = new ArrayList<>();

        try{
            JSONArray methods = FileIO.readJSONArrayFromFile(
                    Configurations.DATA_FOLDER,
                    "selected_methods_" + javaProject.getProjectName() + ".json");
            for(Object obj: methods){
                JSONObject jsonObj = (JSONObject) obj;

                JavaMethod method = new JavaMethod(
                        jsonObj.getString("Method_Name"),
                        jsonObj.getString("File_Path"),
                        jsonObj.getString("Old_Method"),
                        jsonObj.getInt("Start"),
                        jsonObj.getInt("Stop"),
                        javaProject
                );
                method.setImprovementList(
                        FetchImprovements.getImprovementsFromMethod(method)
                );
                allMethods.add(method);
            }


        } catch (IOException e){
            return null;
        }
        return allMethods;
    }

    public static List<JavaProject> getAllProjectData(){
        List<String> projectNames = getProjectNames();
        List<JavaProject> allProjects= new ArrayList<>();

        assert projectNames != null;
        for (String pname: projectNames){
            JavaProject jp = getJavaProjectFromName(pname);
            if (jp == null) {
                continue;
            }
            allProjects.add(jp);
            List<JavaMethod> methods = getAllMethodData(jp);
            if (methods!=null){
                jp.setMethods(methods);
            }
        }

        return allProjects;

    }

    public static JavaProject getJavaProjectFromName(String projectName){
        JSONObject selectedProjects;
        try {
            selectedProjects = FileIO.readJSONObjectFromFile(Configurations.DATA_FOLDER,
                    Configurations.PROJECT_META_FILE);
        } catch (IOException e){
            System.out.println("Couldn't find file");
            return null;
        }

        List<String> foundProject =
                selectedProjects.keySet()
                .stream()
//                .map(x->x.split("##")[1])
                .filter(x->x.split("##")[1].equals(projectName))
                .toList();

        if(foundProject.isEmpty()){
            return null;
        }
        else if (foundProject.size()>1){
            System.out.println("More than one project found.");
            System.out.println(foundProject);
        }
        String key = foundProject.get(0);
        JSONObject obj = (JSONObject) selectedProjects.get(key);
        String projectPath = Configurations.PROJECT_REPOSITORY + projectName;
        String sourcePath = ((JSONObject) ((JSONObject)
                obj.get("systems"))
                .get("build_info"))
                .get("basepath").toString();
        String buildCommand = "";
        String cleanCommand = "";
        try {
            buildCommand = ((JSONObject) ((JSONObject)
                    obj.get("systems"))
                    .get("build_info"))
                    .get("command").toString();
        } catch (JSONException e) {
//            System.out.println("Failed to find specific command");
        }
        try {
            cleanCommand = ((JSONObject) ((JSONObject)
                    obj.get("systems"))
                    .get("build_info"))
                    .get("clean_command").toString();
        } catch (JSONException e) {
//            System.out.println("Failed to find specific command");
        }
        String javaVersion = ((JSONObject) ((JSONObject)
                obj.get("systems"))
                .get("build_info"))
                .get("java_version").toString();
        JSONObject buildTypes = (JSONObject) ((JSONObject) obj.get("systems")).get("build_types");
        String buildSystem = "";
        for (String buildKey : buildTypes.keySet()) {
            if (buildTypes.get(buildKey).toString().equals("true")) {
                buildSystem = buildKey;
            }
        }

        return JavaProject.createProject(projectName, projectPath, sourcePath,
                buildSystem, buildCommand, cleanCommand, javaVersion);
    }
}
