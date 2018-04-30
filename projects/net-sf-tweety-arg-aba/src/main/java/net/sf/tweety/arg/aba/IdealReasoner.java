/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
 /**
 * 
 */
package net.sf.tweety.arg.aba;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.commons.Formula;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 * @author Matthias Thimm
 * This reasoner for ABA theories performs inference on the ideal extension.
 * @param <T>	the language of the underlying ABA theory
 */
public class IdealReasoner<T extends Formula> extends GeneralABAReasoner<T> {

	/**
	 * Creates a new ideal reasoner.
	 * @param beliefBase a knowledge base.
	 * @param inferenceType The inference type for this reasoner.
	 */
	public IdealReasoner(int inferenceType) {
		super(inferenceType);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aba.GeneralABAReasoner#computeExtensions(net.sf.tweety.arg.aba.ABATheory)
	 */
	@Override
	public Collection<Collection<Assumption<T>>> computeExtensions(ABATheory<T> abat) {
		Collection<Collection<Assumption<T>>> prefexts = new PreferredReasoner<T>(Semantics.CREDULOUS_INFERENCE).computeExtensions(abat);
		Iterator<Collection<Assumption<T>>> iter = prefexts.iterator();
		Collection<Assumption<T>> intersec = iter.hasNext() ? iter.next() : new HashSet<Assumption<T>>();
		while (iter.hasNext()) {
			intersec.retainAll(iter.next());
		}
		
		Collection<Collection<Assumption<T>>>result = new HashSet<>();
		Collection<Collection<Assumption<T>>> exts = abat.getAllAdmissbleExtensions();
		for(Collection<Assumption<T>> ext : exts) {
			if (intersec.containsAll(ext))
				result.add(new HashSet<>(ext));
		}
		
		Collection<Collection<Assumption<T>>>result2 = new HashSet<>();
		l:for(Collection<Assumption<T>> ext : result) {
			for(Collection<Assumption<T>> ext2 : result) {
				if (ext2!=ext&&ext2.containsAll(ext))
					continue l;
			}
			result2.add(new HashSet<>(ext));
		}
		return result2;
	}
}