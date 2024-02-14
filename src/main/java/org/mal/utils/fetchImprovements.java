package org.mal.utils;

import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.FileIO;
import org.mal.apply.Improvement;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class fetchImprovements {

    public static String improvementFileRegex = ".*improvements-\\d+-\\d+.json";


    public static List<Improvement> getImprovementsFromFile(JSONObject fileObj){
        List<Improvement> imps = new ArrayList<>();
        JSONObject methodImprovements = fileObj.getJSONObject("Method_Improvements");
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
                            jobj.getString("Description")
                    );
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

    public static List<Improvement> getAll(String projectName){
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
    public static List<Improvement> fetch(){
        List<String> projectNames = SelectedProjects.getProjectNames();
        List<Improvement> allImprovements = new ArrayList<>();
        for (String pname: projectNames){
                List<Improvement>imps = getAll(pname);
                allImprovements.addAll(imps);
        }
        return allImprovements;
    }

    public static void main(String [] args){
        System.out.println(getAll("blitz4j"));
        System.out.println("done");
    }
}
