package org.mal.apply;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class CommandRunner {
    public static BuildCommandResult runCommand(String command, File workingDir, String javaHome) {
        StringWriter outputWriter = new StringWriter();
        ArrayList<String> result = new ArrayList<>();
        int exitCode = -1;

        try {
            String [] commandParts = command.split(" ");
            ProcessBuilder builder = new ProcessBuilder(commandParts);
            builder.directory(workingDir);
            builder.redirectErrorStream(true);
            Map<String, String> env = builder.environment();
            // set environment variable u
            if (!javaHome.equals("default")) {
                env.put("JAVA_HOME", javaHome);
            }

            Process process = builder.start();


            // Reading the output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    result.add(line);
                    outputWriter.write(line + "\n");
                }
            }

            exitCode = process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            result.add("Exception occurred: ");
            result.add(e.getMessage());
            outputWriter.write("Exception occurred: " + e.getMessage() + "\n");
        }

        return new BuildCommandResult(exitCode, result);
    }
}
