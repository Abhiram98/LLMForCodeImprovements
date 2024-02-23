package org.mal.prompts;

public class PromptForClassification {
    public static String SYSTEM_MSG = "You are an expert Java programmer.";
    public static String getPrompt(String shortDescription, String longDescription) {
        StringBuilder sb = new StringBuilder();

        String description = shortDescription + ". "+ longDescription;

        sb.append(
                "Your task is to classify a suggestion (within double qoutes) to improve a java method into one of the following classes:\n" +
                "\n" +
                "Use <method-1> method instead of <method-2>.\n" +
                "Use StringBuilder instead of <>.\n" +
                "Use try-with-resources.\n" +
                "Improve variable name(s).\n" +
                "Add comments.\n" +
                "Use diamond operator.\n" +
                "Use <method> instead of null check.\n" +
                "Use enhanced for loop.\n" +
                "Improve code formatting.\n" +
                "Use switch statement.\n" +
                "Use Lambda expressions.\n" +
                "Use Java streams.\n" +
                "Extract code blocks into separate method.\n" +
                "Improve Exception handling.\n" +
                "Change Type.\n" +
                "Replace Magic values with constants.\n" +
                "Remove Statements/Expressions.\n" +
                "Add Statements/Expressions.\n" +
                "Move Statements/Expressions.\n" +
                "Use Ternary operator.\n" +
                "Add null checking condition.\n" +
                "Use final keyword for immutable variables.\n" +
                "Other.\n" +
                " \n" +
                "\n" +
                "\""+description+"\"\n");
        return sb.toString();
    }
}
