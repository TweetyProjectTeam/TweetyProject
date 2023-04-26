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

import org.tweetyproject.arg.dung.equivalence.IEquivalence;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents an comparator, which defines if 2 frameworks are equivalent, 
 * by comparing their serialisability. 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public abstract class SerialisationEquivalence<T> implements IEquivalence<DungTheory> {

	/**
	 * Comparator used to define the equivalence of two graphs
	 */
	private IEquivalence<T> comparator;
	
	/**
	 * @param comparator Comparator used to define the equivalence of two graphs
	 */
	public SerialisationEquivalence(IEquivalence<T> comparator) {
		super();
		this.comparator = comparator;
	}
	
	/**
	 * Method to retrieve the aspect of the serialisation, 
	 * which is important for the comparison of the equivalence of the frameworks to compare
	 * @param framework Abstract argumentation framework, which is to be compared
	 * @return Aspect of the serialisation, which is needed for the comparator to define equivalence
	 */
	protected abstract T getRelevantAspect(DungTheory framework);
	
	/**
	 * @param object Aspect of the serialisation, used to compare equivalence
	 * @return Framework, which is associated with this aspect
	 */
	protected abstract DungTheory getFramework(T object);
	
	@Override
	public boolean isEquivalent(DungTheory obj1, DungTheory obj2) {
		return comparator.isEquivalent(getRelevantAspect(obj1), getRelevantAspect(obj2));
	}

	@Override
	public boolean isEquivalent(Collection<DungTheory> objects) {
		var aspects = new HashSet<T>();
		
		for (DungTheory framework : objects) {
			aspects.add(getRelevantAspect(framework));
		}
		
		return comparator.isEquivalent(aspects);
	}

	@Override
	public Collection<DungTheory> getEquivalentTheories(DungTheory framework) {
		var objects = comparator.getEquivalentTheories(getRelevantAspect(framework));
		if(objects == null) return null;
		var out_frameworks = new HashSet<DungTheory>();
		for (T element : objects) {
			out_frameworks.add(getFramework(element));
		}
		return out_frameworks;
	}
}
