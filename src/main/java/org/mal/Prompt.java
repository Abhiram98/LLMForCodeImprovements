package org.mal;

import java.util.ArrayList;
import java.util.List;

public class Prompt {
    public static String getPrompt(String code) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please provide suggestions to improve the following Java method. Ensure that your recommendations are " +
                "specific to this method, Your response should be formatted as a JSON object comprising two main fields. " +
                "The first field, named 'Improvements', should be a list of JSON objects, each with the following attributes: 'Improvement' " +
                "providing a brief summary of the improvement, 'Description' offering a detailed explanation of the improvement, 'Start', " +
                "indicating the line number where the improvement should be applied, " +
//                "'Before_Change', providing a JSON list containing sections of the methods that will be subject to the change," +
//                "and 'After_Change', providing a JSON list containing sections of the methods that have changed. " +
                "'Change_Diff', differences in the git diff style representing the intended changes for this improvement.\n"+
                "The second field, named 'Final code', " +
                "should contain the code with all the suggested improvements applied. Please include only the JSON structure specified in your response.");
        sb.append("\n");
        sb.append(code);
        sb.append("\n");
        return sb.toString();
    }
}
