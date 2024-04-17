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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.dung.reasoner;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Universal scc-recursive reasoner
 * initialize with any reasoner for a scc-recursive semantics
 *
 * @author Lars Bengel
 */
public class SimpleSccRecursiveReasoner extends AbstractExtensionReasoner {

    /** The base reasoner applied on each SCC individually*/
    private AbstractExtensionReasoner baseReasoner;

    /**
     * initialize the scc-recursive reasoner with the given reasoner as base function
     * @param reasoner a reasoner for any scc-recursive semantics
     */
    public SimpleSccRecursiveReasoner(AbstractExtensionReasoner reasoner) {
        this.baseReasoner = reasoner;
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModels(org.tweetyproject.arg.dung.syntax.DungTheory)
     */
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
        return this.computeExtensionsViaSccs((DungTheory)bbase, sccs_ordered, 0, new HashSet<Argument>(), new HashSet<Argument>(), new HashSet<Argument>());
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner#getModel(org.tweetyproject.arg.dung.syntax.DungTheory)
     */
    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        return this.getModels(bbase).iterator().next();
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
    private Set<Extension<DungTheory>> computeExtensionsViaSccs(DungTheory theory, List<Collection<Argument>> sccs, int idx, Collection<Argument> in, Collection<Argument> out, Collection<Argument> undec){
        if(idx >= sccs.size()){
            Set<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
            result.add(new Extension<DungTheory>(in));
            return result;
        }
        // construct theory
        DungTheory subTheory = (DungTheory) theory.getRestriction(sccs.get(idx));
        // remove all out arguments
        subTheory.removeAll(out);
        // for all arguments that are attacked by an already undecided argument outside the scc, add attack
        // from an auxiliary self-attacking argument
        Argument aux = new Argument("_aux_argument8937");
        subTheory.add(aux);
        subTheory.add(new Attack(aux,aux));
        for(Argument a: subTheory)
            if(theory.isAttacked(a, new Extension<DungTheory>(undec)))
                subTheory.add(new Attack(aux,a));
        // compute complete extensions of sub theory
        Collection<Extension<DungTheory>> subExt = baseReasoner.getModels(subTheory);
        Set<Extension<DungTheory>> result = new HashSet<Extension<DungTheory>>();
        Collection<Argument> new_in, new_out, new_undec, attacked;
        for(Extension<DungTheory> ext: subExt){
            new_in = new HashSet<Argument>(in);
            new_out = new HashSet<Argument>(out);
            new_undec = new HashSet<Argument>(undec);
            attacked= new HashSet<Argument>();
            new_in.addAll(ext);
            for(Argument a: ext)
                attacked.addAll(theory.getAttacked(a));
            new_out.addAll(attacked);
            for(Argument a: subTheory)
                if(a != aux && !ext.contains(a) && !attacked.contains(a))
                    new_undec.add(a);
            result.addAll(this.computeExtensionsViaSccs(theory, sccs, idx+1, new_in, new_out, new_undec));
        }
        return result;
    }
}
