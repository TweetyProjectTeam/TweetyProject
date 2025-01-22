/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.independence;

import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.graphs.*;

import java.util.*;

/**
 * This class implements the concepts of conditional independence for sets of arguments in abstract argumentation frameworks
 *
 * @see "Tjitze Rienstra, et al. 'Independence and D-separation in Abstract Argumentation', Proceedings of KR'20, (2020)"
 *
 * @author Lars Bengel
 */
public class Independence {

    /**
     * Compute whether argsA and argsB are independent given argsC in the given AF
     * argsA, argsB and argsC are disjoint
     * @param theory a dung theory
     * @param argsA a set of arguments
     * @param argsB a set of arguments
     * @param argsC a set of arguments
     * @param pruneOutAttacks a flag indicating if attacks from arguments that are always out should be pruned
     * @return true iff argsA and argsB are independent given argsC in theory
     */
    public static boolean isIndependent(DungTheory theory, Collection<Argument> argsA, Collection<Argument> argsB, Collection<Argument> argsC, boolean pruneOutAttacks) {
        // prune theory first if the flag is set to true
        if (pruneOutAttacks) {
            theory = Independence.pruneTheory(theory);
        }

        // transform theory into d-graph
        SimpleGraph<Argument> dGraph = Independence.computeDGraph(theory);

        // compute d-separation, following the idea from:
        // Adnan Darwiche. 'Modeling and reasoning with Bayesian networks', Cambridge university press, (2009)
        // prune the d-graph and convert to undirected graph
        SimpleGraph<Argument> prunedDGraph = Independence.pruneDGraph(dGraph, argsA, argsB, argsC);
        prunedDGraph = prunedDGraph.toUndirectedGraph();

        // if argsA and argsB are disconnected in the pruned d-graph, then they are d-separated and thus also independent in the given theory
        return prunedDGraph.areDisconnected(argsA, argsB);
    }

    /**
     * Compute whether argsA and argsB are independent given argsC in the given AF
     * argsA, argsB and argsC are disjoint
     * @param theory a dung theory
     * @param argsA a set of arguments
     * @param argsB a set of arguments
     * @param argsC a set of arguments
     * @return true iff argsA and argsB are independent given argsC in theory
     */
    public static boolean isIndependent(DungTheory theory, Collection<Argument> argsA, Collection<Argument> argsB, Collection<Argument> argsC) {
        return Independence.isIndependent(theory, argsA, argsB, argsC, false);
    }

    /**
     * Compute the smallest set of arguments which needs to be observed so that argsA and argsB are independent in the given AF
     * argsA and argsB are disjoint
     * @param theory a dung theory
     * @param argsA a set of arguments
     * @param argsB a set of arguments
     * @param pruneOutAttacks a flag indicating if attacks from arguments that are always out should be pruned
     * @return the smallest set of arguments which we need to observe so that argsA and argsB are independent
     */
    public static Collection<Collection<Argument>> isIndependentGiven(DungTheory theory, Collection<Argument> argsA, Collection<Argument> argsB, boolean pruneOutAttacks) {
        Collection<Collection<Argument>> result = new HashSet<>();
        int setSize = Integer.MAX_VALUE;

        // all arguments neither in argsA or argsB
        Collection<Argument> spareArguments = new HashSet<>(theory);
        spareArguments.removeAll(argsA);
        spareArguments.removeAll(argsB);

        // sort subsets by size
        List<Set<Argument>> subsets = new ArrayList<>(new SetTools<Argument>().subsets(spareArguments));
        subsets.sort(Comparator.comparing(Collection::size));

        // try each subset beginning with the smallest
        for (Collection<Argument> subset: subsets) {
            if (subset.size() > setSize) {
                // if the subset is bigger than the first found set, stop searching
                break;
            }
            if (Independence.isIndependent(theory, argsA, argsB, subset, pruneOutAttacks)) {
                // first subset which makes argsA and argsB independent determines the size of the sets
                setSize = subset.size();
                result.add(subset);
            }
        }
        return result;
    }

