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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.*;
import org.tweetyproject.arg.dung.syntax.*;

import java.util.*;

/**
 * Reasoner for semi-qualified sigma-semantics. If sigma is a scc-decomposable semantics this reasoner will compute the
 * qualified extensions for the semantics. In qualified semantics a undecided argument x is treated as out iff there are no other possible labelings where x is in. This means
 * an argument y attacked by x and in a different scc can still be accepted(in) iff x is undecided or out in all possible labelings.
 *
 * see: TODO add reference
 *
 * @author Lars Bengel
 */
public class SemiQualifiedReasoner extends AbstractExtensionReasoner {
    private AbstractExtensionReasoner baseReasoner;

    /**
     * initialize reasoner with the given semantics as base function.
     * Will only produce meaningful results if the given semantics is SCC-decomposable
     * @param semantics a scc-decomposable semantics
     */
    public SemiQualifiedReasoner(Semantics semantics) {
        this.baseReasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
    }

    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        List<Collection<Argument>> sccs = new ArrayList<Collection<Argument>>(((DungTheory)bbase).getStronglyConnectedComponents());
        // order SCCs in a DAG
        boolean[][] dag = new boolean[sccs.size()][sccs.size()];
        for(int i = 0; i < sccs.size(); i++){
            dag[i] = new boolean[sccs.size()];
            Arrays.fill(dag[i], false);
        }
        for(int i = 0; i < sccs.size(); i++)
            for(int j = 0; j < sccs.size(); j++)
                if(i != j)
                    if(((DungTheory)bbase).isAttacked(new Extension<DungTheory>(sccs.get(i)), new Extension<DungTheory>(sccs.get(j))))
                        dag[i][j] = true;
        // order SCCs topologically
        List<Collection<Argument>> sccs_ordered = new ArrayList<Collection<Argument>>();
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
        return this.computeExtensionsViaSccs((DungTheory) bbase, sccs_ordered, 0, new HashSet<Argument>(), new HashSet<Argument>(), new HashSet<Argument>());
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
            result.add(new Extension<DungTheory>(in));
            return result;
        }

        // construct theory
        DungTheory subTheory = (DungTheory) theory.getRestriction(sccs.get(idx));
        // remove all out arguments
        subTheory.removeAll(out);

        // for all arguments that are attacked by an already undecided argument outside the scc, add attack
        // from an auxiliary self-attacking argument
        Collection<Argument> attacked_by_undec = new HashSet<>();
        for (Argument a: undec) {
            for (Argument b: theory.getAttacked(a)) {
                if (!sccs.get(idx).contains(a) && sccs.get(idx).contains(b)) {
                    attacked_by_undec.add(b);
                }
            }

        }
        Argument aux = new Argument("_aux_argument8937");
        if (!attacked_by_undec.isEmpty()) {
            subTheory.add(aux);
            subTheory.add(new Attack(aux, aux));
            for (Argument a : attacked_by_undec) {
                subTheory.add(new Attack(aux, a));
            }
        }

        Collection<Collection<Argument>> subSccs = subTheory.getStronglyConnectedComponents();
        Collection<Extension<DungTheory>> subExts;
        // if the sub theory is no longer a scc after removing out arguments,
        // apply algorithm recursively on sub-sccs
        if (!out.isEmpty() && subSccs.size() > 1) { //TODO better condition
            subExts = this.getModels(subTheory);
        } else {
            // compute complete extensions of sub theory(scc)
            subExts = this.baseReasoner.getModels(subTheory);
        }

        Set<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
        Collection<Argument> new_in, new_out, new_undec, attacked;

        // compute set of arguments S { a | a element of some extension of the theory}
        Collection<Argument> in_arguments = new HashSet<>();
        for (Extension<DungTheory> ext: subExts) {
            in_arguments.addAll(ext);
        }

        for(Extension<DungTheory> ext: subExts){
            new_in = new HashSet<>(in);
            new_out = new HashSet<>(out);
            new_undec = new HashSet<>(undec);
            attacked= new HashSet<>();
            new_in.addAll(ext);
            for(Argument a: ext)
                attacked.addAll(theory.getAttacked(a));
            new_out.addAll(attacked);

            for(Argument a: subTheory) {
                if (a != aux && !ext.contains(a) && !attacked.contains(a)) {
                    if (in_arguments.contains(a) || attacked_by_undec.contains(a)) {
                        new_undec.add(a);
                    } else {
                        new_out.add(a);
                    }
                } else if (a == aux) {
                    new_undec.add(a);
                }
            }

            result.addAll(this.computeExtensionsViaSccs(theory, sccs, idx+1, new_in, new_out, new_undec));
        }
        return result;
    }
    
	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}
}
