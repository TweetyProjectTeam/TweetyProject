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
package net.sf.tweety.logics.pl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;

/**
 * This class represents a conjunction in propositional logic.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class Conjunction extends AssociativePropositionalFormula {
		
	/**
	 * Creates a new conjunction with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public Conjunction(Collection<? extends PropositionalFormula> formulas){
		super(formulas);
	}
	
	/**
	 * Creates a new (empty) conjunction.
	 */
	public Conjunction(){
		this(new HashSet<PropositionalFormula>());
	}
	
	/**
	 * Creates a new conjunction with the two given formulae
	 * @param first a propositional formula.
	 * @param second a propositional formula.
	 */
	public Conjunction(PropositionalFormula first, PropositionalFormula second){
		this();
		this.add(first);
		this.add(second);
	}	

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#collapseAssociativeFormulas()
	 */
	@Override
	public PropositionalFormula collapseAssociativeFormulas(){
		if(this.isEmpty())
			return new Tautology();
		if(this.size() == 1)
			return this.iterator().next().collapseAssociativeFormulas();
		Conjunction newMe = new Conjunction();
		for(PropositionalFormula f: this){
			PropositionalFormula newF = f.collapseAssociativeFormulas();
			if(newF instanceof Conjunction)
				newMe.addAll((Conjunction) newF);
			else newMe.add(newF);
		}
		return newMe;
	}
		
	
  /* (non-Javadoc)
   * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
   */
	@Override
	public PropositionalFormula toNnf() {
    Conjunction c = new Conjunction();
    for(PropositionalFormula p : this) {
      c.add( p.toNnf() );
    }
    return c;
	}

	@Override
	public Conjunction clone() {
		return new Conjunction(support.copyHelper(this));
	}


	@SuppressWarnings("unchecked")
	@Override
	public Conjunction createEmptyFormula() {
		return new Conjunction();
	}

	@Override
	public String getOperatorSymbol() {
		return LogicalSymbols.CONJUNCTION();
	}

	@Override
	public String getEmptySymbol() {
		return LogicalSymbols.CONTRADICTION();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
	 */
	@Override
	public Conjunction toCnf() {
		Collection<PropositionalFormula> cnf = new HashSet<PropositionalFormula>();
		for(PropositionalFormula conjunct: this)
			for(PropositionalFormula f: conjunct.toCnf())
			cnf.add(f);		
		return (Conjunction) new Conjunction(cnf).trim();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#trim()
	 */
	public PropositionalFormula trim(){
		Set<PropositionalFormula> conj = new HashSet<PropositionalFormula>();
		for(PropositionalFormula f: this.support)
			conj.add(f.trim());
		return new Conjunction(conj);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#getModels(net.sf.tweety.logics.pl.syntax.PropositionalSignature)
	 */
	@Override
	public Set<PossibleWorld> getModels(PropositionalSignature sig) {
		Set<PossibleWorld> models = new HashSet<PossibleWorld>();
		Iterator<PropositionalFormula> it = this.support.iterator();
		if(!it.hasNext())
			return models;
		models.addAll(it.next().getModels(sig));
		while(it.hasNext())
			models.retainAll(it.next().getModels(sig));
		return models;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#isConjunctiveClause()
	 */
	public boolean isConjunctiveClause(){
		for(PropositionalFormula f: this.getFormulas())
			if(!f.isLiteral())
				return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.syntax.PropositionalFormula#replace(net.sf.tweety.logics.pl.syntax.Proposition, net.sf.tweety.logics.pl.syntax.PropositionalFormula, int)
	 */
	public PropositionalFormula replace(Proposition p, PropositionalFormula f, int i){
		int num = 0;
		Conjunction n = new Conjunction();
		for(PropositionalFormula sub: this.support.getFormulas()){
			if(num < i && num + sub.numberOfOccurrences(p) >= i ){
				n.add(sub.replace(p, f, i-num));
			}else n.add(sub.clone());
			num += sub.numberOfOccurrences(p);
		}
		return n;		
	}
}
