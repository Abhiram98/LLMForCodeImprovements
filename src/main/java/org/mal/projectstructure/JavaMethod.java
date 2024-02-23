package org.mal.projectstructure;

import java.util.List;

public class JavaMethod {

    String methodName;
    String filePath;
    String methodCode;
    Integer startLine;
    Integer endLine;
    JavaProject fromProject;

    List<Improvement> improvementList;


    List<ImprovedMethod> finalImprovedMethods;

    public JavaMethod(String methodName, String filePath,
                      String methodCode, Integer startLine,
                      Integer endLine, JavaProject fromProject){
        this.methodName = methodName;
        this.filePath = filePath;
        this.methodCode = methodCode;
        this.startLine = startLine;
        this.endLine = endLine;
        this.fromProject = fromProject;
    }

    public void setImprovementList(List<Improvement> improvementList) {
        this.improvementList = improvementList;
    }

    public JavaProject getJavaProject() {
        return fromProject;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public JavaProject getFromProject() {
        return fromProject;
    }

    public List<Improvement> getImprovementList() {
        return improvementList;
    }
    public List<ImprovedMethod> getFinalImprovedMethods() {
        return finalImprovedMethods;
    }

    public void setFinalImprovedMethods(List<ImprovedMethod> finalImprovedMethods) {
        this.finalImprovedMethods = finalImprovedMethods;
    }

}
