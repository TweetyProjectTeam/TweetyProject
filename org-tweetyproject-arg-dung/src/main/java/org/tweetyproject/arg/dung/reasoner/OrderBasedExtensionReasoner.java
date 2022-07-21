package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Reasoner for ranking arguments pairwise.
 * Based on "On Supported Inference and Extension Selection in Abstract Argumentation Frameworks" (S. Konieczny et al., 2015)
 */
public class OrderBasedExtensionReasoner {
    private final Semantics semantics;
    private final OrderBasedExtensionReasonerAggregationFunction aggregationFunction;

    /**
     * Create reasoner for specific semantic.
     *
     * @param semantics           a semantic
     * @param aggregationFunction type of aggregation to use
     */
    public OrderBasedExtensionReasoner(Semantics semantics, OrderBasedExtensionReasonerAggregationFunction aggregationFunction) {
        this.semantics = semantics;
        this.aggregationFunction = aggregationFunction;
    }

    /**
     * Returns the order-based extensions for specified semantics and aggregation fucntion (OBE_sigma_gamma)
     * @param theory a Dung theory
     * @return order-based extension of specified semantic and aggregation
     * @throws Exception unsupported/undefined semantics
     */
    public Collection<Extension<DungTheory>> getModels(DungTheory theory) throws Exception {
        Collection<Extension<DungTheory>> allExtensions = getExtensions(theory);
        HashMap<Vector<Integer>, Set<Extension<DungTheory>>> aggregatedVectorToExtensionSetMap = new HashMap<>();
        Vector<Integer> argmax = new Vector<>();
        argmax.add(0);
        for(Extension<DungTheory> ext : allExtensions) {
            Vector<Integer> aggregatedVec = aggregate(getSupportVector(ext, theory));

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
     * Returns a collection of all Extensions for predefined reasoner semantic
     * @param theory a Dung Theory
     * @return collection of Extensions
     */
    private Collection<Extension<DungTheory>> getExtensions(DungTheory theory) throws Exception {
        //make switch statement for all possible semantics

        switch (Objects.requireNonNull(semantics)){
            case CF -> {
                SimpleConflictFreeReasoner reasoner = new SimpleConflictFreeReasoner();
                return reasoner.getModels(theory);
            }
            case ADM -> {
                SimpleAdmissibleReasoner reasoner = new SimpleAdmissibleReasoner();
                return reasoner.getModels(theory);
            }
            case WAD -> {
                WeaklyAdmissibleReasoner reasoner = new WeaklyAdmissibleReasoner();
                return reasoner.getModels(theory);
            }
            case CO -> {
                SimpleCompleteReasoner reasoner = new SimpleCompleteReasoner();
                return reasoner.getModels(theory);
            }
            case GR -> {
                SimpleGroundedReasoner reasoner = new SimpleGroundedReasoner();
                return reasoner.getModels(theory);
            }
            case PR -> {
                SimplePreferredReasoner reasoner = new SimplePreferredReasoner();
                return reasoner.getModels(theory);
            }
            case ST -> {
                SimpleStableReasoner reasoner = new SimpleStableReasoner();
                return reasoner.getModels(theory);
            }
            case STG -> {
                SimpleStageReasoner reasoner = new SimpleStageReasoner();
                return reasoner.getModels(theory);
            }
            case STG2 -> {
                Stage2Reasoner reasoner = new Stage2Reasoner();
                return reasoner.getModels(theory);
            }
            case SST -> {
                SimpleSemiStableReasoner reasoner = new SimpleSemiStableReasoner();
                return reasoner.getModels(theory);
            }
            case ID -> {
                SimpleIdealReasoner reasoner = new SimpleIdealReasoner();
                return reasoner.getModels(theory);
            }
            case EA -> {
                SimpleEagerReasoner reasoner = new SimpleEagerReasoner();
                return reasoner.getModels(theory);
            }
            case CF2 -> {
                SccCF2Reasoner reasoner = new SccCF2Reasoner();
                return reasoner.getModels(theory);
            }
            case SCF2 -> {
                SCF2Reasoner reasoner = new SCF2Reasoner();
                return reasoner.getModels(theory);
            }
            case N -> {
                SimpleNaiveReasoner reasoner = new SimpleNaiveReasoner();
                return reasoner.getModels(theory);
            }
            case diverse -> throw new Exception("Semantics type not defined for this usage.");
        }
        throw new Exception("Illegal Semantics.");
    }

    /**
     * Returns the number of extensions (of reasoners semantic) arg appears in.
     * (ne_semantic(arg,theory)
     * @param arg an argument
     * @param theory a dung theory
     * @return number of extensions (of reasoners semantic) arg appears in.
     * @throws Exception unsupported/undefined semantics
     */
    private Integer getNumberOfContainsInExtensions(Argument arg, DungTheory theory) throws Exception {
        Collection<Extension<DungTheory>> allExtensions = getExtensions(theory);
        int count = 0;
        for (Extension<DungTheory> ext: allExtensions) {
            if(ext.contains(arg)){
                count++;
            }

        }
        return count;
    }

    /**
     * Returns a vector with the number of every of ext arguments appearances in all extensions.
     * @param ext an extension (from all extensions of this reasoners semantic)
     * @param theory a dung theory
     * @return support vector "vsupp"
     * @throws Exception unsupported/undefined semantics
     */
    private Vector<Integer> getSupportVector(Extension<DungTheory> ext, DungTheory theory) throws Exception {
        Vector<Integer> vsupp = new Vector<>();
        for (Argument arg : ext) {
            vsupp.add(getNumberOfContainsInExtensions(arg, theory));
        }
        return vsupp;
    }

    /**
     * Returns the aggregated version of a vector based on the selected aggregation function of reasoner.
     *
     * Used for calculating: aggregate(vsupp(ext,theory))
     * @param vector a vector of Integers
     * @return aggregation function applied to vector
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
                int max = Integer.MIN_VALUE;
                for(Integer x: vector){
                    if(x>max) {
                        max = x;
                    }
                }
                returnVec.add(max);
            }
            case MIN -> {
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
     * Compares two vectors by size (if aggrfunc = SUM,MAX,MIN).
     * Otherwise (LEXI*) compares two vectors lexicographically.
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
        throw new Exception("something went wrong");


    }

    /**
     * DEPRECATED
     * Returns a value for finding the argmax from all vsupp vectors for given semantic. This is the Euclidic magnitude in most cases.
     * If the vector is empty (vsupp({},theory)) and the aggregation func is MAX / MIN, returns negative/positive infinity respectively.
     * @param vec a vector of integers
     * @return magnitude of vector, OR +/- infinity in edge cases
     */
    public Double argmaxValue(Vector<Integer> vec){
        int sum = 0;
        for(int x: vec){
            if(x == Integer.MAX_VALUE){
                return Double.POSITIVE_INFINITY;
            }
            else if(x == Integer.MIN_VALUE){
                return Double.NEGATIVE_INFINITY;
            }
            else{
                sum += (x*x);
            }
        }
        return Math.sqrt(sum);
    }
}
