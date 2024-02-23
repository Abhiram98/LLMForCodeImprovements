package org.mal.projectstructure;

public class ImprovedMethod {

    String improvementFromLLM;
    JavaMethod forMethod=null;

    public ImprovedMethod(String improvementFromLLM){
        this.improvementFromLLM = improvementFromLLM;
    }

    public void setImprovementFromLLM(String improvementFromLLM) {
        this.improvementFromLLM = improvementFromLLM;
    }
    public void setForMethod(JavaMethod javaMethod) {
        this.forMethod = javaMethod;
    }
    public String getImprovementFromLLM() {
        return improvementFromLLM;
    }
    public JavaMethod getForMethod() {
        return forMethod;
    }
}
