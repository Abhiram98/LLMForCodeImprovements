package org.mal.utils;

import org.mal.Configurations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class LLMResponseHandler {

    public static String askLLM(String prompt, String systemMessage,
                                Double temperature) throws Exception{
        String pythonPath = System.getenv("PYTHON_PATH");
        if (pythonPath==null)
            pythonPath = "python3";

        String [] args = {
                pythonPath,
                Configurations.LLM_PY_QUERY_FILE,
                "--prompt-str",
                prompt,
                "--system-msg",
                systemMessage,
                "--temperature",
                temperature.toString()
        };

        ProcessBuilder builder = new ProcessBuilder(args);
        Process process = builder.start();
        StringBuilder response = new StringBuilder();
        process.waitFor();

        if (process.exitValue()==0) {
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append(System.getProperty("line.separator"));
                }
            }
            return response.toString();
        }else{
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append(System.getProperty("line.separator"));
                }
            }
            return response.toString();
        }

    }

    public static void main(String [] args){
        try {
            String response = LLMResponseHandler.askLLM(
                    "What is refactoring?",
                    "You are an expert in Java programming",
                    0.5
            );
            System.out.println(response);
        } catch (Exception e){
            System.out.println("Failed to query LLM.");
            e.printStackTrace();
        }
    }
}
