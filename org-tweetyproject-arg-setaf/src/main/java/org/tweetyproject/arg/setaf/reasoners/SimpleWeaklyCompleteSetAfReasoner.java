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
import org.tweetyproject.commons.util.SetTools;

/**
 * This reasoner for SetAf theories computes the weakly complete extensions.
 * A weakly admissible set E is weakly complete iff no proper superset of E is weakly defended by E.
 * The implementation follows the definition and runs in exponential time.
 *
 * @see "Blümel, König, Ulbricht, 'Weak Admissibility for ABA via Abstract Set-Attacks', KR 2024"
 *
 * @author Lars Bengel
 */
public class SimpleWeaklyCompleteSetAfReasoner extends AbstractSetAfExtensionReasoner {

	/** Default */
	public SimpleWeaklyCompleteSetAfReasoner() {
		// Default
	}

	@Override
	public Collection<Extension<SetAf>> getModels(SetAf bbase) {
		SimpleWeaklyAdmissibleSetAfReasoner reasoner = new SimpleWeaklyAdmissibleSetAfReasoner();
		Collection<Extension<SetAf>> wad = reasoner.getModels(bbase);
		Collection<Extension<SetAf>> result = new HashSet<>();
		for (Extension<SetAf> ext : wad) {
			Collection<Extension<SetAf>> reductWad = reasoner.getModels(bbase.getReduct(ext));
			Collection<Argument> remaining = new HashSet<>(bbase);
			remaining.removeAll(ext);
			boolean complete = true;
			for (Set<Argument> addition : new SetTools<Argument>().subsets(remaining)) {
				if (addition.isEmpty()) continue;
				Set<Argument> superset = new HashSet<>(ext);
				superset.addAll(addition);
				if (this.isWeaklyDefendedBy(bbase, ext, superset, wad, reductWad)) {
					complete = false;
					break;
				}
			}
			if (complete) result.add(ext);
		}
		return result;
	}

	@Override
	public Extension<SetAf> getModel(SetAf bbase) {
		return this.getModels(bbase).iterator().next();
	}

	/**
	 * Checks whether ext weakly defends x, i.e. for every attack (T, h) with h in x either ext attacks T,
	 * or no weakly admissible set of the reduct contains T \ ext and some superset of x is weakly admissible.
	 * @param bbase some SetAf
	 * @param ext some set of arguments
	 * @param x some set of arguments
	 * @param wad the weakly admissible sets of bbase
	 * @param reductWad the weakly admissible sets of the reduct of bbase wrt. ext
	 * @return "true" iff ext weakly defends x
	 */
	private boolean isWeaklyDefendedBy(SetAf bbase, Extension<SetAf> ext, Collection<Argument> x,
			Collection<Extension<SetAf>> wad, Collection<Extension<SetAf>> reductWad) {
		Collection<Argument> attacked = bbase.getAttacked(ext);
		for (SetAttack att : bbase.getAttacks()) {
			if (!x.contains(att.getAttacked()) || !Collections.disjoint(att.getAttackers(), attacked))
				continue;
			Set<Argument> attacker = new HashSet<>(att.getAttackers());
			attacker.removeAll(ext);
			for (Extension<SetAf> other : reductWad)
				if (other.containsAll(attacker)) return false;
			boolean superset = false;
			for (Extension<SetAf> other : wad)
				if (other.containsAll(x)) {
					superset = true;
					break;
				}
			if (!superset) return false;
		}
		return true;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
