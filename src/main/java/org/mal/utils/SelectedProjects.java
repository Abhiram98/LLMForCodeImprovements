package org.mal.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.FileIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SelectedProjects {
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
}
