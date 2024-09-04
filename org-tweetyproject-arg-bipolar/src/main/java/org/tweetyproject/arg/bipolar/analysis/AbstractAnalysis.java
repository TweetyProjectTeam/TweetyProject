package org.tweetyproject.arg.bipolar.analysis;


import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.bipolar.io.eaf.EAFToDAFConverter;
import org.tweetyproject.arg.bipolar.syntax.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AbstractAnalysis provides utility functions for concrete implementations
 *
 * @author Taha Dogan Gunes
 */
public abstract class AbstractAnalysis implements Analysis {


    /**
     * The PEAF theory to be analyzed
     */
    protected PEAFTheory peafTheory;
    /**
     * The extension reasoner that analysis will be based on
     */
    protected final AbstractExtensionReasoner extensionReasoner;
    /**
     * The type of the analysis
     */
    protected final AnalysisType analysisType;

    /**
     * The default constructor
     *
     * @param peafTheory        The PEAF Theory
     * @param extensionReasoner The extension reasoner
     * @param analysisType      The type of the analysis
     */
    public AbstractAnalysis(PEAFTheory peafTheory, AbstractExtensionReasoner extensionReasoner, AnalysisType analysisType) {
        this.peafTheory = peafTheory;
        this.extensionReasoner = extensionReasoner;
        this.analysisType = analysisType;
    }


    /**
     * Setter for peafTheory
     * @param peafTheory theory
     */
    public void setPEAFTheory(PEAFTheory peafTheory) {
        this.peafTheory = peafTheory;
    }

    /**
     * It's called when the analysis is complete.
     *
     * @param probability      the result value found as a result
     * @param noIterations     the number of iterations done for the analysis
     * @param totalProbability computed for correctness (in exact analysis)
     * @return an Analysis object
     */
    protected AnalysisResult createResult(double probability, long noIterations, double totalProbability) {
        return new AnalysisResult(probability, noIterations, this.analysisType, totalProbability);
    }

    /**
     * Computes the contribution of an induced EAF to the justification analysis.
     * <p>
     * Before computing the contribution with extensions, several checks are done.
     * 1. If the queried args are not found in the EAF, return 0.
     * 2. If there are no attacks, return 1.0.
     * 3. If the queried args have no attack, return 1.0
     * 4. Otherwise, convert EAF to DAF and run the given extension reasoner.
     * - If one of the extension has the query, return 1.0.
     *
     * @param args         the query as a set of arguments
     * @param inducibleEAF an induced EAF object
     * @return the contribution for the analysis
     */
    protected double computeContributionOfAniEAF(Set<BArgument> args, InducibleEAF inducibleEAF) {
        EAFTheory eafTheory = inducibleEAF.toNewEAFTheory();

        // If Q is not a subset of A, the contribution is zero
        if (!eafTheory.getArgumentsAsSet().containsAll(args)) {
            return 0.0;
        }

        // If R^F_a is empty, then directly return 1.0
        if (eafTheory.getAttacks().isEmpty()) {
            return 1.0;
        }

        // Create Q' (this is set of nodes that have attack relation)
        // If Q' equals to \emptyset (then return 1.0);
        Set<String> Q_prime = this.getAttackQueries(eafTheory, args);

        if (Q_prime.isEmpty()) {
            return 1.0;
        }

        // Create a DAF from arguments that are involved in an attack
        DungTheory dungTheory = EAFToDAFConverter.convert(eafTheory);

        // Solve (<A^F, R^F_a>)
        Collection<Extension<DungTheory>> extensions = extensionReasoner.getModels(dungTheory);

        for (Extension<DungTheory> extension : extensions) {
            Set<String> extensionArgs = new HashSet<>();

            for (Argument argument : extension) {
                String mainArgName = argument.getName();
                if (mainArgName.contains("_")) {
                    // Must contain all subArgsNames from an extension
                    String[] subArgsNames = mainArgName.split("_");
                    Collections.addAll(extensionArgs, subArgsNames);
                } else {
                    extensionArgs.add(mainArgName);
                }
            }

            if (extensionArgs.containsAll(Q_prime) && !extension.isEmpty()) {
                return 1.0;
            }
        }

        return 0.0;
    }

    /**
     * The helper function that return arguments (the query) that has an attack as a set
     *
     * @param eafTheory the converted iEAF to EAF object
     * @param args      the query
     * @return the names of the arguments that has an attack
     */
    private Set<String> getAttackQueries(EAFTheory eafTheory, Set<BArgument> args) {
        Set<String> Q_prime = new HashSet<String>();

        Set<Attack> attacks = eafTheory.getAttacks();
        for (Attack attack : attacks) {
            BipolarEntity froms = attack.getAttacker();
            BipolarEntity tos = attack.getAttacked();

            for (BArgument arg : args) {
                if (froms.contains(arg) || tos.contains(arg)) {
                    Q_prime.add(arg.getName());
                }
            }
        }
        return Q_prime;
    }

    /**
     * Creates a virtual DAF from EAFTheory without including the support links
     * This is not a great conversion method, however works good for queries.
     * <p>
     * FIXME: this assumes that the internal structure when attacks exist does not have a support link inside the subgraph
     *
     * @param eafTheory an EAFTheory object
     * @return a DungTheory object
     */
    @Deprecated
    protected DungTheory createDAF(EAFTheory eafTheory) {
        Map<String, Argument> argumentsInAttack = new HashMap<String, Argument>();
        DungTheory dungTheory = new DungTheory();
        for (Attack attack : eafTheory.getAttacks()) {
            String from = this.convertEArgumentsToDAFArgumentName(attack.getAttacker());

            if (!argumentsInAttack.containsKey(from)) {
                Argument fromArg = new Argument(from);
                argumentsInAttack.put(from, fromArg);
                dungTheory.add(fromArg);
            }

            String to = this.convertEArgumentsToDAFArgumentName(attack.getAttacked());
            if (!argumentsInAttack.containsKey(to)) {
                Argument toArg = new Argument(to);
                argumentsInAttack.put(to, toArg);
                dungTheory.add(toArg);
            }

            dungTheory.addAttack(argumentsInAttack.get(from), argumentsInAttack.get(to));
        }
        return dungTheory;
    }

    /**
     * Joins EArguments' names with a delimiter to create a new name for the conversion from EAF to DAF
     *
     * @param eArguments a set of arguments
     * @return the joint (sorted) name of arguments
     */
    private String convertEArgumentsToDAFArgumentName(BipolarEntity eArguments) {
        return ((ArgumentSet) eArguments).stream()
                .map(BArgument::getName)
                .sorted()
                .collect(Collectors.joining("_"));
    }


}
