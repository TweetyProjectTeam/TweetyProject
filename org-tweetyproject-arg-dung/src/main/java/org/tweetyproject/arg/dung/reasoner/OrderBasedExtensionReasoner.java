package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.OrderBasedExtensionSemantics;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Reasoner for ranking arguments pairwise.
 * Based on "On Supported Inference and Extension Selection in Abstract Argumentation Frameworks" (S. Konieczny et al., 2015)
 */
public class OrderBasedExtensionReasoner {
    private final OrderBasedExtensionSemantics semantics;
    private final OrderBasedExtensionReasonerAggregationFunction aggregationFunction;

    /**
     * Create reasoner for specific semantic.
     *
     * @param semantics           a semantic
     * @param aggregationFunction type of aggregation to use
     */
    public OrderBasedExtensionReasoner(OrderBasedExtensionSemantics semantics, OrderBasedExtensionReasonerAggregationFunction aggregationFunction) {
        this.semantics = semantics;
        this.aggregationFunction = aggregationFunction;
    }

    public OrderBasedExtensionReasoner(){
        this.semantics = null;
        this.aggregationFunction = null;
    }

    public Collection<Extension<DungTheory>> getModels(DungTheory theory){
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

            if (aggregatedVec.get(0) > argmax.get(0)) {
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
    private Collection<Extension<DungTheory>> getExtensions(DungTheory theory){
        //make switch statement for all possible semantics
        SimplePreferredReasoner reasoner = new SimplePreferredReasoner();
        return reasoner.getModels(theory);
    }

    private Collection<Argument> getArguments(DungTheory theory){
        return theory.getNodes();
    }

    private Integer getNumberOfContainsInExtensions(Argument arg, DungTheory theory){
        Collection<Extension<DungTheory>> allExtensions = getExtensions(theory);
        int count = 0;
        for (Extension<DungTheory> ext: allExtensions) {
            if(ext.contains(arg)){
                count++;
            }

        }
        return count;
    }

    private Vector<Integer> getSupportVector(Extension<DungTheory> ext, DungTheory theory){
        Vector<Integer> vsupp = new Vector<>();
        for(Argument arg: ext){
            vsupp.add(getNumberOfContainsInExtensions(arg,theory));
        }
        return vsupp;
    }

    private Vector<Integer> aggregate(Vector<Integer> vector){
        //switch case for all aggregation types
        Vector<Integer> returnVec = new Vector<>();
        int sum = 0;
        for(Integer x: vector){
            sum += x;
        }
        returnVec.add(sum);
        return returnVec;
    }
}
