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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisibility.equivalence;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents an comparator, which defines if 2 frameworks are equivalent,
 * by comparing their serialisability.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 * @param <T> *description missing*
 */
public abstract class SerialisationEquivalence<T> implements Equivalence<DungTheory> {

	/**
	 * Comparator used to define the equivalence of two graphs
	 */
	private Equivalence<T> comparator;

	/**
	 * @param comparator Comparator used to define the equivalence of two graphs
	 */
	public SerialisationEquivalence(Equivalence<T> comparator) {
		super();
		this.comparator = comparator;
	}

	@Override
	public String getDescription() {
		return this.comparator.getDescription();
	}

	@Override
	public boolean isEquivalent(Collection<DungTheory> objects) {
		var aspects = new HashSet<T>();

		for (DungTheory framework : objects) {
			aspects.add(this.getRelevantAspect(framework));
		}

		return this.comparator.isEquivalent(aspects);
	}

	@Override
	public boolean isEquivalent(DungTheory obj1, DungTheory obj2) {
		return this.comparator.isEquivalent(this.getRelevantAspect(obj1), this.getRelevantAspect(obj2));
	}

	/**
	 * @param object Aspect of the serialisation, used to compare equivalence
	 * @return Framework, which is associated with this aspect
	 */
	protected abstract DungTheory getFramework(T object);

	/**
	 * Method to retrieve the aspect of the serialisation,
	 * which is important for the comparison of the equivalence of the frameworks to compare
	 * @param framework Abstract argumentation framework, which is to be compared
	 * @return Aspect of the serialisation, which is needed for the comparator to define equivalence
	 */
	protected abstract T getRelevantAspect(DungTheory framework);
}
