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
package org.tweetyproject.logics.translators.folprop;

import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.translators.Translator;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * A Translator between the FOL and propositonal logic and vice versa.
 * 
 * @author Tim Janus
 * @author Anna Gessler
 */
public class FOLPropTranslator extends Translator {

	/** Default-Ctor */
	public FOLPropTranslator() {}
	
	/**
	 * Translates the given FOL-Atom into a Proposition
	 * @param atom	FOL-Atom, if the given Atom has
	 * arguments an exception is thrown.
	 * @return		Propositional form of the given Atom
	 */
	public Proposition toPropositional(FolAtom atom) {
		return (Proposition) this.translateAtom(atom, Proposition.class);
	}
	
	/**
	 * Translates the given proposition into a FOL-Atom
	 * @param proposition The Proposition
	 * @return	A FOL-Atom representing the given Proposition in first order logic.
	 */
	public FolAtom toFOL(Proposition proposition) {
		return (FolAtom) this.translateAtom(proposition, FolAtom.class);
	}

	/**
	 * Translates the given propositional Disjunction to a FOL Disjunction
	 * @param disjunction	a PL disjunction
	 * @return	The FOL Disjunction
	 */
	public Disjunction toFOL(org.tweetyproject.logics.pl.syntax.Disjunction disjunction) {
		return (Disjunction) this.translateAssociative(disjunction, Disjunction.class);
	}
	
	/**
	 * Translates the given FOL Disjunction to a propositional Disjunction
	 * @param disjunction	The FOL-Disjunction, if it contains formulas which
	 * 						are not expressible in propositional logic an exception
	 * 						is thrown.
	 * @return	The propositional Disjunction
	 */
	public org.tweetyproject.logics.pl.syntax.Disjunction toPropositional(Disjunction disjunction) {
		return (org.tweetyproject.logics.pl.syntax.Disjunction)
				this.translateAssociative(disjunction, org.tweetyproject.logics.pl.syntax.Disjunction.class);
	}
	
	/**
	 * Translates the given FOL Exclusive Disjunction to a propositional Exclusive Disjunction
	 * @param xor			The FOL-Exclusive Disjunction, if it contains formulas which
	 * 						are not expressible in propositional logic an exception
	 * 						is thrown.
	 * @return	The propositional Exclusive Disjunction
	 */
	public org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction toPropositional(ExclusiveDisjunction xor) {
		return (org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction)
				this.translateAssociative(xor, org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction.class);
	}
	
	/**
	 * Translates the given propositional Conjunction to a FOL Conjunction
	 * @param conjunction a PL conjunction	
	 * @return	The FOL Conjunction
	 */
	public Conjunction toFOL(org.tweetyproject.logics.pl.syntax.Conjunction conjunction) {
		return (Conjunction) this.translateAssociative(conjunction, Conjunction.class);
	}
	
	/**
	 * Translates the given FOL Conjunction to a propositional Conjunction
	 * @param conjunction	The FOL-Conjunction, if it contains formulas which
	 * 						are not expressible in propositional logic an exception
	 * 						is thrown.
	 * @return	The propositional Conjunction
	 */
	public org.tweetyproject.logics.pl.syntax.Conjunction toPropositional(Conjunction conjunction) {
		return (org.tweetyproject.logics.pl.syntax.Conjunction)
				this.translateAssociative(conjunction, org.tweetyproject.logics.pl.syntax.Conjunction.class);
	}
	
	public FolFormula toFOL(PlFormula propFormula) {
		if(propFormula instanceof Tautology) {
			return new org.tweetyproject.logics.fol.syntax.Tautology();
		}
		if(propFormula instanceof Contradiction) {
			return new org.tweetyproject.logics.fol.syntax.Contradiction();
		}
		if(propFormula instanceof Negation) {
			Negation neg = (Negation) propFormula;
			return new org.tweetyproject.logics.fol.syntax.Negation(toFOL(neg.getFormula()));
		}
		if(propFormula instanceof Proposition) {
			Proposition prop = (Proposition) propFormula;
			return toFOL(prop);
		}
		if(propFormula instanceof org.tweetyproject.logics.pl.syntax.Conjunction) {
			org.tweetyproject.logics.pl.syntax.Conjunction conj = (org.tweetyproject.logics.pl.syntax.Conjunction) propFormula;
			return toFOL(conj);
		}
		if(propFormula instanceof org.tweetyproject.logics.pl.syntax.Disjunction) {
			org.tweetyproject.logics.pl.syntax.Disjunction disj = (org.tweetyproject.logics.pl.syntax.Disjunction) propFormula;
			return toFOL(disj);
		}
		if(propFormula instanceof org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction) {
			org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction xor = (org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction) propFormula;
			return toFOL(xor);
		}
		return null;
	}
	
	public PlFormula toPropositional(FolFormula folFormula) {
		if(folFormula instanceof org.tweetyproject.logics.fol.syntax.Contradiction) {
			return new Contradiction();
		}
		if(folFormula instanceof org.tweetyproject.logics.fol.syntax.Tautology) {
			return new Tautology();
		}
		if(folFormula instanceof org.tweetyproject.logics.fol.syntax.Negation) {
			org.tweetyproject.logics.fol.syntax.Negation neg = (org.tweetyproject.logics.fol.syntax.Negation) folFormula;
			return new Negation(toPropositional(neg.getFormula()));
		}
		if(folFormula instanceof FolAtom) {
			return toPropositional(((FolAtom) folFormula));
		}
		if(folFormula instanceof Conjunction) {
			Conjunction conj = (Conjunction) folFormula;
			return toPropositional(conj);
		}
		if(folFormula instanceof Disjunction) {
			Disjunction disj = (Disjunction) folFormula;
			return toPropositional(disj);
		}
		if(folFormula instanceof ExclusiveDisjunction) {
			ExclusiveDisjunction xor = (ExclusiveDisjunction) folFormula;
			return toPropositional(xor);
		}
		return null;
	}
	
	@Override
	protected Map<Class<?>, Pair<Integer, Class<?>>> createTranslateMap() {
		Map<Class<?>, Pair<Integer, Class<?>>> tmap = new HashMap<Class<?>, Pair<Integer,Class<?>>>();
		tmap.put(FolAtom.class, new Pair<Integer, Class<?>>(TT_ATOM, Proposition.class));
		tmap.put(Proposition.class, new Pair<Integer, Class<?>>(TT_ATOM, FolAtom.class));
		
		tmap.put(Disjunction.class, new Pair<Integer, Class<?>>(
				TT_ASSOC, org.tweetyproject.logics.pl.syntax.Disjunction.class));
		tmap.put(org.tweetyproject.logics.pl.syntax.Disjunction.class, 
				new Pair<Integer, Class<?>>(TT_ASSOC, Disjunction.class));
		
		tmap.put(Conjunction.class, new Pair<Integer, Class<?>>(
				TT_ASSOC, org.tweetyproject.logics.pl.syntax.Conjunction.class));
		tmap.put(org.tweetyproject.logics.pl.syntax.Conjunction.class, 
				new Pair<Integer, Class<?>>(TT_ASSOC, Conjunction.class));
		
		tmap.put(ExclusiveDisjunction.class, new Pair<Integer, Class<?>>(
				TT_ASSOC, org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction.class));
		tmap.put(org.tweetyproject.logics.pl.syntax.ExclusiveDisjunction.class, 
				new Pair<Integer, Class<?>>(TT_ASSOC, ExclusiveDisjunction.class));
		
		return tmap;
	}
}