    /**
     * Compute the smallest set of arguments which needs to be observed so that argsA and argsB are independent in the given AF
     * argsA and argsB are disjoint
     * @param theory a dung theory
     * @param argsA a set of arguments
     * @param argsB a set of arguments
     * @return the smallest set of arguments which we need to observe so that argsA and argsB are independent
     */
    public static Collection<Collection<Argument>> isIndependentGiven(DungTheory theory, Collection<Argument> argsA, Collection<Argument> argsB) {
        return Independence.isIndependentGiven(theory, argsA, argsB, false);
    }

    /**
     * Transform the given AF into a DAG (D-graph) by adding new meta argument for each strongly connected component
     * @param theory a dung theory
     * @return the d-graph of the given AF
     */
    public static SimpleGraph<Argument> computeDGraph (DungTheory theory) {
        Collection<Collection<Argument>> sccs = theory.getStronglyConnectedComponents();

        SimpleGraph<Argument> dGraph = new SimpleGraph<>();
        int counter = 0;
        for (Collection<Argument> scc: sccs) {
            counter++;
            // add new latent common cause variable s_i to d-graph
            Argument lccVar = new Argument("s_" + counter);
            dGraph.add(lccVar);
            for (Argument arg: scc) {
                // add argument to d-graph, add edge from s_i to argument
                dGraph.add(arg);
                dGraph.add(new DirectedEdge<>(lccVar, arg));

                // for every parent add edge from parent to s_i
                for (Argument parent: theory.getAttackers(arg)) {
                    if (!scc.contains(parent)) {
                        try {
                            dGraph.add(new DirectedEdge<>(parent, lccVar));
                        } catch (IllegalArgumentException e) {
                            dGraph.add(parent);
                            dGraph.add(new DirectedEdge<>(parent, lccVar));
                        }
                    }
                }
            }
        }
        return dGraph;
    }

    /**
     * Prune the given AF by removing all attacks from arguments which are attacked by unattacked arguments
     * i.e. remove attacks from arguments that are out in the grounded labeling of the AF
     * @param theory a dung theory
     * @return the pruned theory
     */
    private static DungTheory pruneTheory(DungTheory theory) {
        Extension<DungTheory> grounded = new SimpleGroundedReasoner().getModel(theory);
        Labeling groundedLabeling = new Labeling(theory, grounded);

        // copy theory
        DungTheory prunedTheory = new DungTheory();
        prunedTheory.addAll(theory);
        prunedTheory.addAllAttacks(theory.getAttacks());

        // remove all attacks coming from arguments that are OUT in the grounded labeling
        for (Argument arg: groundedLabeling.getArgumentsOfStatus(ArgumentStatus.OUT)) {
            for (Argument b: theory.getAttacked(arg)) {
                prunedTheory.remove(new Attack(arg, b));
            }
        }
        return prunedTheory;
    }

    /**
     * Prune the given DAG by removing all leaf nodes, which are not in any of the given sets and removing
     * all outgoing edges from nodes in argsC
     * @param dGraph a DAG
     * @param argsA a set of arguments
     * @param argsB a set of arguments
     * @param argsC a set of arguments
     * @return the pruned DAG
     */
    public static SimpleGraph<Argument> pruneDGraph(SimpleGraph<Argument> dGraph, Collection<Argument> argsA, Collection<Argument> argsB, Collection<Argument> argsC) {
        Collection<Argument> allArguments = new HashSet<>(argsA);
        allArguments.addAll(argsB);
        allArguments.addAll(argsC);

        // copy the given dGraph
        SimpleGraph<Argument> prunedDAG = new SimpleGraph<>(dGraph);

        // delete all leaf nodes which are not in any of the three sets
        // all nodes not in any of the three given sets can be removed
        Collection<Argument> removalCandidates = new HashSet<>(dGraph.getNodes());
        removalCandidates.removeAll(allArguments);
        boolean changed = true;
        // as long as we delete new node, continue process
        while (changed) {
            // set change-flag to false
            changed = false;
            for (Argument a : removalCandidates) {
                if (prunedDAG.contains(a) && prunedDAG.getChildren(a).isEmpty()) {
                    // leaf node found, remove and set change-flag to true
                    prunedDAG.remove(a);
                    changed = true;
                }
            }
        }

        // remove all attacks from elements in argsC
        for (Argument c: argsC) {
            for (Argument a: prunedDAG.getChildren(c)) {
                prunedDAG.remove(new DirectedEdge<>(c, a));
            }
        }
        return prunedDAG;
    }
}
