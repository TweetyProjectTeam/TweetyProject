package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.AggregationFunction;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Reasoner for refining extension based semantics.
 * Based on "On Supported Inference and Extension Selection in Abstract Argumentation Frameworks" (S. Konieczny et al., 2015)
 */
public class OrderBasedExtensionReasoner{

    private AggregationFunction aggregationFunction;

    /**
     * Create reasoner with specific aggregation function and input extensions.
     *
     * @param aggregationFunction type of aggregation to use
     */
    public OrderBasedExtensionReasoner(AggregationFunction aggregationFunction) {
        this.aggregationFunction = aggregationFunction;
    }

    /**
     * Returns the order-based extensions, which are a subset of the Extensions given at creation of this reasoner.
     * Result depends on the chosen Aggregation Function.
     * Aggregation with MIN/MAX of the empty Extension is interpreted as +Infinity/0 respectively.
     * @return order-based extension subset of Extensions for specified aggregation function
     * @throws Exception invalid argumentation Function
     */
    public Collection<Extension<DungTheory>> getModels(Collection<Extension<DungTheory>> extensions) throws Exception {
        HashMap<Vector<Integer>, Set<Extension<DungTheory>>> aggregatedVectorToExtensionSetMap = new HashMap<>();
        Vector<Integer> argmax = new Vector<>();
        argmax.add(0);
        for(Extension<DungTheory> ext : extensions) {
            Vector<Integer> aggregatedVec = getSupportVector(ext, extensions,true);

            aggregatedVectorToExtensionSetMap.computeIfAbsent(aggregatedVec, k -> new HashSet<>());
            Set<Extension<DungTheory>> newExtensionSet = aggregatedVectorToExtensionSetMap.get(aggregatedVec);
            newExtensionSet.add(ext);
            aggregatedVectorToExtensionSetMap.put(aggregatedVec, newExtensionSet);
            if(compare(aggregatedVec, argmax) > 0){
                argmax = aggregatedVec;
            }

        }
        return aggregatedVectorToExtensionSetMap.get(argmax);
    }

    /**
     * Returns a vector with the number of every of ext arguments appearances in predefined Extensions.
     * @param ext an extension (from all extensions of this reasoners semantic)
     * @param extensions a collection of Extensions for reasoner
     * @param aggregate raw support vector if false, otherwise returns vector aggregated with the currently assigned aggregation function
     * @return support vector "vsupp"
     */
    public Vector<Integer> getSupportVector(Extension<DungTheory> ext, Collection<Extension<DungTheory>> extensions, boolean aggregate){
        Vector<Integer> vsupp = new Vector<>();
        for (Argument arg : ext) {
            vsupp.add(getNumberOfContainsInExtensions(arg, extensions));
        }
        if(aggregate){
            return aggregate(vsupp);
        }
        else {
            return vsupp;
        }
    }

    /**
     * Set a new aggregation function.
     * @param af an aggregation function.
     */
    public void setAggregationFunction(AggregationFunction af){this.aggregationFunction=af;}

    /**
     * Get the current aggregation function.
     * @return aggregation function.
     */
    public AggregationFunction getAggregationFunction(){return this.aggregationFunction;}

  
    /**
     * Returns the number of predefined Extensions in which arg is contained.
     * (ne_semantic(arg,theory)
     * @param arg an argument
     * @return number of Extensions that arg appears in.
     */
    private Integer getNumberOfContainsInExtensions(Argument arg, Collection<Extension<DungTheory>> extensions){
        int count = 0;
        for (Extension<DungTheory> ext: extensions) {
            if(ext.contains(arg)){
                count++;
            }

        }
        return count;
    }


    /**
     * Returns the aggregated version of a vector based on the selected aggregation function of reasoner.
     * For SUM/MIN/MAX returns a 1D vector.
     * For LEXIMIN/LEXIMAX dimensions are equal to input vector.
     * For max/min of an empty vector, returns a 1D vector with 0/+inf respectively.
     * Used for calculating: aggregate(vsupp(ext,theory))
     * @param vector a vector of Integers
     * @return vector aggregated by function
     */
    private Vector<Integer> aggregate(Vector<Integer> vector){
        //switch case for all aggregation types
        Vector<Integer> returnVec = new Vector<>();
        switch(Objects.requireNonNull(aggregationFunction)){

            case SUM -> {
                int sum = 0;
                for(Integer x: vector){
                    sum += x;
                }
                returnVec.add(sum);
            }
            case MAX -> {
                int max = 0;
                for(Integer x: vector){
                    if(x>max) {
                        max = x;
                    }
                }
                returnVec.add(max);
            }
            case MIN -> {
                //acts like positive infinity
                int min = Integer.MAX_VALUE;
                for (Integer x : vector) {
                    if(x<min){
                        min = x;
                    }
                }
                returnVec.add(min);
            }
            case LEXIMAX -> {
                returnVec = vector;
                returnVec.sort(Collections.reverseOrder());
                return returnVec;
            }
            case LEXIMIN -> {
                returnVec = vector;
                Collections.sort(returnVec);
                return returnVec;
            }
        }
        return returnVec;
    }

    /**
     * For SUM/MAX/MIN, compares two vectors using the value of their single element.
     * Otherwise, for LEXIMIN/LEXIMAX compares two vectors lexicographically.
     * Result returned as in Arrays.compare(int[],int[]).
     * Used to compute argmax(aggr(vsupp))
     * @param v1 first vector to compare
     * @param v2 second vector to compare
     * @return see Arrays.compare(int[],int[]);
     */
    public Integer compare(Vector<Integer> v1, Vector<Integer> v2) throws Exception {
        int returnInt;
        switch(aggregationFunction){

            case SUM, MAX, MIN -> {
                returnInt = Integer.compare(v1.get(0),v2.get(0));
                return returnInt;
            }
            case LEXIMAX,LEXIMIN -> {
                Integer[] arr1 = new Integer[v1.size()];
                v1.toArray(arr1);
                Integer[] arr2 = new Integer[v2.size()];
                v2.toArray(arr2);
                returnInt = Arrays.compare(arr1, arr2);
                return returnInt;
            }
        }
        throw new Exception("Unsupported aggregation function");


    }

    /**
     * DEPRECATED
     * Returns a value for finding the argmax from all vsupp vectors for given semantic. This is the Euclidic magnitude in most cases.
     * If the vector is empty (vsupp({},theory)) and the aggregation func is MAX / MIN, returns negative/positive infinity respectively.
     * @param vec a vector of integers
     * @return magnitude of vector, OR +/- infinity in edge cases
     */
    public Double argmaxValue(Vector<Integer> vec){
        if(vec.get(0) == Integer.MAX_VALUE){
            return Double.POSITIVE_INFINITY;
        }
        int sum = 0;
        for(int x: vec){
                sum += (x*x);
        }
        return Math.sqrt(sum);
    }
}
