package org.mal.selection;

import org.mal.Configurations;
import org.mal.utils.SelectedProjects;

import java.io.File;
import java.util.List;

public class runGPTMain {
    public static void main(String[] args){

//        File projectDir = new File(Configurations.PROJECT_REPOSITORY);
        List<String> projectNames = SelectedProjects.getProjectNames();
        for (String name: projectNames){
            System.out.println("Running: "+ name);
            runProject rp = new runProject(name);
            rp.run();
        }


    }
}
