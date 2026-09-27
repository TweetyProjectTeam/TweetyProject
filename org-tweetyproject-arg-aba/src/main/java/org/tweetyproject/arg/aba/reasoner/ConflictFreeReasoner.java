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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.util.SetTools;

/**
 * This reasoner for ABA theories performs inference on the conflict-free
 * extensions, i.e. the sets of assumptions that do not attack themselves.
 *
 * @param <T> the language of the underlying ABA theory
 */
public class ConflictFreeReasoner<T extends Formula> extends GeneralAbaReasoner<T> {

	/** Default */
	public ConflictFreeReasoner() {
	}

	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers = getAttackers(abat);
		Collection<AbaExtension<T>> result = new HashSet<>();
		for (Set<Assumption<T>> ext : SetTools.powerSet(new HashSet<>(abat.getAssumptions())))
			if (isConflictFree(ext, attackers))
				result.add(new AbaExtension<T>(ext));
		return result;
	}

	/**
	 * Maps each assumption to the minimal supports of its contraries, i.e. the
	 * minimal sets of assumptions that attack it
	 *
	 * @param abat an ABA theory
	 * @return the minimal attackers of each assumption
	 */
	protected Map<Assumption<T>, Set<Set<Assumption<T>>>> getAttackers(AbaTheory<T> abat) {
		Map<T, Set<Set<Assumption<T>>>> supports = abat.getMinimalSupports();
		Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers = new HashMap<>();
		for (Assumption<T> a : abat.getAssumptions()) {
			Set<Set<Assumption<T>>> s = new HashSet<>();
			for (T c : abat.getContraries(a.getConclusion()))
				s.addAll(supports.getOrDefault(c, Set.of()));
			attackers.put(a, s);
		}
		return attackers;
	}

	/**
	 * Checks whether ext contains a minimal attacker of one of its members
	 *
	 * @param ext       a set of assumptions
	 * @param attackers the minimal attackers of each assumption
	 * @return true iff ext does not attack itself
	 */
	protected boolean isConflictFree(Collection<Assumption<T>> ext,
			Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers) {
		for (Assumption<T> a : ext)
			for (Set<Assumption<T>> att : attackers.get(a))
				if (ext.containsAll(att))
					return false;
		return true;
	}

	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}
}
