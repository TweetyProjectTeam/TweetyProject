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
package org.tweetyproject.arg.setaf.reasoners;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.arg.setaf.syntax.SetAttack;

/**
 * This reasoner for SetAf theories computes the weakly admissible extensions.
 * A set E is weakly admissible iff it is conflict-free and for every attack (T, h) on E with
 * T not attacked by E, no weakly admissible set of the E-reduct contains T \ E.
 * Self-attacking arguments are removed beforehand, as they do not change the result.
 * The implementation follows the recursive definition and runs in exponential time.
 *
 * @see "Blümel, König, Ulbricht, 'Weak Admissibility for ABA via Abstract Set-Attacks', KR 2024"
 *
 * @author Lars Bengel
 */
public class SimpleWeaklyAdmissibleSetAfReasoner extends AbstractSetAfExtensionReasoner {

	/** Default */
	public SimpleWeaklyAdmissibleSetAfReasoner() {
		// Default
	}

	@Override
	public Collection<Extension<SetAf>> getModels(SetAf bbase) {
		Collection<Argument> arguments = new HashSet<>();
		for (Argument a : bbase)
			if (!bbase.containsAttack(new SetAttack(a, a)))
				arguments.add(a);
		SetAf theory = bbase.getRestriction(arguments);
		Collection<Extension<SetAf>> result = new HashSet<>();
		for (Extension<SetAf> ext : new SimpleConflictFreeSetAfReasoner().getModels(theory))
			if (this.isWeaklyAdmissible(theory, ext))
				result.add(ext);
		return result;
	}

	@Override
	public Extension<SetAf> getModel(SetAf bbase) {
		// the empty set is always weakly admissible
		return new Extension<>();
	}

	/**
	 * Checks whether the given set is weakly admissible, i.e. whether it is conflict-free and
	 * no weakly admissible set of the reduct contains the remainder of an attacker not attacked by the set.
	 * @param bbase some SetAf
	 * @param ext some set of arguments
	 * @return "true" iff ext is weakly admissible in bbase
	 */
	public boolean isWeaklyAdmissible(SetAf bbase, Extension<SetAf> ext) {
		if (!bbase.isConflictFree(ext)) return false;
		Collection<Argument> attacked = bbase.getAttacked(ext);
		Collection<Set<Argument>> attackers = new HashSet<>();
		for (SetAttack att : bbase.getAttacks()) {
			if (!ext.contains(att.getAttacked()) || !Collections.disjoint(att.getAttackers(), attacked))
				continue;
			Set<Argument> attacker = new HashSet<>(att.getAttackers());
			attacker.removeAll(ext);
			attackers.add(attacker);
		}
		if (attackers.isEmpty()) return true;
		for (Extension<SetAf> other : this.getModels(bbase.getReduct(ext)))
			for (Set<Argument> attacker : attackers)
				if (other.containsAll(attacker))
					return false;
		return true;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
