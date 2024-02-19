package org.mal.selection;

import org.mal.Configurations;
import org.mal.utils.SelectedProjects;

import java.io.File;
import java.util.List;

public class SelectMain {
    public static void main(String[] args){
        System.out.println("hello select!");
//        SelectFunctions sf = new SelectFunctions("blitz4j", "data/projects/blitz4j");
//        sf.getFunctions();

        List<String> projectNames = SelectedProjects.getProjectNames();
        for (String name: projectNames){
            SelectFunctions sf = new SelectFunctions(name, Configurations.PROJECT_REPOSITORY+name);
            sf.getFunctions();

        }
    }
}
