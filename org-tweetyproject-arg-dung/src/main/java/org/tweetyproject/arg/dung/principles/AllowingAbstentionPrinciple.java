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
import java.util.HashSet;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Allowing abstention principle
 * <p>
 * A semantics satisfies allowing abstention iff for all arguments 'a', it holds that:
 * if there is some extension S with 'a' in S and some extension S' with 'a' in S'^+, then there is some extension
 * S'' with 'a' not in S'' or S''^+.
 *
 * @see "Baroni P, Caminada M, Giacomin M. An introduction to argumentation semantics. The knowledge engineering review. 2011;26(4):365-410."
 *
 * @author Julian Sander
 * @author Lars Bengel
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
		Collection<Argument> accepted = new HashSet<>();
		Collection<Argument> attacked = new HashSet<>();
		for (Extension<DungTheory> extension: exts) {
			accepted.addAll(extension);
			attacked.addAll(theory.getAttacked(extension));
		}

		// only retain arguments that are both credulously accepted and also rejected by some extension
		accepted.retainAll(attacked);

        for(Argument arg: accepted) {
			boolean abstention = false;
        	for (Extension<DungTheory> extension: exts) {
				if (!extension.contains(arg) && !theory.isAttacked(arg, extension)) {
					abstention = true;
					break;
				}
			}
			if (!abstention) return false;
        }
        return true;
	}

}
