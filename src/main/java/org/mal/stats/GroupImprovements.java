package org.mal.stats;

import org.mal.Configurations;
import org.mal.FileIO;
import org.mal.apply.Improvement;
import org.mal.utils.SelectedProjects;
import org.mal.utils.fetchImprovements;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GroupImprovements {
    /**
     * This main function analyzes the improvements in the resources/improvements.txt and tries to group them based on regex matching.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<String> improvements = getImprovements().stream().map(s -> s.replaceAll("^\\d+\\. ", "")).toList();
        System.out.println("Total Improvements "+improvements.size());
        Map<String, Integer> improvementsCounts = getImprovementsCounts(improvements);
        Set<String> set = new HashSet<>(improvements);
        List<String> dedupedImprovements = new ArrayList<>(set);

        HashMap<Pattern, List<String>> patternAndImprvements = new HashMap<>();
        HashMap<Pattern, Integer> patternToInstances = new HashMap<>();
        System.out.println("Total instances ::: "+improvementsCounts.values().stream().mapToInt(Integer::intValue).sum());

        assert improvementsCounts.values().stream().reduce(Integer::sum).get()==improvements.size();
        for (Pattern s : getStringPatterns()) {
            patternAndImprvements.put(s,improvements.stream().filter(x -> s.matcher(x).find()).toList());
            try {
                patternToInstances.put(s,
                        dedupedImprovements
                                .stream()
                                .filter(x -> s.matcher(x).find())
                                .map(improvementsCounts::get)
                                .reduce(Integer::sum)
                                .get()
                );
            } catch (Exception e){
                patternToInstances.put(s, 0);
            }
            dedupedImprovements = dedupedImprovements.stream().filter(x -> !s.matcher(x).find()).toList();
        }
        patternAndImprvements.entrySet().forEach(x-> System.out.println(x.getKey().pattern()+" ::: "+x.getValue().size()+" ::: "+x.getValue().toString()));

        List<String> nonMatched = improvements.stream().filter(x -> getStringPatterns().stream().noneMatch(y -> y.matcher(x).find())).toList();
        System.out.println("Size of non-matched improvements "+nonMatched.size());
        nonMatched.forEach(System.out::println);
        FileIO.writeLinesToFile(Configurations.IMPROVEMENTS+"non_matched.txt", nonMatched.stream().sorted().toList());

        System.out.println("Matched-improvements--");
        patternToInstances.entrySet().forEach(x -> System.out.println(x.getKey()+" ::: "+ x.getValue()));
    }


    /**
     * get impvements and their frequency
     * @return
     * @throws IOException
     */
    private static Map<String, Integer> getImprovementsCounts(List<String> improvementLines) throws IOException {
//        Map<String, Integer> collect = FileIO.readFileLineByLineNIO(Path.of(getFileInResources("improvements.txt").getPath()))
//                .map(q -> q.replaceAll("^\\d+\\. ", ""))
//                .map(line -> line.split(","))
//                .filter(z -> z.length == 2)
//                .filter(parts -> parts[0] != null && parts[1] != null)
//                .filter(parts -> !parts[0].trim().isEmpty() && !parts[1].trim().isEmpty())
//                .collect(Collectors.toMap(x -> x[0],
//                        y -> Integer.parseInt(y[1].trim()), (existing, replacement) -> existing));

        Map<String, Integer> countMap = new HashMap<>();

        for (String str : improvementLines) {
            // If the string is already in the map, increment its count; otherwise, add it with count 1.
            countMap.put(str, countMap.getOrDefault(str, 0) + 1);
        }
//
//        Map<String, Integer> collect =
//                FileIO.readFileLineByLineNIO(Path.of(Configurations.IMPROVEMENTS+"improvements.txt"))
//                .collect(Collectors.toMap(x -> x,
//                        y -> 1, (existing, replacement) -> existing));
        return countMap;
    }



    /**
     * get the list of improvements from the folder in Configurations.IMPROVEMENTS
     * @return
     * @throws IOException
     */
    private static List<String> getImprovements() throws IOException {
        List<Improvement> allImps = fetchImprovements.fetch();
        return allImps.stream()
                .map(Improvement::getExplanationShort)
                .toList();
//        return FileIO.readFileLineByLineNIO(
//                Path.of(Configurations.IMPROVEMENTS+"improvements.txt")
//        ).toList();
    }


    /**
     * Users can define list of regex patterns inside the method, it converts string regex into a pattern
     * @return
     */
    private static List<Pattern> getStringPatterns(){
        List<String> list = List.of("Use enhanced for[- ]loop.*",
                "Use foreach loop instead of.*",
                "(Use|Improve|Update|Refactor).* variable names?|Rename .*?",
                "Extract .*?",
                "Use (a )?StringBuilder(\\.append\\(\\))? (for|instead of|to)? .*",
                "Use lambda expression.*",
                "Use ([\\w\\.]+\\(.*?\\)) instead of (!?[\\w\\.]+\\(.*?\\))( .*?)?",
//                "Use (\\w+\\.)?(\\w+\\(\\)) instead of null check.*",
//                "Use (\\w+\\.)?(\\w+\\(\\)) method instead of.*",
//                "Use .*? method[.!?]?$",
                "Consistent indentation|Improve code formatting|Structure code .*?",
                "Use try-with-resources( for .*?)?",
                "Use switch statement for *.?",
                "Add comments *.?",
                "Use diamond operator .*?");
        return list.stream().map(Pattern::compile).toList();
    }

    /**
     *
     * @param fileName file name in resources folder that need path
     * @return URL for the path
     */
    private static URL getFileInResources(String fileName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResource(fileName);
    }


    private static void getAllImprovements(){
        List<String> allProjects = SelectedProjects.getProjectNames();
    }

}
