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
package org.tweetyproject.arg.aba.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.util.IncreasingSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;

/**
 * This reasoner for ABA theories performs inference on the stable extensions,
 * i.e. the closed, conflict-free sets of assumptions that attack every
 * assumption outside them.
 *
 * @param <T> the language of the underlying ABA theory
 *
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 * @author Matthias Thimm
 */
public class StableReasoner<T extends Formula> extends AdmissibleReasoner<T> {
	/** Default */
	public StableReasoner() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.arg.aba.reasoner.GeneralABAReasoner#getModels(org.
	 * tweetyproject.arg.aba.syntax.ABATheory)
	 */
	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers = getAttackers(abat);
		boolean flat = abat.isFlat();
		Collection<AbaExtension<T>> result = new HashSet<>();
		SubsetIterator<Assumption<T>> it = new IncreasingSubsetIterator<>(new HashSet<>(abat.getAssumptions()));
		l: while (it.hasNext()) {
			Set<Assumption<T>> ext = it.next();
			if (!isConflictFree(ext, attackers) || (!flat && !abat.isClosed(ext)))
				continue;
			for (Assumption<T> a : abat.getAssumptions())
				if (!ext.contains(a) && !attacks(ext, Set.of(a), attackers))
					continue l;
			result.add(new AbaExtension<T>(ext));
		}
		return result;
	}

	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}

}
