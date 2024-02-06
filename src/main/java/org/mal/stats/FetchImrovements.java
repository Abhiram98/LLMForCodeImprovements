package org.mal.stats;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.FileIO;
import org.mal.utils.SelectedProjects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FetchImrovements {

    public static void main(String[] args){
        List<String> allImprovementsRaw = new ArrayList<>();

        List<String> projectNames = SelectedProjects.getProjectNames();
        for (String pName: projectNames){
            List<String> imps = getImprovements(pName);
            allImprovementsRaw.addAll(imps);
        }
        System.out.println(allImprovementsRaw);
        System.out.println("len(allImprovementsRaw)="+allImprovementsRaw.size());
        try {

            FileIO.writeLinesToFile(
                    Configurations.IMPROVEMENTS + "improvements.txt", allImprovementsRaw);
        } catch (IOException e){
            System.out.println("Failed to write to improvements.txt");
        }
    }

    public static List<String> getImprovements(String projectName){
        JSONArray allImprovements = null;
        List<String> allImprovementsStr = null;
        try {
            allImprovements =
                    FileIO.readJSONArrayFromFile(Configurations.IMPROVEMENTS+ projectName,
                    "improvements.json");
        } catch (IOException e){
            System.out.println("Couldn't read file");
            return allImprovementsStr;
        }
        allImprovementsStr = new ArrayList<>();
        for (Object obj : allImprovements){
            JSONObject impObj = (JSONObject) obj;
            try {
                JSONArray imps = impObj.getJSONArray("Method_Improvements")
                        .getJSONObject(0)
                        .getJSONArray("Improvements");
                for (Object i : imps) {
                    try {
                        JSONObject ji = (JSONObject) i;
                        allImprovementsStr.add(ji.getString("Improvement"));
                    } catch (Exception e){
                        continue;
                    }
                }
            } catch (Exception e){
                continue;
            }
        }

        return allImprovementsStr;

    }

}
