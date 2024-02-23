package org.mal.processing.stats.overlap_analysis;

import org.mal.projectstructure.Improvement;
import org.mal.projectstructure.JavaMethod;
import org.mal.projectstructure.JavaProject;
import org.mal.utils.FetchImprovements;

import java.util.List;

public class OverlapAnalysisAllQueries {
    public static OverlapResult run(JavaProject project){

        OverlapResult results = new OverlapResult();
        for (JavaMethod method: project.getMethods()) {
            List<Improvement> improvements = null;
            improvements = method.getImprovementList();
            OverlapResult newResults = OverlapAnalysisMain.findOverlaps(improvements);

            results = OverlapResult.addInPlace(results, newResults);
        }
        return results;
    }
}
