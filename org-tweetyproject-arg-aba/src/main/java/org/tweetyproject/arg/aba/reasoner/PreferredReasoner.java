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

import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.commons.Formula;

/**
 * This reasoner for ABA theories performs inference on the preferred
 * extensions.
 *
 * @param <T> the language of the underlying ABA theory
 *
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 * @author Matthias Thimm
 */
public class PreferredReasoner<T extends Formula> extends GeneralAbaReasoner<T> {
	/** Default */
	public PreferredReasoner() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.tweetyproject.arg.aba.reasoner.GeneralABAReasoner#getModels(org.
	 * tweetyproject.arg.aba.syntax.ABATheory)
	 */
	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		Collection<AbaExtension<T>> result = new HashSet<>();
		Collection<AbaExtension<T>> exts = abat.getAllAdmissbleExtensions();
		l: for (AbaExtension<T> ext : exts) {
			for (AbaExtension<T> ext2 : exts) {
				if (ext2 != ext && ext2.containsAll(ext))
					continue l;
			}
			result.add(ext);
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
