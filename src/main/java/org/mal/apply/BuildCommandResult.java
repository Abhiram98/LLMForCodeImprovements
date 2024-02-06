package org.mal.apply;

import java.util.List;

public class BuildCommandResult {
    private final int exitCode;
    private final List<String> output;

    public BuildCommandResult(int exitCode, List<String> output) {
        this.exitCode = exitCode;
        this.output = output;
    }

    public int getExitCode() {
        return exitCode;
    }

    public List<String> getOutput() {
        return output;
    }
}