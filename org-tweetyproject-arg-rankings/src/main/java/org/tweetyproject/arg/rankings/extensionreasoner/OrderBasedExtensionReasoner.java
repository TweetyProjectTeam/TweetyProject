package org.tweetyproject.arg.rankings.extensionreasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Reasoner for refining extension based semantics.
 * Based on "On Supported Inference and Extension Selection in Abstract Argumentation Frameworks" (S. Konieczny et al., 2015)
 * @author Daniel Letkemann
 */
public class OrderBasedExtensionReasoner {

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
     * Result depends on the chosen Aggregation Function and support vector function.
     * Aggregation with MIN/MAX of the empty Extension is interpreted as +Infinity/0 respectively.
     *
     * @param extensions set of extensions
     * @param function a specific function that computes the support vector
     * @return order-based extension subset of Extensions for specified aggregation function
     * @throws Exception invalid argumentation Function
     */
    public Collection<Extension<DungTheory>> getModels(Collection<Extension<DungTheory>> extensions, SupportVectorFunction function) throws Exception {
        HashMap<Vector<Double>, Set<Extension<DungTheory>>> aggregatedVectorToExtensionSetMap = new HashMap<>();
        Vector<Double> argmax = new Vector<>();
        argmax.add(0d);
        for (Extension<DungTheory> ext : extensions) {
            Vector<Double> suppVec = function.getSupportVector(ext, extensions, true);
            Vector<Double> aggregatedVec = aggregate(suppVec);

            aggregatedVectorToExtensionSetMap.computeIfAbsent(aggregatedVec, k -> new HashSet<>());
            Set<Extension<DungTheory>> newExtensionSet = aggregatedVectorToExtensionSetMap.get(aggregatedVec);
            newExtensionSet.add(ext);
            aggregatedVectorToExtensionSetMap.put(aggregatedVec, newExtensionSet);
            if (compare(aggregatedVec, argmax) > 0) {
                argmax = aggregatedVec;
            }

        }
        return aggregatedVectorToExtensionSetMap.get(argmax);
    }
    
    
    /**
     * Returns the ranked order-based extensions, which are a subset of the Extensions given at creation of this reasoner.
     * Result depends on the chosen Aggregation Function and support vector function.
     * Aggregation with MIN/MAX of the empty Extension is interpreted as +Infinity/0 respectively.
     *
     * @param extensions set of extensions
     * @param function a specific function that computes the support vector
     * @return ranked order-based extension subset of Extensions for specified aggregation function
     * @throws Exception invalid argumentation Function
     */
    public HashMap<Vector<Double>, Set<Extension<DungTheory>>> getRankedExtensions(Collection<Extension<DungTheory>> extensions, SupportVectorFunction function) throws Exception {
    	
    	HashMap<Vector<Double>, Set<Extension<DungTheory>>> aggregatedVectorToExtensionSetMap = new HashMap<>();
        Vector<Double> argmax = new Vector<>();
        argmax.add(0d);
        for (Extension<DungTheory> ext : extensions) {
            Vector<Double> suppVec = function.getSupportVector(ext, extensions, true);
            Vector<Double> aggregatedVec = aggregate(suppVec);

            aggregatedVectorToExtensionSetMap.computeIfAbsent(aggregatedVec, k -> new HashSet<>());
            Set<Extension<DungTheory>> newExtensionSet = aggregatedVectorToExtensionSetMap.get(aggregatedVec);
            newExtensionSet.add(ext);
            aggregatedVectorToExtensionSetMap.put(aggregatedVec, newExtensionSet);
            if (compare(aggregatedVec, argmax) > 0) {
                argmax = aggregatedVec;
            }

        }
        return aggregatedVectorToExtensionSetMap;
    	
    }
    
    
    
