package net.sf.tweety.logics.pl.syntax;

import java.util.Set;

import net.sf.tweety.logics.commons.LogicalSymbols;

/**
 * This class models classical negation of propositional logic.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class Negation extends PropositionalFormula {

	/**
	 * The formula within this negation.
	 */
	private PropositionalFormula formula;
	
	/**
	 * Creates a new negation with the given formula.
	 * @param formula the formula within the negation.
	 */
	public Negation(PropositionalFormula formula){
		this.formula = formula;	
	}
	
	/**
	 * Returns the formula within this negation.
	 * @return the formula within this negation.
	 */
	public PropositionalFormula getFormula(){
		return this.formula;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#collapseAssociativeFormulas()
	 */
	@Override
	public PropositionalFormula collapseAssociativeFormulas(){
		return new Negation(this.formula.collapseAssociativeFormulas());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#hasLowerBindingPriority(net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula)
	 */
	public boolean hasLowerBindingPriority(PropositionalFormula other){
		// negations have the highest binding priority
		return false;
	}
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		if(this.formula instanceof AssociativePropositionalFormula || this.formula instanceof Negation)			
			return LogicalSymbols.CLASSICAL_NEGATION() + "(" + this.formula + ")";
		return LogicalSymbols.CLASSICAL_NEGATION() + this.formula;
	}
	
  /* (non-Javadoc)
   * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
   */
	@Override
	public PropositionalFormula toNnf() {
    // remove double negation    
    if(formula instanceof Negation)
      return ((Negation)formula).formula.toNnf();

     // Distribute negation inside conjunctions or disjunctions according to deMorgan's laws:
     // -(p & q)  = -p || -q
     // -(p || q) = -p & -q
    if(formula instanceof Conjunction) {
      Conjunction c = (Conjunction)formula;
      Disjunction d = new Disjunction();
      
      for(PropositionalFormula p : c) {
        d.add( new Negation( p ).toNnf() );
      }
      return d;
    }
    
    if(formula instanceof Disjunction) {
       Disjunction d = (Disjunction)formula;
       Conjunction c = new Conjunction();
       
       for(PropositionalFormula p : d) {
         c.add( new Negation( p ).toNnf() );
       }
       return c;
    }
    return this;
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
		Negation other = (Negation) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

	@Override
	public Set<PropositionalPredicate> getPredicates() {
		return formula.getPredicates();
	}

	@Override
	public PropositionalFormula clone() {
		return new Negation(formula.clone());
	}

	@Override
	public Set<Proposition> getAtoms() {
		return formula.getAtoms();
	}

	@Override
	public boolean isLiteral() {
		return (formula instanceof Proposition);
	}
	
	@Override
	public PropositionalSignature getSignature() {
		return formula.getSignature();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
	 */
	@Override
	public Conjunction toCnf() {	
		if(this.formula instanceof Negation){
			return ((Negation)this.formula).getFormula().toCnf();
		}else if(this.formula instanceof Conjunction){
			Disjunction disj = new Disjunction();
			for(PropositionalFormula f: (Conjunction) this.formula)
				disj.add((PropositionalFormula)f.complement());
			return disj.toCnf();
		}else if(this.formula instanceof Disjunction){
			Conjunction conj = new Conjunction();
			for(PropositionalFormula f: (Disjunction) this.formula)
				conj.add((PropositionalFormula)f.complement());
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
}
