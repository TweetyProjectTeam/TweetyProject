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
    public OrderBasedExtensionReasoner(Semantics semantics) {
        this.semantics = semantics;
        this.aggregationFunction = null;
    }
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

    private Collection<Argument> getAllArguments(DungTheory theory){
        return theory.getNodes();
    }

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

    private Vector<Integer> getSupportVector(Extension<DungTheory> ext, DungTheory theory) throws Exception {
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
