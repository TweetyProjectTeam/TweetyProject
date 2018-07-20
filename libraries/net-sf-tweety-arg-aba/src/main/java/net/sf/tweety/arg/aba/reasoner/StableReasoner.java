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
 package net.sf.tweety.arg.aba.reasoner;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.aba.semantics.AbaExtension;
import net.sf.tweety.arg.aba.syntax.ABATheory;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.Formula;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 * @author Matthias Thimm
 * This reasoner for ABA theories performs inference on the stable extensions.
 * @param <T>	the language of the underlying ABA theory
 */
public class StableReasoner<T extends Formula> extends GeneralABAReasoner<T> {

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aba.reasoner.GeneralABAReasoner#getModels(net.sf.tweety.arg.aba.syntax.ABATheory)
	 */
	@Override
	public Collection<AbaExtension<T>> getModels(ABATheory<T> abat) {
		Collection<AbaExtension<T>>result = new HashSet<>();
		Collection<Collection<Assumption<T>>> exts = abat.getAllExtensions();
		for(Collection<Assumption<T>> ext : exts) {
			if(!abat.isConflictFree(ext)) 
				continue;
			if (!abat.isClosed(ext)) 
				continue;
			for(Assumption<T> a: abat.getAssumptions()) {
				if(!abat.attacks(ext, Arrays.asList(a)))
					continue;
			}
			result.add(new AbaExtension<T>(ext));
		}
		return result;
	}

}
