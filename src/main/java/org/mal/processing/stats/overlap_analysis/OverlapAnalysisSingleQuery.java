package org.mal.processing.stats.overlap_analysis;

import org.mal.projectstructure.Improvement;
import org.mal.utils.FetchImprovements;

import java.io.IOException;
import java.util.List;

public class OverlapAnalysisSingleQuery {
    public static OverlapResult run(String projectName) {

        List<Improvement> improvements=null;
        OverlapResult results = new OverlapResult();
        for(int m=0;m<50;m++){
            for (int i = 0;i<21;i++){
                try {
                    improvements = FetchImprovements.getOne(projectName, m, i);
                } catch (IOException e){
                    break;
                }

                OverlapResult newResults = OverlapAnalysisMain.findOverlaps(improvements);
                results = OverlapResult.addInPlace(results, newResults);
            }
        }
        return results;

    }
}
