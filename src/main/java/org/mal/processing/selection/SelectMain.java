package org.mal.processing.selection;

import org.mal.Configurations;
import org.mal.utils.FetchProjectsAndData;

import java.util.List;

public class SelectMain {
    public static void main(String[] args){
        System.out.println("hello select!");
//        SelectFunctions sf = new SelectFunctions("blitz4j", "data/projects/blitz4j");
//        sf.getFunctions();

        List<String> projectNames = FetchProjectsAndData.getProjectNames();
        for (String name: projectNames){
            SelectFunctions sf = new SelectFunctions(name, Configurations.PROJECT_REPOSITORY+name);
            sf.getFunctions();

        }
    }
}
