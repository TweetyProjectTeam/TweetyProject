/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.dung.principles;

import java.util.Collection;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Allowing abstention principle <br>
 * A semantics satisfies the "allowing abstention principle" iff<br>
 * for every AF=(A,R) and for every a ∈ A,<br>
 * if there exist two extensions E1,E2 ∈ σ(AF) such that a ∈ E1 and a ∈ E2+,<br>
 * then there exists an extension E3 ∈ σ(AF) such that a not(∈) (E3 ∪ E3+).
 
 * 
 * @author Julian Sander
 * 
 * @see: Baroni P, Caminada M, Giacomin M. An introduction to argumentation semantics. The knowledge engineering review. 2011;26(4):365-410.
 *
 */
public class AllowingAbstentionPrinciple extends Principle {

	@Override
	public String getName() {
		return "Allowing Abstention";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb instanceof DungTheory);
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
		DungTheory theory = (DungTheory) kb;
        Collection<Extension<DungTheory>> exts = ev.getModels(theory);
        for(var a : kb) {
        	for(var ext1 : exts) {
        		if(!ext1.contains(a)) {
        			continue;
        		}        			
        		// a in E1
            	for(var ext2 : exts) {
            		if(!theory.getAttacked(ext2).contains(a)) {
            			continue;
            		}
            		// a in E2+
            		if(!searchForE3(theory, exts, a)) {
            			return false;
            		}
            	}
            }
        }
        return true;
	}

	private static boolean searchForE3(DungTheory theory, Collection<Extension<DungTheory>> exts, Argument a) {
		for(var ext3 : exts) {
			if(!ext3.contains(a) && !theory.getAttacked(ext3).contains(a)) {
				//found E3
				return true;
			}
		}
		
		return false;
	}

}
