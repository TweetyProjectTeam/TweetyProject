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
 * Defence principle
 * <p>
 * A semantics S satisfies the defence principle if and only if
 * for every argumentation framework F, for every E in S(F) and for every argument 'a' in E, E defends 'a'.
 *
 * @see "van der Torre L, Vesic S. The Principle-Based Approach to Abstract Argumentation Semantics.
 * In: Handbook of formal argumentation, Vol. 1. College Publications; 2018. p. 2735-78."
 *
 * @author Julian Sander
 */
public class DefencePrinciple extends Principle {

	@Override
	public String getName() {
		return "Defence";
	}

	@Override
	public boolean isApplicable(Collection<Argument> kb) {
		return (kb instanceof DungTheory);
	}

	@Override
	public boolean isSatisfied(Collection<Argument> kb, AbstractExtensionReasoner ev) {
		DungTheory theory = (DungTheory) kb;
		Collection<Extension<DungTheory>> exts = ev.getModels(theory);
		for(Extension<DungTheory> ext : exts) {
			for(Argument a : ext) {
				if(!theory.isAcceptable(a,ext)) {
					return false;
				}
			}
		}
		return true;
	}
}
