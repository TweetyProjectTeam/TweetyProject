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
 * This reasoner for ABA theories performs inference on the complete extensions.
 * @param <T>	the language of the underlying ABA theory
 *
 * @author Nils Geilen (geilenn@uni-koblenz.de)
 * @author Matthias Thimm
 */
public class CompleteReasoner<T extends Formula> extends AdmissibleReasoner<T> {

	/** Default */
	public CompleteReasoner() {
	}


	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.aba.reasoner.GeneralABAReasoner#getModels(org.tweetyproject.arg.aba.syntax.ABATheory)
	 */
	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		if (abat.isFlat())
			return getFlatModels(abat);
		Collection<AbaExtension<T>> result = new HashSet<>();
		Collection<AbaExtension<T>> exts = abat.getAllAdmissbleExtensions();
		l:for(Collection<Assumption<T>> ext : exts) {
			for(Assumption<T> a: abat.getAssumptions()) {
				if(!ext.contains(a)&&abat.defends(ext, a)){
					continue l;
				}
			}
			result.add(new AbaExtension<T>(ext));
		}
		return result;
	}

	/**
	 * Admissible sets that contain every assumption they defend
	 */
	private Collection<AbaExtension<T>> getFlatModels(AbaTheory<T> abat) {
		Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers = getAttackers(abat);
		Collection<AbaExtension<T>> result = new HashSet<>();
		SubsetIterator<Assumption<T>> it = new IncreasingSubsetIterator<>(new HashSet<>(abat.getAssumptions()));
		l: while (it.hasNext()) {
			Set<Assumption<T>> ext = it.next();
			if (!isConflictFree(ext, attackers) || !isAdmissible(ext, attackers))
				continue;
			for (Assumption<T> a : abat.getAssumptions())
				if (!ext.contains(a) && defends(ext, a, attackers))
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