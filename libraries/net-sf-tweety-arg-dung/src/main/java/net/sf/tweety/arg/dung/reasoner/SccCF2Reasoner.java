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

package net.sf.tweety.arg.dung.reasoner;

import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;

import java.util.*;

/**
 * Reasoner for CF2 extensions using scc-recursiveness.
 *
 * definition see:
 * Baroni, Giacomin, Guida: Scc-recursiveness: A general schema for argumentation semantics 2005
 *
 * @author Lars Bengel
 */
public class SccCF2Reasoner extends AbstractExtensionReasoner {
    @Override
    public Collection<Extension> getModels(DungTheory bbase) {
        List<Collection<Argument>> sccs = new ArrayList<Collection<Argument>>(bbase.getStronglyConnectedComponents());
        // order SCCs in a DAG
        boolean[][] dag = new boolean[sccs.size()][sccs.size()];
        for(int i = 0; i < sccs.size(); i++){
            dag[i] = new boolean[sccs.size()];
            Arrays.fill(dag[i], false);
        }
        for(int i = 0; i < sccs.size(); i++)
            for(int j = 0; j < sccs.size(); j++)
                if(i != j)
                    if(bbase.isAttacked(new Extension(sccs.get(i)), new Extension(sccs.get(j))))
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
        return this.computeExtensionsViaSccs(bbase, sccs_ordered, 0, new HashSet<Argument>(), new HashSet<Argument>(), new HashSet<Argument>());
    }

    @Override
    public Extension getModel(DungTheory bbase) {
        Collection<Extension> extensions = this.getModels(bbase);
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
    private Set<Extension> computeExtensionsViaSccs(DungTheory theory, List<Collection<Argument>> sccs, int idx, Collection<Argument> in, Collection<Argument> out, Collection<Argument> undec) {
        if (idx >= sccs.size()) {
            Set<Extension> result = new HashSet<Extension>();
            result.add(new Extension(in));
            return result;
        }

        // construct theory
        DungTheory subTheory = (DungTheory) theory.getRestriction(sccs.get(idx));
        // remove all out arguments
        subTheory.removeAll(out);

        Collection<Collection<Argument>> subSccs = subTheory.getStronglyConnectedComponents();
        Collection<Extension> subExts;
        if (!out.isEmpty() && subSccs.size() > 1) {
            subExts = this.getModels(subTheory);
        } else {
            // compute naive extensions of sub theory(scc)
            subExts = new SimpleNaiveReasoner().getModels(subTheory);
        }

        Set<Extension> result = new HashSet<Extension>();
        Collection<Argument> new_in, new_out, new_undec, attacked;
        for(Extension ext: subExts){
            new_in = new HashSet<Argument>(in);
            new_out = new HashSet<Argument>(out);
            new_undec = new HashSet<Argument>(undec);
            attacked= new HashSet<Argument>();
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
