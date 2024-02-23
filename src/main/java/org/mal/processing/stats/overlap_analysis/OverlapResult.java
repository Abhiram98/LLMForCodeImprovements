package org.mal.processing.stats.overlap_analysis;

import org.apache.commons.lang3.tuple.Pair;
import org.mal.projectstructure.Improvement;

import java.util.ArrayList;
import java.util.List;

public class OverlapResult {
    public Integer numMethods = 0;
    public Integer numOverlaps = 0;
    public Integer numSuggestions = 0;

    List<Pair<List<Improvement>, List<Improvement>>> overlaps;

    public OverlapResult(){
        overlaps = new ArrayList<>();
    }
    public void addOverlap(List<Improvement> i1, List<Improvement> i2){
        overlaps.add(Pair.of(i1, i2));
    }

    public static OverlapResult addInPlace(OverlapResult o1, OverlapResult o2){
        o1.numOverlaps += o2.numOverlaps;
        o1.numMethods += o2.numMethods;
        o1.numSuggestions += o2.numSuggestions;
        o1.overlaps.addAll(o2.overlaps);
        return o1;
    }

}
