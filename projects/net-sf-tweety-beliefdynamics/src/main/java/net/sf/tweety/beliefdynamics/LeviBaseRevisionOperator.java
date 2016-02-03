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
package net.sf.tweety.beliefdynamics;

import java.util.*;

import net.sf.tweety.logics.commons.syntax.interfaces.ClassicalFormula;

/**
 * This class implements the Levi identity for revision, ie. an revision that is composed of the
 * contraction with the negated formula and then expansion of the formula.
 * 
 * @author Matthias Thimm
 *
 * @param <T> the type of formulas this operators works on.
 */
public class LeviBaseRevisionOperator<T extends ClassicalFormula> implements BaseRevisionOperator<T> {

	/**
	 * The contraction operator of this Levi revision.
	 */
	private BaseContractionOperator<T> contraction;
	
	/**
	 * The expansion operator of this Levi revision.
	 */
	private BaseExpansionOperator<T> expansion;
	
	/**
	 * Creates a new Levi base revision with the given contraction and expansion operators.
	 * @param contraction some contraction operator.
	 * @param expansion some expansion operator.
	 */
	public LeviBaseRevisionOperator(BaseContractionOperator<T> contraction, BaseExpansionOperator<T> expansion){
		this.contraction = contraction;
		this.expansion = expansion;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.BaseRevisionOperator#revise(java.util.Collection, net.sf.tweety.Formula)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<T> revise(Collection<T> base, T formula) {
		return this.expansion.expand(this.contraction.contract(base, (T) formula.complement()), formula);
	}

}
