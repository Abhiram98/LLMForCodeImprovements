package org.mal.stats;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.FileIO;
import org.mal.apply.Improvement;
import org.mal.utils.FetchImprovements;
import org.mal.utils.SelectedProjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        List<Improvement> improvements=null;
        try {
            improvements = FetchImprovements.getAll(projectName);
        } catch (Exception e){
//            System.out.println("Couldn't find file");
        }
        System.out.println(improvements.size());

        OverlapResult result = new OverlapResult();
        Set<String> methodAndFile = new HashSet<>(
                improvements.stream()
                        .map(improvement -> improvement.getFilePath()+";"+improvement.getMethodName())
                        .toList()
                        );
        for (String mfname: methodAndFile){
            result.numMethods+=1;

            String[] splitNames = mfname.split(";");
            String fileName = splitNames[0];
            String methodName = splitNames[1];


            List<Improvement> imps = improvements.stream()
                    .filter(x->x.getFilePath().equals(fileName)
                            && x.getMethodName().equals(methodName))
                    .toList();
            List<Improvement> impsDeduped = new ArrayList<>();
            for(Improvement i: imps){
                if (impsDeduped.stream()
                        .filter(x -> x.getStart() == i.getStart() && x.getStop() == i.getStop())
                        .toList().isEmpty()){
                    impsDeduped.add(i);
                }
            }


            result.numSuggestions += impsDeduped.size();
            for(int i=0; i< impsDeduped.size(); i++){
                for(int j=i+1; j< impsDeduped.size(); j++){
                    if (i==j)
                        continue;

                    Integer s1 = impsDeduped.get(i).getStart();
                    Integer e1 = impsDeduped.get(i).getStop();

                    Integer s2 = impsDeduped.get(j).getStart();
                    Integer e2 = impsDeduped.get(j).getStop();

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
