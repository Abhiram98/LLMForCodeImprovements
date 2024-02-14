package org.mal.apply;

public class Improvement {
    String improvedCode;
    String filePath;
    Integer start;
    Integer stop;
    String explanationShort;
    String explanationLong;

    public Improvement(String improvedCode, Integer start, Integer stop, String filePath,
                       String explanationShort, String explanationLong) {
        this.improvedCode = improvedCode;
        this.start = start;
        this.stop = stop;
        this.filePath = filePath;
        this.explanationLong = explanationLong;
        this.explanationShort = explanationShort;
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

    public String toString(){
        return "{Start:"+start+", End:"+stop+"}";
    }
}
