package org.mal.selection;

import org.mal.Configurations;

import java.io.File;

public class runGPTMain {
    public static void main(String[] args){

        File projectDir = new File(Configurations.PROJECT_REPOSITORY);
        for (File folder: projectDir.listFiles()){
            if (folder.getName().equals("blitz4j")){
                continue;
            }
            runProject rp = new runProject(folder.getName());
            rp.run();
        }


    }
}
