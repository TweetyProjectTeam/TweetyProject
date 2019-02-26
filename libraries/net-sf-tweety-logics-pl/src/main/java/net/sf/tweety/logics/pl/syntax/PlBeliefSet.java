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
package net.sf.tweety.logics.pl.syntax;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This class represents a knowledge base of propositional formulae.
 * 
 * @author Matthias Thimm
 *
 */
public class PlBeliefSet extends BeliefSet<PlFormula> {

	/**
	 * Creates a new (empty) knowledge base.
	 */
	public PlBeliefSet() {
		super();
	}

	/**
	 * Creates a new knowledge base with the given set of formulas.
	 * 
	 * @param formulas a set of formulas.
	 */
	public PlBeliefSet(Collection<? extends PlFormula> formulas) {
		super(formulas);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.kr.BeliefBase#getSignature()
	 */
	@Override
	public Signature getSignature() {
		PlSignature signature = new PlSignature();
		for (Formula f : this)
			signature.addAll(((PlFormula) f).getAtoms());
		return signature;
	}

	/**
	 * This method returns this belief set in conjunctive normal form (CNF). A
	 * formula is in CNF iff it is a conjunction of disjunctions and in NNF.
	 * 
	 * @return the formula in CNF.
	 */
	public Conjunction toCnf() {
		Conjunction conj = new Conjunction();
		for (PlFormula f : this)
			conj.add(f);
		return conj.toCnf();
	}

	/**
	 * Returns the set of syntax components of this belief set, i.e. a partitioning
	 * {K1,...,Kn} of K (a disjoint union K1u...uKn=K) such that the signatures of
	 * K1,...,Kn are pairwise disjoint.
	 * 
	 * @return the set of syntax components of this belief set
	 */
	public Collection<PlBeliefSet> getSyntaxComponents() {
		List<PlBeliefSet> sets = new LinkedList<PlBeliefSet>();
		for (PlFormula f : this) {
			PlBeliefSet s = new PlBeliefSet();
			s.add(f);
			sets.add(s);
		}
		boolean changed;
		do {
			changed = false;
			for (int i = 0; i < sets.size(); i++) {
				for (int j = i + 1; j < sets.size(); j++) {
					if (sets.get(i).getSignature().isOverlappingSignature(sets.get(j).getSignature())) {
						changed = true;
						sets.get(i).addAll(sets.get(j));
						sets.remove(j);
						break;
					}
				}
				if (changed)
					break;
			}
		} while (changed);
		return sets;
	}

	/**
	 * Force ordering on belief set based on comparison of hash codes.
	 * 
	 * @return list of "canonically" ordered formulas
	 * @see net.sf.tweety.logics.pl.syntax.PlBeliefSet.PlFormulaHashCodeComparator
	 */
	public List<PlFormula> getCanonicalOrdering() {
		List<PlFormula> orderedKB = new ArrayList<PlFormula>(this);
		orderedKB.sort(new PlFormulaHashCodeComparator());
		return orderedKB;
	}

	/**
	 * Comparator for sorting propositional formulas by comparing hash codes.
	 * 
	 * @see net.sf.tweety.logics.pl.syntax.PlBeliefSet#getCanonicalOrdering()
	 */
	class PlFormulaHashCodeComparator implements Comparator<PlFormula> {
		public int compare(PlFormula p1, PlFormula p2) {
			return p1.hashCode() - p2.hashCode();
		}
	}
}
