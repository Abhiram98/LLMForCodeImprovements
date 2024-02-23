package org.mal;

import org.mal.ast.ProcessAST;

public class GenerateImprovements {
    public static void main(String[] args) {
        ProcessAST p = new ProcessAST();
        p.processProjects();
    }
}