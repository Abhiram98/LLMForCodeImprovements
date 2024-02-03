package org.mal.selection;

import org.mal.Configurations;

import java.io.File;

public class SelectMain {
    public static void main(String[] args){
        System.out.println("hello select!");
//        SelectFunctions sf = new SelectFunctions("blitz4j", "data/projects/blitz4j");
//        sf.getFunctions();

        File projectDir = new File(Configurations.PROJECT_REPOSITORY);
        for (File folder: projectDir.listFiles()){
            SelectFunctions sf = new SelectFunctions(folder.getName(), folder.getPath());
            sf.getFunctions();

        }
    }
}
