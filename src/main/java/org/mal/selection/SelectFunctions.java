package org.mal.selection;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mal.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SelectFunctions {

    Integer minLoc = 10;
    Integer maxLoc = 300;
    Integer bucketSize = 10;
//    String jsonOutFile = "selected_methods.json";
    String projectName = "";
    String projectPath = "";

    public SelectFunctions(String projectName, String projectPath){
        this.projectName = projectName;
        this.projectPath = projectPath;
    }

    private void toJsonFile(ArrayList<org.mal.MethodDeclaration> selectedMethods){
        JSONArray array = new JSONArray();
        for (MethodDeclaration m: selectedMethods){
            JSONObject obj = new JSONObject();
            obj.put("Method_Name", m.getName());
            obj.put("File_Path", m.getUrl());
            obj.put("Old_Method", m.getMethod().toString());
            obj.put("Start", m.getStartCharacter());
            obj.put("Stop", m.getEndCharacter());
            obj.put("Project_Name", projectPath);

            array.put(obj);
        }

        try {
            String jsonOutFile = String.format("selected_methods_%s.json", projectName);
            FileIO.writeJSONArrayToFile(array, "data/", jsonOutFile, true);
        } catch (Exception e){
            System.out.println("Failed to write to json file");
        }

    }

    private ArrayList<org.mal.MethodDeclaration> selectMethods(
            HashMap<Integer, ArrayList<org.mal.MethodDeclaration>> byLineNum,
            ArrayList<org.mal.MethodDeclaration> allSelectedMethods
            ){

        Random ran = new Random(42);

        for (Integer lineBucket : byLineNum.keySet()){
            System.out.println("lineBucket:"+lineBucket);
            ArrayList<org.mal.MethodDeclaration> methods = byLineNum.get(lineBucket);
            Integer numMethods = methods.size();
            if (numMethods > 0) {
                Integer ind = ran.nextInt(numMethods);
                allSelectedMethods.add(methods.get(ind));
                Integer ind2 = ran.nextInt(numMethods);
                if (ind2!=ind) {
                    allSelectedMethods.add(methods.get(ind));
                }
            }
        }

        return allSelectedMethods;
    }

    private HashMap<Integer, ArrayList<org.mal.MethodDeclaration>> getByLineNum(
            List<org.mal.MethodDeclaration> methods,
            HashMap<Integer, ArrayList<org.mal.MethodDeclaration>> byLineNum
            ){

//        HashMap<Integer, ArrayList<org.mal.MethodDeclaration>> byLineNum = new HashMap<>();

        for (org.mal.MethodDeclaration mi : methods){
            Long len = mi.getMethod().getBody().toString().trim()
                    .chars().filter(ch -> ch == '\n').count() - 1;
            if (len > maxLoc || len < minLoc){
                continue;
            }

            Integer int_len = len.intValue();
            Integer bucket = int_len - int_len % bucketSize;

            if (!byLineNum.containsKey(bucket)){
                byLineNum.put(bucket, new ArrayList<org.mal.MethodDeclaration>());
            }
            ArrayList<org.mal.MethodDeclaration> lineList = byLineNum.get(bucket);
            lineList.add(mi);
        }

        return byLineNum;
    }

    public void getFunctions(){

//            File[] file = (new File(getClass().getResource(projectPath).toURI())).listFiles();
            ArrayList<Path> allFiles = new ArrayList<>();
            allFiles.addAll(
                    FileIO.getAllFilesInDirectory(
                    Paths.get(projectPath), ".java")
            );

            System.out.println(allFiles);
            List<Integer> methodLengths = new ArrayList<Integer>();
            ArrayList<org.mal.MethodDeclaration> allSelectedMethods = new ArrayList<>();
            HashMap<Integer, ArrayList<org.mal.MethodDeclaration>> byLineNum = new HashMap<>();

            for (Path p: allFiles){
//                System.out.println(p);
                try {

                    String text = FileIO.readStringFromFile(p);
                    ASTNode node = JavaASTUtil.parseSource(text);
                    MethodVisitor visitor = new MethodVisitor();
                    CompilationUnit cUnit = (CompilationUnit)node;
                    cUnit.accept(visitor);

                    List<org.mal.MethodDeclaration> methods =
                            visitor.getMethods().stream()
                                    .map(x ->
                                new org.mal.MethodDeclaration(
                                        x, x.getName().getFullyQualifiedName(),
                                        x.getStartPosition(),
                                        x.getLength() + x.getStartPosition(),
                                        Paths.get(Configurations.PROJECT_REPOSITORY).relativize(p).toString())).toList();

                    getByLineNum(methods, byLineNum);

                } catch (Exception e){
//                    System.out.println("Couldn't find file");
                }
            }
            allSelectedMethods = selectMethods(byLineNum, allSelectedMethods);
            System.out.println("num of methods selected:"+allSelectedMethods.size());

            toJsonFile(allSelectedMethods);

    }
}
