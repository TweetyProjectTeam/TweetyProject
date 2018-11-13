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

import net.sf.tweety.logics.commons.LogicalSymbols;

/**
 * This class models an intersection in description logics. This can be
 * translated to a conjunction in first-order logic.
 * 
 * @author Anna Gessler
 *
 */
public class Intersection extends AssociativeDLFormula {
	/**
	 * Creates a new intersection with the given inner formulas.
	 * 
	 * @param formulas
	 *            a collection of formulas.
	 */
	public Intersection(Collection<? extends DlFormula> formulas) {
		super(formulas);
	}

	/**
	 * Creates a new (empty) intersection.
	 */
	public Intersection() {
		this(new HashSet<DlFormula>());
	}

	/**
	 * Creates a new intersection with the two given formulae
	 * 
	 * @param first
	 *            a relational formula.
	 * @param second
	 *            a relational formula.
	 */
	public Intersection(DlFormula first, DlFormula second) {
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
		return LogicalSymbols.CONJUNCTION();
	}

	@Override
	public String getEmptySymbol() {
		return LogicalSymbols.TAUTOLOGY();
	}

	@Override
	public DlFormula clone() {
		return new Intersection(support.copyHelper(this));
	}

	@Override
	public DlFormula collapseAssociativeFormulas() {
		if (this.isEmpty())
			return new TopConcept();
		if (this.size() == 1)
			return (this.iterator().next()).collapseAssociativeFormulas();
		Intersection newMe = new Intersection();
		for (DlFormula f : this) {
			if (!(f instanceof DlFormula))
				throw new IllegalStateException("Can not collapse conjunctions containing non-description logic formulae.");
			DlFormula newF = ((DlFormula) f).collapseAssociativeFormulas();
			if (newF instanceof Intersection)
				newMe.addAll((Intersection) newF);
			else
				newMe.add(newF);
		}
		return newMe;
	}
}
