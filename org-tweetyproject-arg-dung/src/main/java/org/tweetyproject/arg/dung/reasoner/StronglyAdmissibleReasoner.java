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
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;
import java.util.HashSet;

/**
 * Reasoner for strong admissibility
 * A set of arguments E is strongly admissible iff all every argument A in E is defended by some argument B in E \ {A}
 * i.e. no argument in E is defended only by itself
 *
 * @author Lars Bengel
 */
public class StronglyAdmissibleReasoner extends AbstractExtensionReasoner {
    @Override
    public Collection<Extension<DungTheory>> getModels(DungTheory bbase) {
        // check all admissible extensions of bbase
        Collection<Extension<DungTheory>> admExts = AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.ADM).getModels(bbase);
        Collection<Extension<DungTheory>> exts = new HashSet<>();
        for (Extension<DungTheory> ext: admExts) {
            boolean isDefended = true;
            for (Argument a: ext) {
                if (!checkStrongyDefended(bbase, ext, a)) 
                {
                	isDefended = false;
                	break;
                }  
            }
            if (isDefended)
                exts.add(ext);
        }
        return exts;
    }

    /**
     * Checks wheter a specified argument is strongly defended or not by a specified set of arguments
     * 
     * @param bbase Abstract argumentation framework in which the specified set and argument are part of
     * @param admSet Set of arguments, which might defend the specified argument
     * @param candidate Argument, which is to be examined
     * @return TRUE iff the specified argument {@link candidate} is strongly defended by the set {@link admSet}
     */
	private boolean checkStrongyDefended(DungTheory bbase, Extension<DungTheory> admSet,Argument candidate) {
		Extension<DungTheory> extWithoutCandidate = new Extension<DungTheory>(admSet);
		extWithoutCandidate.remove(candidate);
		for (Argument attacker: ((DungTheory) bbase).getAttackers(candidate)) {
		    if (!bbase.isAttacked(attacker, extWithoutCandidate)) {
		        return false;
		    }
		    
		    var defenders = bbase.getAttackers(attacker);
		    boolean atLeastOneDefenderIsDefended = false;
		    //[TERMINATION CONDITION]
		    for (Argument defender : defenders) {
		    	//[RECURSIVE CALL]
				if(checkStrongyDefended(bbase, extWithoutCandidate, defender))
				{
					atLeastOneDefenderIsDefended = true;
				}
			}
		    if(!atLeastOneDefenderIsDefended) {
		    	return false;
		    }
		}
		return true;
	}

    @Override
    public Extension<DungTheory> getModel(DungTheory bbase) {
        return this.getModels(bbase).iterator().next();
    }
    
	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}
}