    /**
     * transforms the Map to ArrayList. It maintains the structure.
     * 
     * @param extensionsMap the Map to transform
     * @return the ArrayList 
     */
    public ArrayList<LinkedList<Extension<DungTheory>>> mapToArrayList(Map<Vector<Double>, Set<Extension<DungTheory>>> extensionsMap) {
   
        List<Map.Entry<Vector<Double>, Set<Extension<DungTheory>>>> sortedEntries = extensionsMap.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey, this::compare))
                .collect(Collectors.toList());

      
        ArrayList<LinkedList<Extension<DungTheory>>> result = new ArrayList<>();

       
        for (Map.Entry<Vector<Double>, Set<Extension<DungTheory>>> entry : sortedEntries) {
            Set<Extension<DungTheory>> rankExtensions = entry.getValue();
            LinkedList<Extension<DungTheory>> linkedList = new LinkedList<>();
            for(Extension<DungTheory> ex : rankExtensions) {
            	linkedList.add(ex);
            }
            result.add(linkedList);
        }

        return result;
    }
    
    

 

    /**
     * Set a new aggregation function.
     *
     * @param af an aggregation function.
     */
    public void setAggregationFunction(AggregationFunction af) {
        this.aggregationFunction = af;
    }

    /**
     * Get the current aggregation function.
     *
     * @return aggregation function.
     */
    public AggregationFunction getAggregationFunction() {
        return this.aggregationFunction;
    }


    /**
     * Returns the aggregated version of a vector based on the selected aggregation function of reasoner.
     * For SUM/MIN/MAX returns a 1D vector.
     * For LEXIMIN/LEXIMAX dimensions are equal to input vector.
     * For max/min of an empty vector, returns a 1D vector with 0/+inf respectively.
     * Used for calculating: aggregate(vsupp(ext,theory))
     *
     * @param vector a vector of Doubles
     * @return vector aggregated by function
     */
    private Vector<Double> aggregate(Vector<Double> vector) {
        //switch case for all aggregation types
        Vector<Double> returnVec = new Vector<>();
        switch (Objects.requireNonNull(aggregationFunction)) {

            case AVG -> {
                double sum = 0d;
                for (Double x : vector) {
                    sum += x;
                }
                returnVec.add(sum / vector.size());
            }

            case SUM -> {
                double sum = 0;
                for (double x : vector) {
                    sum += x;
                }
                returnVec.add(sum);
            }
            case MAX -> {
                double max = 0;
                for (double x : vector) {
                    if (x > max) {
                        max = x;
                    }
                }
                returnVec.add(max);
            }
            case MIN -> {
                double min = Double.POSITIVE_INFINITY;
                for (double x : vector) {
                    if (x < min) {
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
     * Result returned as in Arrays.compare(double[],double[]).
     * Used to compute argmax(aggr(vsupp))
     *
     * @param v1 first vector to compare
     * @param v2 second vector to compare
     * @return see Arrays.compare(double[] a,double[] b);
     */
    public Integer compare(Vector<Double> v1, Vector<Double> v2) {
        int returnInt;
        switch (aggregationFunction) {

            case SUM, MAX, MIN -> {
                returnInt = Double.compare(v1.get(0), v2.get(0));
                return returnInt;
            }
            case LEXIMAX, LEXIMIN -> {
                Double[] arr1 = new Double[v1.size()];
                v1.toArray(arr1);
                Double[] arr2 = new Double[v2.size()];
                v2.toArray(arr2);
                returnInt = Arrays.compare(arr1, arr2);
                return returnInt;
            }
        }
        //throw new Exception("Unsupported aggregation function");
        
        return null;


    }

    /**
     * DEPRECATED
     * Returns a value for finding the argmax from all vsupp vectors for given semantic. This is the Euclidic magnitude in most cases.
     * If the vector is empty (vsupp({},theory)) and the aggregation func is MAX / MIN, returns negative/positive infinity respectively.
     *
     * @param vec a vector of Doubles
     * @return magnitude of vector, OR +/- infinity in edge cases
     */
    public Double argmaxValue(Vector<Double> vec) {
        if (vec.get(0) == Double.POSITIVE_INFINITY) {
            return Double.POSITIVE_INFINITY;
        }
        double sum = 0d;
        for (double x : vec) {
            sum += (x * x);
        }
        return Math.sqrt(sum);
    }
    
   
    
    
}