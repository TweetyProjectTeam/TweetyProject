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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.syntax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.logics.pl.semantics.PossibleWorld;

/**
 * This class represents a cardinality constraint, i.e. a constraint of the form 
 * "at most n out of {a1,...,ak} are true", where a1 ... ak are propositions and n is an integer. 
 * It also includes methods that generate SAT encodings for cardinality constraints.
 * 
 * @author Anna Gessler
 *
 */
public class CardinalityConstraint extends PlFormula {
	/**
	 * The atoms the cardinality constraint ranges over.
	 */
	private Collection<Proposition> atoms;
	
	/**
	 * The number of atoms that are allowed to be true at most.
	 */
	private int atMost;
	
	/**
	 * Create a new at-most-n cardinality constraint with the given set of
	 * atoms and the given n.
	 * 
	 * @param atoms
	 * @param atMost n 
	 */
	public CardinalityConstraint(Collection<Proposition> atoms, int atMost) {
		this.atoms = atoms;
		this.atMost = atMost;
	}
	
	/**
	 * Returns a SAT encoding of this cardinality constraint.
	 * @param name prefix for auxiliary variables
	 * @return encoding
	 */
	public PlBeliefSet getSatEncoding(String name) {
		if (atoms.size() < 6 || (atMost == atoms.size()-1)) 
			return getBinomialEncoding(atoms, atMost);
		return getSequentialCounterEncoding(atoms, atMost, name);
	}
	
	/**
	 * Returns a SAT encoding of this cardinality constraint.
	 * @return encoding
	 */
	public PlBeliefSet getSatEncoding() {
		if (atoms.size() < 6 || (atMost == atoms.size()-1)) 
			return getBinomialEncoding(atoms, atMost);
		return getSequentialCounterEncoding(atoms, atMost, "C");
	}
	
	/**
	 * Returns a naive SAT encoding of the given cardinality constraint.
	 * 
	 * @param atoms
	 * @param atMost
	 * @return sat encoding
	 */
	public static PlBeliefSet getBinomialEncoding(Collection<Proposition> atoms, int atMost) {
		Set<Set<Proposition>> clauses = new SetTools<Proposition>().subsets(atoms, atMost+1);
		PlBeliefSet result = new PlBeliefSet();
		for (Set<Proposition> s : clauses) {
			Disjunction d = new Disjunction();
			for (Proposition p : s)
				d.add(new Negation(p));
			result.add(d);
		}
		return result;
	}

	/**
	 * Returns a SAT encoding of the given cardinality constraint based on the method of
	 * [Sinz. "Towards an Optimal {CNF} Encoding of Boolean Cardinality Constraints."
	 * Principles and Practice of Constraint Programming, Springer 2005]. 
	 * 
	 * @param atoms
	 * @param atMost
	 * @param name for auxiliary variables
	 * @return SAT encoding of constraint
	 */
	public static PlBeliefSet getSequentialCounterEncoding(Collection<Proposition> atoms, int atMost, String name) { 
		PlBeliefSet encoding = new PlBeliefSet();
		String v = name + "_$1_$2"; // name for new atoms, $s1 and $s2 will be replaced with indices
		int n = atoms.size();
		List<PlFormula> atomsList = new ArrayList<PlFormula>();
		atomsList.addAll(atoms);
		Disjunction d = new Disjunction();
		d.add(new Negation(atomsList.get(0)));
		d.add(new Proposition(v.replace("$1", Integer.toString(0)).replace("$2", "0")));
		encoding.add(d);
		
		//each formula has a register that counts true formulas in base one
		//i represents registers, j represents bits 
		for (int j = 1; j <= atMost-1; j++) {
			Proposition t = new Proposition(v.replace("$1", Integer.toString(0)).replace("$2", Integer.toString(j)));
			encoding.add(new Negation(t));
		}
		for (int i = 1; i <= n-2; i++) {
			d = new Disjunction();
			d.add(new Negation(atomsList.get(i)));
			d.add(new Proposition(v.replace("$1", Integer.toString((i))).replace("$2", "0")));
			encoding.add(d);
			d = new Disjunction();
			Proposition t1 = new Proposition(v.replace("$1", Integer.toString((i-1))).replace("$2", Integer.toString(0)));
			Proposition t2 = new Proposition(v.replace("$1", Integer.toString((i))).replace("$2", Integer.toString(0)));
			d.add(new Negation(t1));
			d.add(t2);
			encoding.add(d);
			d = new Disjunction();
			d.add(new Negation(atomsList.get(i)));
			d.add(new Negation(new Proposition(v.replace("$1", Integer.toString(((i-1)))).replace("$2", Integer.toString(atMost-1)))));
			encoding.add(d);
			for (int j = 1; j <= atMost-1; j++) {
				d = new Disjunction();
				Proposition rimjm = new Proposition(v.replace("$1", Integer.toString(i-1)).replace("$2",Integer.toString(j-1)));
				Proposition rij = new Proposition(v.replace("$1", Integer.toString(i)).replace("$2",Integer.toString(j)));
				d.add(new Negation(atomsList.get(i)));
				d.add(new Negation(rimjm));
				d.add(rij);
				encoding.add(d);
				d = new Disjunction();
				Proposition rimj = new Proposition(v.replace("$1", Integer.toString(i-1)).replace("$2",Integer.toString(j)));
				d.add(new Negation(rimj));
				d.add(rij);
				encoding.add(d);
			}
		}
		
		d = new Disjunction();
		d.add(new Negation(atomsList.get(n-1)));
		Proposition t = new Proposition(v.replace("$1", Integer.toString(n-2)).replace("$2",Integer.toString(atMost-1)));
		d.add(new Negation(t));
		encoding.add(d);
		return encoding;
	}
	
