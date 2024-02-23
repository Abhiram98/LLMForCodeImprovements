package org.mal.projectstructure;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Optional;

public class Improvement {
    String improvedCode;
    String filePath;
    Integer start;
    Integer stop;
    String explanationShort;
    String explanationLong;
    String methodName;
    JavaMethod forMethod;

    public ImprovedMethod getImprovedMethod() {
        return improvedMethod;
    }

    public void setImprovedMethod(ImprovedMethod improvedMethod) {
        this.improvedMethod = improvedMethod;
    }

    String refactoringClass = "unknown";

    ImprovedMethod improvedMethod;

    public Improvement(String improvedCode, Integer start, Integer stop, String filePath,
                       String explanationShort, String explanationLong, String methodName) {
        this.improvedCode = improvedCode;
        this.start = start;
        this.stop = stop;
        this.filePath = filePath;
        this.explanationLong = explanationLong;
        this.explanationShort = explanationShort;
        this.methodName = methodName;
    }



    public String getFilePath() {
        return filePath;
    }

    public String getImprovedCode() {
        return improvedCode;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getStop() {
        return stop;
    }

    public String getExplanationShort(){return explanationShort;}
    public String getExplanationLong(){return explanationLong;}

    public String getMethodName() { return methodName;}

    public void setRefactoringClass(String refactoringClass) {
        this.refactoringClass = refactoringClass;
    }

    public String getRefactoringClass() {
        return refactoringClass;
    }

    public String toString(){
        return "{Start:"+start+", End:"+stop+"}";
    }

    public JSONObject toJsonObject(){
        return new JSONObject()
                .put("improvedCode", improvedCode)
                .put("filePath", filePath)
                .put("start", start)
                .put("stop", stop)
                .put("explanationShort", explanationShort)
                .put("explanationLong", explanationLong)
                .put("methodName", methodName);
    }


    public JavaMethod getForMethod() {
        return forMethod;
    }

    public void setForMethod(JavaMethod forMethod) {
        this.forMethod = forMethod;
    }

    public String getTwoRemovedDiffLines(){
        Optional<String> result = Arrays.stream(improvedCode.split("\n"))
                .filter(line->line.stripLeading().startsWith("-"))
                .limit(2)
                .reduce(String::concat);
        return result.toString().replace(" ", "");
    }

}
