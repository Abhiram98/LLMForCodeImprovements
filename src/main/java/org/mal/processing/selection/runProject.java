package org.mal.processing.selection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.utils.FileIO;
import org.mal.prompts.PromptForImprovements;
import org.mal.projectstructure.Improvement;
import org.mal.utils.LLMResponseHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class runProject {

    String projectName = "";
    Integer maxIterations = 1;
    Integer minIterations = 1;
    Integer maxChances = 2;

    public runProject(String projectName, Integer maxIterations, Integer minIterations){
        this.projectName = projectName;
        this.maxIterations = maxIterations;
        this.minIterations = minIterations;
    }

    private JSONObject runMethod(JSONObject method){
        try {
            String stringResponse = LLMResponseHandler.askLLM(
                    PromptForImprovements.getPrompt(method.get("Old_Method").toString()),
                    PromptForImprovements.SYSTEM_MSG,
                    0.7
            );
            return new JSONObject(stringResponse);
        } catch (Exception e){
            return new JSONObject("{\"response_failed\":true}");
        }
    }

    private List<Improvement> collectSelections(JSONObject methodImpResponse){
        List<Improvement> improvements = new ArrayList<>();
        try {
            for (Object o : methodImpResponse.getJSONArray("Improvements")) {
                JSONObject impObj = (JSONObject) o;
                try {
                    improvements.add(new Improvement(
                            impObj.getString("Change_Diff"),
                            impObj.getInt("Start"),
                            impObj.getInt("End"),
                            "not_important",
                            impObj.getString("Improvement"),
                            impObj.getString("Description"),
                            "not_important_4"
                    ));
                } catch (Exception e) { }
            }
        } catch (Exception e){}

        return improvements;
    }

    private List<Improvement> fetchImprovementsFromFile(String filePath){
        List<Improvement> improvements;
        try {
            JSONObject obj = FileIO.readJSONObjectFromFile(Path.of(filePath));
            return collectSelections(obj.getJSONObject("Method_Improvements"));
        } catch (Exception e){
            return new ArrayList<>();
        }
    }

    private String fetchFinalMethodFromResponse(JSONObject response){
        try {
            return response.getJSONObject("Method_Improvements").getString("Final code");
        } catch (Exception e){
            return "no response";
        }
    }
    private String fetchFinalMethodFromFile(String filePath){
        List<Improvement> improvements;
        try {
            JSONObject obj = FileIO.readJSONObjectFromFile(Path.of(filePath));
            return fetchFinalMethodFromResponse(obj);
        } catch (Exception e){
            return null;
        }
    }



    private void runIterations(JSONObject method, Integer methodNumber){

//        File f = new File(Configurations.IMPROVEMENTS + projectName + "/improvements-" + methodNumber + "-0.json");
//        if (f.exists()){
//            System.out.println("Already done!");
//            System.out.println("Method number="+methodNumber);
//            return;
//        }

        if (maxIterations==-1){
            maxIterations = Configurations.MAX_ITERATIONS;
        }
        Boolean reachedFixedPoint = false;
        Boolean newSuggestions = true;
        List<Improvement> allImprovements = new ArrayList<>();
        Integer chaces = maxChances;
        List<String> allMethodRewrites = new ArrayList<>();

        for(int iteration = 0; iteration< maxIterations; iteration++) {
            System.out.println(iteration+" iteration(s) complete.");
            newSuggestions = false;

            if (iteration>=minIterations-1 && chaces<=0) {
                System.out.println("No new improvements found.");
                reachedFixedPoint=true;
                break;
            }

            List<Improvement> newImprovements;
            String filePath = Configurations.IMPROVEMENTS + projectName +
                    "/improvements-" + methodNumber + "-"+iteration+".json";
            File f = new File(filePath);
            JSONObject response=null;
            if (f.exists()){
                newImprovements = fetchImprovementsFromFile(filePath);
                allMethodRewrites.add(fetchFinalMethodFromFile(filePath));
            }else {
                response = runMethod(method);
                newImprovements = collectSelections(response);
                allMethodRewrites.add(
                        fetchFinalMethodFromResponse(response));
            }
            for (Improvement imp: newImprovements){
                List<Improvement> matches = allImprovements.stream()
                        .filter(item -> item.getStart()==imp.getStart())
                        .filter(item -> item.getStop()==imp.getStop())
                        .toList();

                if (matches.isEmpty()){
                    allImprovements.add(imp);
                    newSuggestions=true;
                    chaces = maxChances;
                }
            }
            System.out.println("AllImprovements:\n"+allImprovements);

            if(!newSuggestions){
                chaces-=1;
            }

            // Write to file.
            try {
                if (response!=null) {
                    method.put("Method_Improvements", response);
                    FileIO.writeJSONObjectToFile(method,
                            Configurations.IMPROVEMENTS + "/" + projectName,
                            "improvements-" + methodNumber + "-" + iteration + ".json",
                            true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        method.put("Improvements", allImprovements.toString());

        method.put("Improvements", new JSONArray(
                allImprovements.stream()
                    .map(x-> new JSONObject()
                            .put("Improvement", x.getExplanationShort())
                            .put("Change_Diff", x.getImprovedCode())
                            .put("Description", x.getExplanationLong())
                            .put("Start", x.getStart())
                            .put("End", x.getStop()))
                    .toList())
        );
        method.put("All_Improved_Methods", new JSONArray(allMethodRewrites));
    }

    public void run(){

        JSONArray selectedMethods;
        try {
            selectedMethods = FileIO.readJSONArrayFromFile(Configurations.DATA_FOLDER,
                    "selected_methods_" + projectName + ".json");
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Failed to read file");
            return;
        }
        for (int i=0; i< selectedMethods.length(); i++) {
            System.out.println("Running method "+i+", total="+selectedMethods.length());
            JSONObject method = (JSONObject) selectedMethods.get(i);
            runIterations(method, i);
        }

        try {
            FileIO.writeJSONArrayToFile(selectedMethods,
                    Configurations.IMPROVEMENTS + "/" + projectName,
                    "improvements.json", true);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
