package org.mal.selection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.FileIO;
import org.mal.OpenAIRequestHandler;
import org.mal.Prompt;

import java.io.IOException;

public class runProject {

    String projectName = "";

    public runProject(String projectName){
        this.projectName = projectName;
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
            JSONObject method = (JSONObject) selectedMethods.get(i);
            OpenAIRequestHandler requestHandler = new OpenAIRequestHandler();
            JSONArray response =
                    requestHandler.getGPT4Response(
                            Prompt.getPrompt(method.get("Old_Method").toString())
                    );
            method.put("Method_Improvements", response);
            try {
                FileIO.writeJSONObjectToFile(method,
                        Configurations.IMPROVEMENTS + "/" + projectName,
                        "improvements-"+String.valueOf(i)+".json", true);
            } catch (Exception e){
                e.printStackTrace();
            }

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
