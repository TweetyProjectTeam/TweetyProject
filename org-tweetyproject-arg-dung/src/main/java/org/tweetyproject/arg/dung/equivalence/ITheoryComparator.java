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
package org.tweetyproject.arg.dung.equivalence;

import java.util.Collection;

import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This interface defines methods to analyze the equivalence of two abstract argumentation frameworks. 
 * This interface was derived from the class {@link StrongEquivalence}.
 *
 * @see StrongEquivalence
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public interface ITheoryComparator {

	/**
     * compute whether the given theories are equivalent wrt. the kernel
     * @param theory1 a dung theory
     * @param theory2 a dung theory
     * @return true if both theories are equivalent wrt. to the kernel
     */
	public boolean isEquivalent(DungTheory theory1, DungTheory theory2);
	
	 /**
     * compute whether the given theories are equivalent wrt. the kernel
     * @param theories a collection of dung theories
     * @return true if all theories are equivalent wrt. to the kernel
     */
	public boolean isEquivalent(Collection<DungTheory> theories);
	
	 /**
     * compute all equivalent theories for the the given theory wrt. the kernel
     * i.e. get all useless attacks of theory and use them to create other strongly equivalent theories
     * @param theory a dung theory
     * @return the collection of equivalent theories
     */
	public Collection<DungTheory> getEquivalentTheories(DungTheory theory);
	
	/**
     * compute all equivalent theories for the given theory wrt. the kernel
     * i.e. enumerate all theories and compare to the base theory
     * @param baseTheory a dung theory
     * @return collection of equivalent theories
     */
	public Collection<DungTheory> getEquivalentTheoriesNaive(DungTheory baseTheory);
}
