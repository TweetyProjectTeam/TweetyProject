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
package net.sf.tweety.logics.dl.syntax;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class models an intersection in description logics. This can be
 * translated to a conjunction in first-order logic.
 * 
 * @author Anna Gessler
 *
 */
public class Intersection extends AssociativeDlFormula {
	/**
	 * Creates a new intersection with the given inner formulas.
	 * 
	 * @param formulas
	 *            a collection of formulas.
	 */
	public Intersection(Collection<? extends ComplexConcept> formulas) {
		super(formulas);
	}

	/**
	 * Creates a new (empty) intersection.
	 */
	public Intersection() {
		this(new HashSet<ComplexConcept>());
	}

	/**
	 * Creates a new intersection with the two given formulae
	 * 
	 * @param first
	 *            a relational formula.
	 * @param second
	 *            a relational formula.
	 */
	public Intersection(ComplexConcept first, ComplexConcept second) {
		this();
		this.add(first);
		this.add(second);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Intersection createEmptyFormula() {
		return new Intersection();
	}

	@Override
	public String getOperatorSymbol() {
		return "and";
	}

	@Override
	public String getEmptySymbol() {
		return "*top*";
	}

	@Override
	public ComplexConcept clone() {
		return new Intersection(support.copyHelper(this));
	}

	@Override
	public ComplexConcept collapseAssociativeFormulas() {
		if (this.isEmpty())
			return new TopConcept();
		if (this.size() == 1)
			return (this.iterator().next()).collapseAssociativeFormulas();
		Intersection newMe = new Intersection();
		for (ComplexConcept f : this) {
			if (!(f instanceof ComplexConcept))
				throw new IllegalStateException("Can not collapse conjunctions containing non-description logic formulae.");
			ComplexConcept newF = ((ComplexConcept) f).collapseAssociativeFormulas();
			if (newF instanceof Intersection)
				newMe.addAll((Intersection) newF);
			else
				newMe.add(newF);
		}
		return newMe;
	}
}
