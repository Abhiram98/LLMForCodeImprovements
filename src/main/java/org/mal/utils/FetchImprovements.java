package org.mal.utils;

import io.vavr.Tuple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.projectstructure.ImprovedMethod;
import org.mal.projectstructure.Improvement;
import org.mal.projectstructure.JavaMethod;
import org.mal.projectstructure.JavaProject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class FetchImprovements {

    public static String improvementFileRegex = ".*improvements-\\d+-\\d+.json";
    private static HashMap<String, List<Improvement>> improvementsHash = new HashMap<>();

    private static JSONArray improvementClasses = null;


    public static List<Improvement> getImprovementsFromMethod(JavaMethod method){
        String hashStr = method.getJavaProject().getProjectName();
        List<Improvement> projectImps;
        projectImps = improvementsHash.get(hashStr);
        if (projectImps==null){
         projectImps = getAllImprovements(method.getJavaProject().getProjectName());
         improvementsHash.put(hashStr, projectImps);
        }

        Set<ImprovedMethod> improvedMethods = new HashSet<>(projectImps.stream().map(Improvement::getImprovedMethod).toList());
        method.setFinalImprovedMethods(improvedMethods.stream().toList());


        List<Improvement> methodImps = projectImps.stream()
                .filter(x-> Objects.equals(x.getMethodName(), method.getMethodName()))
                .filter(x-> Objects.equals(x.getFilePath(), method.getFilePath()))
                .toList();

        methodImps.forEach(i->i.setForMethod(method));
        improvedMethods.forEach(i->i.setForMethod(method));

        return methodImps;
    }

    public static void setImprovementClass(Improvement i){

            for(Object obj: improvementClasses){
                JSONObject jsonObject = (JSONObject) obj;
                if(jsonObject.getString("methodName").equals(i.getMethodName()) &&
                    jsonObject.getString("filePath").equals(i.getFilePath()) &&
                    jsonObject.getString("longDescription").equals(i.getExplanationLong()) &&
                    jsonObject.get("shortDescription").equals(i.getExplanationShort())){
                    i.setRefactoringClass(jsonObject.getString("class"));
                }
            }
    }
    public static List<Improvement> getImprovementsFromFile(JSONObject fileObj){
        List<Improvement> imps = new ArrayList<>();
        JSONObject methodImprovements = fileObj.getJSONObject("Method_Improvements");
        ImprovedMethod improvedMethod = getFinalImprovementsFromFile(fileObj);
        try {
            for (Object obj : methodImprovements.getJSONArray("Improvements")) {
                JSONObject jobj = (JSONObject) obj;
                try{
                    Improvement i = new Improvement(
                            jobj.getString("Change_Diff"),
                            jobj.getInt("Start"),
                            jobj.getInt("End"),
                            fileObj.getString("File_Path"),
                            jobj.getString("Improvement"),
                            jobj.getString("Description"),
                            fileObj.getString("Method_Name")
                    );
                    setImprovementClass(i);

                    i.setImprovedMethod(improvedMethod);
                    //Make sure improvements are unique.
                    List<Improvement> matches = imps.stream()
                            .filter(item -> item.getStart()==i.getStart())
                            .filter(item -> item.getStop()==i.getStop())
                            .toList();

                    if (matches.isEmpty()){
                        imps.add(i);
                    }
                } catch (Exception e){}
            }
        } catch (Exception e){}
        return imps;
    }


    private static void loadImprovementClasses(){
        if (improvementClasses!=null)
            return;
        try {
            improvementClasses = FileIO.readJSONArrayFromFile(Configurations.IMPROVEMENTS, "improvement_class.json");
        } catch (IOException e){
            System.out.println("Failed to read improvement classes");
        }
    }

    public static List<Improvement> getAllImprovements(String projectName){

        loadImprovementClasses();

        List<Path> allImprovements = FileIO.getAllFilesInDirectory(
                Path.of(Configurations.IMPROVEMENTS+projectName), ".json");

        List<Path> filesFiltered = allImprovements.stream()
                                    .filter(x->x.toString().matches(improvementFileRegex))
                                    .toList();
        List<Improvement> allImps = new ArrayList<>();
        for (Path p: filesFiltered){
            try {
                JSONObject obj = FileIO.readJSONObjectFromFile(p);
                List<Improvement> newImps = getImprovementsFromFile(obj);

                allImps.addAll(newImps);
            } catch (IOException e){
                System.out.println("Can't find file.");
            }
        }
        return allImps;
    }


    private static ImprovedMethod getFinalImprovementsFromFile(JSONObject fileObj) {
        JSONObject methodImprovements = fileObj.getJSONObject("Method_Improvements");
        try {
            ImprovedMethod improvedMethod = new ImprovedMethod(
                    methodImprovements.getString("Final code")
            );
            return improvedMethod;
        } catch (JSONException e){
            return null;
        }
    }

    public static List<Improvement> getOne(String projectName,
                                           Integer methodNum, Integer improvementNumber)
            throws IOException
    {
        List<Improvement> allImps;
        Path p = Path.of(Configurations.IMPROVEMENTS+projectName+
                "/improvements-"+methodNum+"-"+improvementNumber+".json");
        JSONObject obj = FileIO.readJSONObjectFromFile(p);
        allImps = getImprovementsFromFile(obj);
        return allImps;
    }
    public static List<Improvement> fetch(){
        List<String> projectNames = FetchProjectsAndData.getProjectNames();
        List<Improvement> allImprovements = new ArrayList<>();
        for (String pname: projectNames){
                List<Improvement>imps = getAllImprovements(pname);
                allImprovements.addAll(imps);
        }
        return allImprovements;
    }

    public static void main(String [] args){
        System.out.println(getAllImprovements("blitz4j"));
        System.out.println("done");
    }
}
