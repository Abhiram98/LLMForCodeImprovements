package org.mal.processing.stats;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.Configurations;
import org.mal.utils.FileIO;
import org.mal.projectstructure.Improvement;
import org.mal.prompts.PromptForClassification;
import org.mal.utils.FetchImprovements;
import org.mal.utils.LLMResponseHandler;

import java.io.IOException;
import java.util.List;

public class ClassifyImprovements {
    public static void main(String[] args){
        List<Improvement> allImprovements = FetchImprovements.fetch();
        JSONArray w = new JSONArray();

        int count=0;
        for (Improvement i: allImprovements){
            System.out.println("Classified "+count+"/"+allImprovements.size());
            count+=1;
            findClassification(i);
            w.put(
                    new JSONObject()
                            .put("class", i.getRefactoringClass())
                            .put("shortDescription", i.getExplanationShort())
                            .put("longDescription", i.getExplanationLong())
                            .put("startLine", i.getStart())
                            .put("endLine", i.getStop())
                            .put("filePath", i.getFilePath())
                            .put("methodName", i.getMethodName())
            );
            try{
            FileIO.writeJSONArrayToFile(w,
                    Configurations.IMPROVEMENTS,
                    "improvement_class.json",
                    true);
            } catch (IOException e){

                System.out.println("Failed to write to file");
                return;
            }
        }

    }

    public static void findClassification(Improvement improvement){

        String prompt = PromptForClassification.getPrompt(
                improvement.getExplanationShort(), improvement.getExplanationLong());
        try {
            String improvementClass = LLMResponseHandler.askLLM(
                    prompt,
                    PromptForClassification.SYSTEM_MSG,
                    0.0
            );
            improvement.setRefactoringClass(improvementClass);
        } catch (Exception e){
            return;
        }
    }


}
