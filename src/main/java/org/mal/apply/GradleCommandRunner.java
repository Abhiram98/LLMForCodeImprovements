package org.mal.apply;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

public class GradleCommandRunner {

    public static BuildCommandResult gradleClean(File workingDir, String javaHome){
        return GradleCommandRunner.runGradleCommand("clean", workingDir, javaHome);
    }

    public static BuildCommandResult gradleCompile(File workingDir, String javaHome){
        return GradleCommandRunner.runGradleCommand("build", workingDir, javaHome);
    }

    /**
     * Executes a Maven command in a specified working directory and captures its output.
     *
     * This method runs a Maven command using the provided `command` string in the context of the given `workingDir` directory.
     * It captures both the standard output and error stream of the Maven process, consolidates them into a single output,
     * and records the exit code of the process. The method is designed to handle Maven commands generically, allowing
     * for various Maven operations (e.g., clean, compile) to be executed programmatically.
     *
     * @param command The Maven command to execute (e.g., "clean", "compile").
     * @param workingDir The directory in which the Maven command should be executed. This should be the root directory of a Maven project.
     * @return A MavenCommandResult object containing the exit code of the Maven process and the console output (both standard output and error stream combined).
     *         The exit code is 0 for a successful execution or a positive integer if an error occurred. The console output includes all lines produced by the
     *         Maven command, and if an exception occurs during the execution, it includes the exception message.
     *
     * @throws IOException If an I/O error occurs during the execution of the Maven command or while reading its output.
     * @throws InterruptedException If the current thread is interrupted while waiting for the Maven process to complete.
     */
    private static BuildCommandResult runGradleCommand(String command, File workingDir, String javaHome) {
        StringWriter outputWriter = new StringWriter();
        ArrayList<String> result = new ArrayList<>();
        int exitCode = -1;

        try {
            ProcessBuilder builder = new ProcessBuilder("gradle", command, "-x", "test");
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
