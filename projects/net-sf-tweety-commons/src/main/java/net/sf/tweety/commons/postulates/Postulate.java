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
 *  Copyright 2018 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons.postulates;

import java.util.Collection;

import net.sf.tweety.commons.Formula;

/**
 * Models a general (rationality) postulate, i.e. a property that
 * can be satisfied or violated by some approach. This class 
 * contains methods for checking whether an approach satisfies
 * certain instances wrt. this postulate.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas this postulate is about.
 */
public interface Postulate<S extends Formula> {

	/**
	 * Checks whether the given kb represents a non-trivial
	 * instance for this postulate, i.e., whether assumptions
	 * of this postulates are satisfied (evaluating an approach
	 * on a non-applicable instance always succeeds).
	 * 
	 * @param kb some knowledge base
	 * @return true if the knowledge base is a non trivial instance
	 *  of this postulate.
	 */
	public boolean isApplicable(Collection<S> kb);
	
	/**
	 * Checks whether this postulate is satisfied by the given approach
	 * <code>ev</code> wrt. the given instance <code>kb</code> (note
	 * that evaluating an approach on a non-applicable instance always succeeds).
	 * @param kb some knowledge base
	 * @param ev some approach
	 * @return true if the postulate is satisfied on the instance
	 */
	public boolean isSatisfied(Collection<S> kb, PostulateEvaluatable<S> ev);
	
	/**
	 * The textual name of the postulate
	 * @return a string
	 */
	public String getName();
}
