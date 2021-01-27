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
 /**
 * 
 */
package org.tweetyproject.arg.aba.reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.commons.Formula;

/**
 * This reasoner for ABA theories performs inference on the ideal extension.
 * @param <T>	the language of the underlying ABA theory
 * 
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 * @author Matthias Thimm
 */
public class WellFoundedReasoner<T extends Formula> extends GeneralAbaReasoner<T> {

	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.aba.reasoner.GeneralABAReasoner#getModels(org.tweetyproject.arg.aba.syntax.ABATheory)
	 */
	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		Collection<AbaExtension<T>> complete_exts = new CompleteReasoner<T>().getModels(abat);
		Iterator<AbaExtension<T>> iter = complete_exts.iterator();
		AbaExtension<T> ext = iter.hasNext() ? iter.next() : new AbaExtension<T>();
		while (iter.hasNext()) {
			ext.retainAll(iter.next());
		}
		Collection<AbaExtension<T>>result = new HashSet<>();
		result.add(ext);
		return result;
	}
}
