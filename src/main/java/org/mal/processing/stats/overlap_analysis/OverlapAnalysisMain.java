package org.mal.processing.stats.overlap_analysis;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.projectstructure.Improvement;
import org.mal.projectstructure.JavaProject;
import org.mal.utils.FetchProjectsAndData;
import org.mal.utils.FileIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OverlapAnalysisMain {
    public static void main(String[] args){

        OverlapResult results = new OverlapResult();
        for (JavaProject project: FetchProjectsAndData.getAllProjectData()) {
            OverlapResult r1;
            if (args.length>0 && args[0].equals("single")){
                r1 = OverlapAnalysisSingleQuery.run(project.getProjectName());
            }
            else{
                r1 = OverlapAnalysisAllQueries.run(project);
            }
            results = OverlapResult.addInPlace(results, r1);
        }
        System.out.println("Total Methods="+results.numMethods);
        System.out.println("Total conflicts="+results.numOverlaps);
        System.out.println("Total suggestions="+ results.numSuggestions);

        writeOverlapResults(results);
        
    }

    private static JSONArray getImprovementsArrFromCluster(List<Improvement> cluster){
        JSONArray arr = new JSONArray();
        cluster.forEach(i->arr.put(i.toJsonObject()));
        return arr;
    }

    public static void writeOverlapResults(OverlapResult results){

        JSONObject writeData = new JSONObject()
                .put("numMethods", results.numMethods)
                .put("numOverlaps", results.numOverlaps)
                .put("numSuggestions", results.numSuggestions);

        JSONArray overlapData = new JSONArray().put(
                results.overlaps
                .stream()
                .map(x->new JSONObject()
                        .put("cluster1", getImprovementsArrFromCluster(x.getLeft()))
                        .put("cluster2", getImprovementsArrFromCluster(x.getRight()))
                ).toList()
        );
        writeData.put("overlaps", overlapData);

        try {
            FileIO.writeJSONObjectToFile(writeData, Configurations.OVERLAP_RESULTS,
                    "results.json", true);
        } catch (IOException e){
            System.out.println("Failed to write to file");
        }
    }

    public static Boolean isOverlaping(Integer s1, Integer e1,
                                       Integer s2, Integer e2){
        return (s1 <= e2 && s1>=s2) || (s2 >=s1 && s2<=e1);
    }

    public static Boolean isOverlaping(Improvement i1, Improvement i2){
        return isOverlaping(i1.getStart(), i1.getStop(), i2.getStart(), i2.getStop());
    }

    private static boolean isSimilar(Improvement i1, Improvement i2){
        return isOverlaping(i1, i2)
                && i1.getRefactoringClass().equals(i2.getRefactoringClass())
                && i1.getTwoRemovedDiffLines().equals(i2.getTwoRemovedDiffLines());
    }



    private static List<List<Improvement>> clusterImprovements(List<Improvement> improvements){
        List<List<Improvement>> similarImprovements = new ArrayList<>();
        for(Improvement i: improvements){
            List<Improvement> whichCluster = null;
            List<List<Improvement>> possibleImprovements =
                    similarImprovements.stream()
                    .filter(
                            cluster-> cluster.stream()
                                    .filter(j -> isSimilar(i, j))
                                    .toList().size() >= 1
//                                    (0.5 * cluster.size())
                    ).toList();
            if (possibleImprovements.size()==1)
                whichCluster = possibleImprovements.get(0);
            else if (possibleImprovements.isEmpty()) {
                whichCluster = new ArrayList<>();
                similarImprovements.add(whichCluster);
            }else{
                System.out.println("More than 1 cluster!!");
                whichCluster = possibleImprovements.get(0);
            }
            whichCluster.add(i);
        }

        return similarImprovements;

    }

    private static boolean clusterClash(List<Improvement> cluster1,
                                        List<Improvement> cluster2){
        return true;
    }

    public static OverlapResult findOverlaps(List<Improvement> improvements){
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
            List<List<Improvement>> similarImprovements = clusterImprovements(imps);


            result.numSuggestions += similarImprovements.size();
            for(int i=0; i< similarImprovements.size(); i++){
                for(int j=i+1; j< similarImprovements.size(); j++){
                    if (i==j)
                        continue;

                    List<Improvement> cluster1 = similarImprovements.get(i);
                    List<Improvement> cluster2 = similarImprovements.get(j);
                    if (clusterClash(cluster1, cluster2)){
                        result.numOverlaps += 1;
                        result.addOverlap(cluster1, cluster2);
                    }

                }
            }
        }

        System.out.println("Total Methods="+result.numMethods);
        System.out.println("Total conflicts="+result.numOverlaps);
        System.out.println("Total suggestions="+ result.numSuggestions);

        return result;

    }


}
