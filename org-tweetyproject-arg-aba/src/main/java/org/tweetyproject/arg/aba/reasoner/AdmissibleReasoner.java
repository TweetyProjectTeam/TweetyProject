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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.aba.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.util.SetTools;

/**
 * This reasoner for ABA theories performs inference on the admissible
 * extensions, i.e. the closed, conflict-free sets of assumptions that attack
 * every closed set attacking them.
 *
 * @param <T> the language of the underlying ABA theory
 */
public class AdmissibleReasoner<T extends Formula> extends ConflictFreeReasoner<T> {

	/** Default */
	public AdmissibleReasoner() {
	}

	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		// TODO non-flat: check closed attackers cl(T) once that is proven equivalent
		if (!abat.isFlat())
			return abat.getAllAdmissbleExtensions();
		Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers = getAttackers(abat);
		Collection<AbaExtension<T>> result = new HashSet<>();
		for (Set<Assumption<T>> ext : SetTools.powerSet(new HashSet<>(abat.getAssumptions())))
			if (isConflictFree(ext, attackers) && isAdmissible(ext, attackers))
				result.add(new AbaExtension<T>(ext));
		return result;
	}

	/**
	 * Checks whether ext attacks every minimal attacker of its members; for a
	 * flat theory and a conflict-free ext this is admissibility
	 *
	 * @param ext       a set of assumptions
	 * @param attackers the minimal attackers of each assumption
	 * @return true iff ext counter-attacks all attackers of its members
	 */
	protected boolean isAdmissible(Collection<Assumption<T>> ext,
			Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers) {
		for (Assumption<T> a : ext)
			if (!defends(ext, a, attackers))
				return false;
		return true;
	}

	/**
	 * Checks whether ext attacks every minimal attacker of a; for a flat theory
	 * this is defence
	 *
	 * @param ext       a set of assumptions
	 * @param a         an assumption
	 * @param attackers the minimal attackers of each assumption
	 * @return true iff ext defends a
	 */
	protected boolean defends(Collection<Assumption<T>> ext, Assumption<T> a,
			Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers) {
		for (Set<Assumption<T>> att : attackers.get(a))
			if (!attacks(ext, att, attackers))
				return false;
		return true;
	}

	/**
	 * Checks whether ext contains a minimal attacker of some assumption in target
	 *
	 * @param ext       a set of assumptions
	 * @param target    a set of assumptions
	 * @param attackers the minimal attackers of each assumption
	 * @return true iff ext attacks target
	 */
	protected boolean attacks(Collection<Assumption<T>> ext, Collection<Assumption<T>> target,
			Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers) {
		for (Assumption<T> t : target)
			for (Set<Assumption<T>> att : attackers.get(t))
				if (ext.containsAll(att))
					return true;
		return false;
	}
}
