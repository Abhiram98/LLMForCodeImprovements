package org.mal.processing.selection;

import org.mal.utils.FetchProjectsAndData;

import java.util.List;

public class runGPTMain {
    public static void main(String[] args){

//        File projectDir = new File(Configurations.PROJECT_REPOSITORY);
        List<String> projectNames = FetchProjectsAndData.getProjectNames();
        for (String name : projectNames) {
            System.out.println("Running: " + name);
            runProject rp = new runProject(name, 1, 1);
            rp.run();
        }



    }
}
