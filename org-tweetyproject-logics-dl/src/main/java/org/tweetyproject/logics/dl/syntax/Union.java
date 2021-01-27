/*
 *  This file is part of "Tweety", a collection of Java libraries for
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
 *  Copyright 2018 The TweetyProject Project Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.dl.syntax;

import java.util.Collection;
import java.util.HashSet;

/**
 * This class models an union in description logics. This can be translated to a
 * disjunction in first-order logic.
 * 
 * @author Anna Gessler
 *
 */
public class Union extends AssociativeDlFormula {
	/**
	 * Creates a new union with the given inner formulas.
	 * 
	 * @param formulas
	 *            a collection of formulas.
	 */
	public Union(Collection<? extends ComplexConcept> formulas) {
		super(formulas);
	}

	/**
	 * Creates a new (empty) union.true
	 */
	public Union() {
		this(new HashSet<ComplexConcept>());
	}

	/**
	 * Creates a new union with the two given formulae
	 * 
	 * @param first
	 *            a relational formula.
	 * @param second
	 *            a relational formula.
	 */
	public Union(ComplexConcept first, ComplexConcept second) {
		this();
		this.add(first);
		this.add(second);
	}

	/**
	 * Create a new union with the given formula.
	 * 
	 * @param formula
	 *            a DlFormula
	 */
	public Union(ComplexConcept formula) {
		this();
		this.add(formula);
	}

	@Override
	public String getOperatorSymbol() {
		return "or";
	}

	@Override
	public String getEmptySymbol() {
		return "*bottom*";
	}

	@Override
	public ComplexConcept clone() {
		return new Union(support.copyHelper(this));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Union createEmptyFormula() {
		return new Union();
	}

	@Override
	public ComplexConcept collapseAssociativeFormulas() {
		if (this.isEmpty())
			return new BottomConcept();
		if (this.size() == 1)
			return (this.iterator().next()).collapseAssociativeFormulas();
		Union newMe = new Union();
		for (ComplexConcept f : this) {
			if (!(f instanceof ComplexConcept))
				throw new IllegalStateException("Can not collapse disjunctions containing non-description formulae.");
			ComplexConcept newF = ((ComplexConcept) f).collapseAssociativeFormulas();
			if (newF instanceof Union)
				newMe.addAll((Union) newF);
			else
				newMe.add(newF);
		}
		return newMe;
	}

}
