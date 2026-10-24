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
import java.util.HashSet;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.commons.util.IncreasingSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;

/**
 * This reasoner for SetAf theories computes the strongly admissible extensions.
 * A set E is strongly admissible iff it is conflict-free and every argument in E is defended
 * by a strongly admissible subset of E that does not contain it. Equivalently, iterating faf
 * restricted to E from the empty set reaches E. Every strongly admissible set is contained
 * in the grounded extension, so only its subsets are checked.
 *
 * @see "Berthold, Blümel, Rapberger, 'On Strong and Weak Admissibility in Non-Flat Assumption-Based Argumentation', 2025"
 *
 * @author Lars Bengel
 */
public class SimpleStronglyAdmissibleSetAfReasoner extends AbstractSetAfExtensionReasoner {

	/** Default */
	public SimpleStronglyAdmissibleSetAfReasoner() {
		// Default
	}

	@Override
	public Collection<Extension<SetAf>> getModels(SetAf bbase) {
		Collection<Extension<SetAf>> result = new HashSet<>();
		SubsetIterator<Argument> it = new IncreasingSubsetIterator<>(new HashSet<>(new SimpleGroundedSetAfReasoner().getModel(bbase)));
		while (it.hasNext()) {
			Extension<SetAf> ext = new Extension<>(it.next());
			if (this.isStronglyAdmissible(bbase, ext))
				result.add(ext);
		}
		return result;
	}

	@Override
	public Extension<SetAf> getModel(SetAf bbase) {
		// the empty set is always strongly admissible
		return new Extension<>();
	}

	/**
	 * Checks whether the given set is strongly admissible, i.e. whether it is conflict-free
	 * and iterating faf restricted to the set from the empty set reaches the whole set.
	 * @param bbase some SetAf
	 * @param ext some set of arguments
	 * @return "true" iff ext is strongly admissible in bbase
	 */
	public boolean isStronglyAdmissible(SetAf bbase, Extension<SetAf> ext) {
		if (!bbase.isConflictFree(ext)) return false;
		Extension<SetAf> defended = new Extension<>();
		int size;
		do {
			size = defended.size();
			defended = bbase.faf(defended);
			defended.retainAll(ext);
		} while (size != defended.size());
		return defended.size() == ext.size();
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
