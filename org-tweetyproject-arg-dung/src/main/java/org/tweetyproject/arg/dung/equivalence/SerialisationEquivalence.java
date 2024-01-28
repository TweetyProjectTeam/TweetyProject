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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import org.tweetyproject.arg.dung.reasoner.serialisable.SerialisableExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

/**
 * This class represents a comparator for deciding whether two {@link DungTheory Argumentation Frameworks} are equivalent
 * wrt. their serialisation sequences for some semantics
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class SerialisationEquivalence implements Equivalence<DungTheory> {

	/** the semantics by which the argumentation frameworks are compared */
	private final Semantics semantics;

	public SerialisationEquivalence(Semantics semantics) {
		this.semantics = semantics;
	}

	@Override
	public boolean isEquivalent(DungTheory obj1, DungTheory obj2) {
		SerialisableExtensionReasoner reasoner = SerialisableExtensionReasoner.getSerialisableReasonerForSemantics(this.semantics);
		return reasoner.getSequences(obj1).equals(reasoner.getSequences(obj2));
	}

	@Override
	public boolean isEquivalent(Collection<DungTheory> theories) {
		DungTheory first = theories.iterator().next();
		for (DungTheory theory : theories) {
			if(theory == first) {
				continue;
			}
			if(!this.isEquivalent(theory, first)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getDescription() {
		return "Serialisation Equivalence";
	}
}
