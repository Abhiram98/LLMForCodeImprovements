package org.mal.stats;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.FileIO;
import org.mal.utils.SelectedProjects;

public class OverlapAnalysisMain {

    public static void main(String[] args){

        OverlapResult results = new OverlapResult();
        for (String projectName: SelectedProjects.getProjectNames()) {
            OverlapResult r1 = OverlapAnalysisMain.run(projectName);
            results.numOverlaps += r1.numOverlaps;
            results.numSuggestions += r1.numSuggestions;
            results.numMethods += r1.numMethods;
        }
        System.out.println("Total Methods="+results.numMethods);
        System.out.println("Total conflicts="+results.numOverlaps);
        System.out.println("Total suggestions="+ results.numSuggestions);
        
    }

    public static Boolean isOverlaping(Integer s1, Integer e1,
                                       Integer s2, Integer e2){
        return (s1 <= e2 && s1>=s2) || (s2 >=s1 && s2<=e1);
    }

    public static OverlapResult run(String projectName){

        JSONArray improvements=null;
        try {
            improvements = FileIO.readJSONArrayFromFile(
                    Configurations.IMPROVEMENTS + projectName, "improvements.json");
        } catch (Exception e){
//            System.out.println("Couldn't find file");
        }
        System.out.println(improvements.length());

        OverlapResult result = new OverlapResult();
        for (Object obj: improvements){
            JSONArray imps;
            JSONObject methodWise = (JSONObject) obj;
            try {
                imps = methodWise
                        .getJSONArray("Method_Improvements")
                        .getJSONObject(0)
                        .getJSONArray("Improvements");
            } catch (Exception e){
                System.out.println("Couldn't read improvements");
                continue;
            }
            result.numMethods+=1;
            result.numSuggestions += imps.length();

            for(int i=0; i< imps.length(); i++){
                for(int j=i+1; j< imps.length(); j++){
                    if (i==j)
                        continue;

                    Integer s1 = imps.getJSONObject(i).getInt("Start");
                    Integer e1 = imps.getJSONObject(i).getInt("End");

                    Integer s2 = imps.getJSONObject(j).getInt("Start");
                    Integer e2 = imps.getJSONObject(j).getInt("End");

                    if (isOverlaping(s1, e1, s2, e2))
                        result.numOverlaps+=1;
                }
            }
        }

        System.out.println("Total Methods="+result.numMethods);
        System.out.println("Total conflicts="+result.numOverlaps);
        System.out.println("Total suggestions="+ result.numSuggestions);

        return result;
    }
}
