package org.tweetyproject.logics.translators.adfrevision;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.logics.pl.semantics.PossibleWorld;
import org.tweetyproject.logics.pl.syntax.AssociativePlFormula;
import org.tweetyproject.logics.pl.syntax.Conjunction;
import org.tweetyproject.logics.pl.syntax.Contradiction;
import org.tweetyproject.logics.pl.syntax.Disjunction;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlPredicate;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.Tautology;

/**
 * This class models an indecision operator for 3-valued propositional logic as proposed in [Heyninck 2020]
 * Indecision(a) is true, if formula a is undecided
 * Indecision(a) is false, if formula a is true or false
 * Adapted from the class "Negation"
 *
 * @author Jonas Schumacher
 */
public class Indecision extends PlFormula {

	/**
	 * The formula within this negation.
	 */
	private PlFormula formula;

	/**
	 * Creates a new negation with the given formula.
	 * @param formula the formula within the negation.
	 */
	public Indecision(PlFormula formula){
		this.formula = formula;
	}

	/**
	 * Returns the formula within this negation.
	 * @return the formula within this negation.
	 */
	public PlFormula getFormula(){
		return this.formula;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#collapseAssociativeFormulas()
	 */
	@Override
	public PlFormula collapseAssociativeFormulas(){
		return new Indecision(this.formula.collapseAssociativeFormulas());
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#hasLowerBindingPriority(org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula)
	 */
	/**
	 *
	 * Return returns false
	 * @param other a formula
	 * @return returns false
	 */
	public boolean hasLowerBindingPriority(PlFormula other){
		// negations have the highest binding priority
		return false;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		if(this.formula instanceof AssociativePlFormula || this.formula instanceof Indecision)
			return "¤" + "(" + this.formula + ")";
		return "¤" + this.formula;
	}

  /* (non-Javadoc)
   * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
   */
	@Override
	public PlFormula toNnf() {
    // remove double negation
    if(formula instanceof Indecision)
      return ((Indecision)formula).formula.toNnf();

     // Distribute negation inside conjunctions or disjunctions according to deMorgan's laws:
     // -(p & q)  = -p || -q
     // -(p || q) = -p & -q
    if(formula instanceof Conjunction) {
      Conjunction c = (Conjunction)formula;
      Disjunction d = new Disjunction();

      for(PlFormula p : c) {
        d.add( new Indecision( p ).toNnf() );
      }
      return d;
    }

    if(formula instanceof Disjunction) {
       Disjunction d = (Disjunction)formula;
       Conjunction c = new Conjunction();

       for(PlFormula p : d) {
         c.add( new Indecision( p ).toNnf() );
       }
       return c;
    }
    return this;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#trim()
	 */
	public PlFormula trim(){
		PlFormula f = this.formula.trim();
		if(f instanceof Indecision)
			return ((Indecision)f).formula;
		return new Indecision(f);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formula == null) ? 0 : formula.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Indecision other = (Indecision) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

	@Override
	public Set<PlPredicate> getPredicates() {
		return formula.getPredicates();
	}

	@Override
	public PlFormula clone() {
		return new Indecision(formula.clone());
	}

	@Override
	public Set<Proposition> getAtoms() {
		return formula.getAtoms();
	}

	@Override
	public boolean isLiteral() {
		return (formula instanceof Proposition);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#getLiterals()
	 */
	@Override
	public Set<PlFormula> getLiterals(){
		Set<PlFormula> result = new HashSet<PlFormula>();
		if(this.isLiteral())
			result.add(this);
		else result.addAll(this.formula.getLiterals());
		return result;
	}

	@Override
	public PlSignature getSignature() {
		return formula.getSignature();
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
	 */
	@Override
	public Conjunction toCnf() {
		if(this.formula instanceof Indecision){
			return ((Indecision)this.formula).getFormula().toCnf();
		}else if(this.formula instanceof Conjunction){
			Disjunction disj = new Disjunction();
			for(PlFormula f: (Conjunction) this.formula)
				disj.add((PlFormula)f.complement());
			return disj.toCnf();
		}else if(this.formula instanceof Disjunction){
			Conjunction conj = new Conjunction();
			for(PlFormula f: (Disjunction) this.formula)
				conj.add((PlFormula)f.complement());
			return conj.toCnf();
		}else if(this.formula instanceof Contradiction){
			Conjunction conj = new Conjunction();
			Disjunction disj = new Disjunction();
			disj.add(new Tautology());
			conj.add(disj);
			return conj;
		} if(this.formula instanceof Tautology){
			Conjunction conj = new Conjunction();
			Disjunction disj = new Disjunction();
			disj.add(new Contradiction());
			conj.add(disj);
			return conj;
		}
		Conjunction conj = new Conjunction();
		Disjunction disj = new Disjunction();
		disj.add(this);
		conj.add(disj);
		return conj;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#getModels(org.tweetyproject.logics.pl.syntax.PropositionalSignature)
	 */
	@Override
	public Set<PossibleWorld> getModels(PlSignature sig) {
		Set<PossibleWorld> models = PossibleWorld.getAllPossibleWorlds(sig);
		for(PossibleWorld w: this.formula.getModels(sig))
			models.remove(w);
		return models;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#numberOfOccurrences(org.tweetyproject.logics.pl.syntax.Proposition)
	 */
	public int numberOfOccurrences(Proposition p){
		return this.formula.numberOfOccurrences(p);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.pl.syntax.PropositionalFormula#replace(org.tweetyproject.logics.pl.syntax.Proposition, org.tweetyproject.logics.pl.syntax.PropositionalFormula, int)
	 */
	public PlFormula replace(Proposition p, PlFormula f, int i){
		return new Indecision(this.formula.replace(p, f, i));
	}
}