	/**
	 * Returns a naive at-most-1 encoding for the given set of atoms.
	 * @param atoms
	 * @return at-most-1 encoding
	 */
	public static Conjunction getNaiveAtMostOneEncoding(Collection<Proposition> atoms) {
		Conjunction encoding = new Conjunction();
		for (PlFormula a : atoms) {
			for (PlFormula b : atoms) {
				if (a != b) {
					Disjunction d = new Disjunction();
					d.add(new Negation(a), new Negation(b));
					encoding.add(d);
				}
			}
		}
		return encoding;
	}

	@Override
	public Set<Proposition> getAtoms() {
		return (Set<Proposition>) atoms;
	}

	@Override
	public Set<PlFormula> getLiterals() {
		Set<PlFormula> res = new HashSet<PlFormula>();
		res.addAll(this.getAtoms());
		return res;
	}

	@Override
	public PlFormula collapseAssociativeFormulas() {
		return this;
	}

	@Override
	public Set<PlPredicate> getPredicates() {
		Set<PlPredicate> res = new HashSet<PlPredicate>();
		for (Proposition p : atoms)
			res.addAll(p.getPredicates());
		return res;
	}

	@Override
	public PlFormula trim() {
		return this;
	}

	@Override
	public PlFormula toNnf() {
		return this.toCnf().toNnf();
	}

	@Override
	public Conjunction toCnf() {
		return getSatEncoding("N").toCnf();
	}

	@Override
	public Set<PossibleWorld> getModels(PlSignature sig) {
		Set<PossibleWorld> models = new HashSet<PossibleWorld>();
		Set<PossibleWorld> worlds = PossibleWorld.getAllPossibleWorlds(sig);
		if (sig.isEmpty())  
			worlds = PossibleWorld.getAllPossibleWorlds(new PlSignature(atoms)); 
		Set<Set<Proposition>> bigger_worlds = new SetTools<Proposition>().subsets(atoms, atMost+1);
		for (PossibleWorld w : worlds) {
			if (w.size()<=atMost)
				models.add(w);
			else {
				boolean found = false;
				for (Set<Proposition> bw : bigger_worlds) {
					Set<Proposition> wx = new HashSet<Proposition>(w);
					if (wx.containsAll(bw)) {
						found = true; 
						break;
					}
				}
				if (!found)
					models.add(w);
			}
		}
		return models;
	}

	@Override
	public int numberOfOccurrences(Proposition p) {
		int res = 0;
		for (Proposition px : atoms)
			res += px.numberOfOccurrences(p);
		return res;
	}

	@Override
	public CardinalityConstraint replace(Proposition p, PlFormula f, int i) {
		if (!(f instanceof Proposition))
			throw new UnsupportedOperationException();
		Set<Proposition> res = new HashSet<Proposition>();
		for (Proposition a : atoms)
			if (a.equals(p))
				res.add((Proposition)f);
			else
				res.add(a);
		return new CardinalityConstraint(res,atMost);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CardinalityConstraint other = (CardinalityConstraint) obj;
		if (atoms == null) {
			if (other.atoms != null)
				return false;
		} else if (!atoms.equals(other.atoms))
			return false;
		if (atMost != other.atMost)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atoms == null) ? 0 : atoms.hashCode());
		return result;
	}

	@Override
	public PlFormula clone() {
		return new CardinalityConstraint(atoms, atMost);
	}
	
	@Override
	public String toString() {
		return this.atoms.toString().replace("[", "{").replace("]", "}") + "<=" + atMost;
	}

}
