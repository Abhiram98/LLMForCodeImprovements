package org.mal.selection;

import org.mal.Configurations;

import java.io.File;

public class runGPTMain {
    public static void main(String[] args){

        File projectDir = new File(Configurations.PROJECT_REPOSITORY);
        for (File folder: projectDir.listFiles()){
            System.out.println("Running: "+ folder.getName());
            runProject rp = new runProject(folder.getName());
            rp.run();
        }


    }
}
