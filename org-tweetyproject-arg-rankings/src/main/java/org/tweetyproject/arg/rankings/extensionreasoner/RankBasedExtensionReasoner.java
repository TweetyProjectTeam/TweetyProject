package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.comparator.GeneralComparator;
import org.tweetyproject.comparator.NumericalPartialOrder;

import java.util.*;
/**
 * Reasoner for refining extension based semantics with argument rankings.
 * Based on "Combining Extension-Based Semantics and Ranking-Based Semantics for Abstract Argumentation" (E. Bonzon et al, KR 2018)
 */
public class RankBasedExtensionReasoner {
    private AggregationFunction aggregationFunction;

    public RankBasedExtensionReasoner(AggregationFunction aggregationFunction){
        this.aggregationFunction = aggregationFunction;
    }

    public AggregationFunction getAggregationFunction() {
        return aggregationFunction;
    }

    public void setAggregationFunction(AggregationFunction aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
    }
// TODO: JavaDoc

    //input is one collection of extensions
    //list index = distance to the worst element
    /**
     * Returns the rank-based extensions, which are a subset of the 'extensions' parameter.
     * Result depends on the input rankMap and the chosen Aggregation Function.
     * Integers in the map represent the rank in ASCENDING order, with 0 being best.
     * Aggregation with MIN/MAX of the empty Extension is interpreted as +Infinity/0 respectively.
     * @return order-based extension subset of Extensions for specified aggregation function
     * @throws Exception invalid argumentation Function
     */
    public Collection<Extension<DungTheory>> getModels(Collection<Extension<DungTheory>> extensions, GeneralComparator<Argument,DungTheory> argumentRanks, DungTheory theory) throws Exception {
        Map<Vector<Double>, Set<Extension<DungTheory>>> aggregatedVectorToExtensionSetMap = getAggregatedVectorToExtensionMap(extensions,argumentRanks,theory);
        Vector<Double> argmin = aggregatedVectorToExtensionSetMap.keySet().stream().findFirst().orElseThrow();
        for(Vector<Double> vec: aggregatedVectorToExtensionSetMap.keySet()){
            if(argmin == null){
                argmin = vec;
                continue;
            }
            if(compare(vec, argmin) < 0){
                argmin = vec;
            }
        }

        return aggregatedVectorToExtensionSetMap.get(argmin);
    }

    private Map<Argument, Integer> getIntegerRankMap(GeneralComparator<Argument, DungTheory> argumentRanks, DungTheory theory) {
        Map<Argument,Integer> rankMap = new HashMap<>();

        Collection<Argument> arguments = theory.clone().getNodes();

        int rank = 0;
        while(!arguments.isEmpty()){
//            double max = 0;
//            for(Argument arg: arguments){
//                if(catRanking.get(arg)>max){
//                    max = catRanking.get(arg);
//                    maxArgs.clear();
//                    maxArgs.add(arg);
//                }
//                else if(catRanking.get(arg) == max){
//                    maxArgs.add(arg);
//                }
//            }
            Collection<Argument> maxArgs = argumentRanks.getMaximallyAcceptedArguments(arguments);
            for(Argument maxArg: maxArgs){
                rankMap.put(maxArg,rank);
                arguments.remove(maxArg);
            }
            ++rank;
            maxArgs.clear();


        }
        return rankMap;
    }

    public Map<Vector<Double>,Set<Extension<DungTheory>>> getAggregatedVectorToExtensionMap(Collection<Extension<DungTheory>> extensions,GeneralComparator<Argument,DungTheory> argumentRanks, DungTheory theory){

        Map<Argument,Integer> integerRankMap = getIntegerRankMap(argumentRanks,theory);
        Map<Vector<Double>, Set<Extension<DungTheory>>> aggregatedVectorToExtensionSetMap = new HashMap<>();
        for(Extension<DungTheory> ext : extensions) {
            Vector<Double> extRankingVec = new Vector<>(ext.size());
            for (Argument arg : ext) {
                double val = (double) integerRankMap.get(arg);
                extRankingVec.add(val);
            }
            Vector<Double> aggregatedVec = aggregate(extRankingVec);

            aggregatedVectorToExtensionSetMap.computeIfAbsent(aggregatedVec, k -> new HashSet<>());
            Set<Extension<DungTheory>> newExtensionSet = aggregatedVectorToExtensionSetMap.get(aggregatedVec);
            newExtensionSet.add(ext);
            aggregatedVectorToExtensionSetMap.put(aggregatedVec, newExtensionSet);
        }
        return aggregatedVectorToExtensionSetMap;
    }


    /**
     * Returns the aggregated version of a vector based on the selected aggregation function of reasoner.
     * For AVG/SUM/MIN/MAX returns a 1D vector.
     * For LEXIMIN/LEXIMAX dimensions are equal to input vector.
     * For max/min of an empty vector, returns a 1D vector with 0/+inf respectively.
     *
     * @param vector a vector of Integers
     * @return vector aggregated by function
     */
    private Vector<Double> aggregate(Vector<Double> vector){
        //switch case for all aggregation types
        Vector<Double> returnVec = new Vector<>();
        switch(Objects.requireNonNull(aggregationFunction)){

            case AVG -> {
                double sum = 0d;
                for(Double x: vector){
                    sum += x;
                }
                returnVec.add(sum / vector.size());
            }
            case SUM -> {
                double sum = 0d;
                for(Double x: vector){
                    sum += x;
                }
                returnVec.add(sum);
            }
            case MAX -> {
                double max = 0d;
                for(Double x: vector){
                    if(x>max) {
                        max = x;
                    }
                }
                returnVec.add(max);
            }
            case MIN -> {
                //acts like positive infinity
                double min = Double.POSITIVE_INFINITY;
                for (Double x : vector) {
                    if(x<min){
                        min = x;
                    }
                }
                returnVec.add(min);
            }
            case LEXIMAX -> {
                returnVec = vector;
                returnVec.sort(Collections.reverseOrder());
            }
            case LEXIMIN -> {
                returnVec = vector;
                Collections.sort(returnVec);
            }
        }
        return returnVec;
    }

    /**
     * For AVG/SUM/MAX/MIN, compares two vectors using the value of their single element.
     * Otherwise, for LEXIMIN/LEXIMAX compares two vectors lexicographically.
     * Result returned as in Arrays.compare(int[],int[]).
     * Used to compute argmin(aggr(rv))
     * @param v1 first vector to compare
     * @param v2 second vector to compare
     * @return see Arrays.compare(int[],int[]);
     */
    public Integer compare(Vector<Double> v1, Vector<Double> v2) throws Exception {
        int returnInt;
        switch(aggregationFunction){

            case AVG, SUM, MAX, MIN -> {
                returnInt = Double.compare(v1.get(0),v2.get(0));
                return returnInt;
            }
            case LEXIMAX,LEXIMIN -> {
                Double[] arr1 = new Double[v1.size()];
                v1.toArray(arr1);
                Double[] arr2 = new Double[v2.size()];
                v2.toArray(arr2);
                returnInt = Arrays.compare(arr1, arr2);
                return returnInt;
            }
        }
        throw new Exception("Unsupported aggregation function.");


    }
}
