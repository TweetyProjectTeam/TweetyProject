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
import org.tweetyproject.arg.setaf.syntax.SetAf;

/**
 * This reasoner for SetAf theories computes the weakly grounded extensions,
 * i.e. the subset-minimal weakly complete extensions. These are not necessarily unique.
 *
 * @see "Blümel, König, Ulbricht, 'Weak Admissibility for ABA via Abstract Set-Attacks', KR 2024"
 *
 * @author Lars Bengel
 */
public class SimpleWeaklyGroundedSetAfReasoner extends AbstractSetAfExtensionReasoner {

	/** Default */
	public SimpleWeaklyGroundedSetAfReasoner() {
		// Default
	}

	@Override
	public Collection<Extension<SetAf>> getModels(SetAf bbase) {
		Collection<Extension<SetAf>> wco = new SimpleWeaklyCompleteSetAfReasoner().getModels(bbase);
		Collection<Extension<SetAf>> result = new HashSet<>();
		for (Extension<SetAf> e1 : wco) {
			boolean minimal = true;
			for (Extension<SetAf> e2 : wco)
				if (!e1.equals(e2) && e1.containsAll(e2)) {
					minimal = false;
					break;
				}
			if (minimal) result.add(e1);
		}
		return result;
	}

	@Override
	public Extension<SetAf> getModel(SetAf bbase) {
		return this.getModels(bbase).iterator().next();
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
