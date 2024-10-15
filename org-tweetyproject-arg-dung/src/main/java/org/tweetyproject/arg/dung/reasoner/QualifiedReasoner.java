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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Reasoner for qualified sigma-semantics. If sigma is a {@link org.tweetyproject.arg.dung.principles.SccDecomposabilityPrinciple scc-decomposable}
 * semantics this reasoner will compute the qualified extensions for the semantics.
 * Under qualified semantics an UNDECIDED argument x is treated as OUT.
 * This means an argument y attacked by x and in a different scc can still be IN.
 *
 * @see "Jeremie Dauphin, Tjitze Rienstra, and Leendert Van Der Torre. 'A principle-based analysis of weakly admissible semantics', Proceedings of COMMA'20, (2020)"
 *
 * @author Lars Bengel
 */
public class QualifiedReasoner extends AbstractExtensionReasoner {
    private final AbstractExtensionReasoner baseReasoner;

    /**
     * initialize reasoner with the given semantics as base function.
     * Will only produce meaningful results if the given semantics is SCC-decomposable
     * @param semantics a scc-decomposable semantics
     */
    public QualifiedReasoner(Semantics semantics) {
        this.baseReasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
    }

    /**
     * initialize reasoner with the given reasoner as base reasoner
     * Will only produce meaningful results if the given semantics is SCC-decomposable
     * @param reasoner a reasoner for a scc-decomposable semantics
     */
    public QualifiedReasoner(AbstractExtensionReasoner reasoner) {
        this.baseReasoner = reasoner;
    }

    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        List<Collection<Argument>> sccs = new ArrayList<>(bbase.getStronglyConnectedComponents());
        // order SCCs in a DAG
        boolean[][] dag = new boolean[sccs.size()][sccs.size()];
        for(int i = 0; i < sccs.size(); i++){
            dag[i] = new boolean[sccs.size()];
            Arrays.fill(dag[i], false);
        }
        for(int i = 0; i < sccs.size(); i++)
            for(int j = 0; j < sccs.size(); j++)
                if(i != j)
                    if(bbase.isAttacked(new Extension<>(sccs.get(i)), new Extension<>(sccs.get(j))))
                        dag[i][j] = true;
        // order SCCs topologically
        List<Collection<Argument>> sccs_ordered = new ArrayList<>();
        while(sccs_ordered.size() < sccs.size()){
            for(int i = 0; i < sccs.size();i++){
                if(sccs_ordered.contains(sccs.get(i)))
                    continue;
                boolean isNull = true;
                for(int j = 0; j < sccs.size(); j++)
                    if(dag[i][j]){
                        isNull = false;
                        break;
                    }
                if(isNull){
                    sccs_ordered.add(sccs.get(i));
                    for(int j = 0; j < sccs.size(); j++)
                        dag[j][i] = false;
                }
            }
        }
        return this.computeExtensionsViaSccs(bbase, sccs_ordered, 0, new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        Collection<Extension<DungTheory>> extensions = this.getModels(bbase);
        return extensions.iterator().next();
    }

    /**
     * Computes extensions recursively following the SCC structure.
     * @param theory the theory
     * @param sccs all SCCs topologically sorted
     * @param idx the current SCC to be processed
     * @param in all arguments currently in
     * @param out all arguments currently out
     * @param undec all arguments currently undecided
     * @return the set of extensions
     */
    private Set<Extension<DungTheory>> computeExtensionsViaSccs(DungTheory theory, List<Collection<Argument>> sccs, int idx, Collection<Argument> in, Collection<Argument> out, Collection<Argument> undec) {
        if (idx >= sccs.size()) {
            Set<Extension<DungTheory>> result = new HashSet<>();
            result.add(new Extension<>(in));
            return result;
        }

        // construct theory
        DungTheory subTheory = (DungTheory) theory.getRestriction(sccs.get(idx));
        // remove all out arguments
        subTheory.removeAll(out);

        Collection<Collection<Argument>> subSccs = subTheory.getStronglyConnectedComponents();
        Collection<Extension<DungTheory>> subExts;
        if (!out.isEmpty() && subSccs.size() > 1) {
            subExts = this.getModels(subTheory);
        } else {
            // compute complete extensions of sub theory(scc)
            subExts = this.baseReasoner.getModels(subTheory);
        }

        Set<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
        Collection<Argument> new_in, new_out, new_undec, attacked;
        for(Extension<DungTheory> ext: subExts){
            new_in = new HashSet<>(in);
            new_out = new HashSet<>(out);
            new_undec = new HashSet<>(undec);
            attacked= new HashSet<>();
            new_in.addAll(ext);
            for(Argument a: ext)
                attacked.addAll(theory.getAttacked(a));
            new_out.addAll(attacked);

            for(Argument a: subTheory)
                if(!ext.contains(a) && !attacked.contains(a))
                    new_undec.add(a);

            result.addAll(this.computeExtensionsViaSccs(theory, sccs, idx+1, new_in, new_out, new_undec));
        }
        return result;
    }
}
