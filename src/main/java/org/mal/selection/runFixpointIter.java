package org.mal.selection;

import org.mal.utils.SelectedProjects;

import java.util.List;

public class runFixpointIter {
    public static void main(String[] args){

//        File projectDir = new File(Configurations.PROJECT_REPOSITORY);
        if (args.length == 0){
            System.out.println("Shouldn't be here");
//            List<String> projectNames = SelectedProjects.getProjectNames();
//            assert projectNames != null;
//            for (String name: projectNames){
//                System.out.println("Running: "+ name);
//                runProject rp = new runProject(name, -1, 5);
//                rp.run();
//            }
        }else{
        for(String pname: args){
            System.out.println("Running: " + pname);
            runProject rp = new runProject(pname, -1, 5);
            rp.run();
        }
    }


    }
}
