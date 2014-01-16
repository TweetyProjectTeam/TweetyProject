package net.sf.tweety.logics.pl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.logics.commons.LogicalSymbols;
import net.sf.tweety.util.SetTools;

/**
 * This class represents a disjunction in propositional logic.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class Disjunction extends AssociativePropositionalFormula {
	
	/**
	 * Creates a new disjunction with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public Disjunction(Collection<? extends PropositionalFormula> formulas){
		super(formulas);
	}
	
	/**
	 * Creates a new (empty) disjunction.
	 */
	public Disjunction(){
		this(new HashSet<PropositionalFormula>());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#collapseAssociativeFormulas()
	 */
	@Override
	public PropositionalFormula collapseAssociativeFormulas(){
		if(this.isEmpty())
			return new Contradiction();
		if(this.size() == 1)
			return this.iterator().next().collapseAssociativeFormulas();
		Disjunction newMe = new Disjunction();
		for(PropositionalFormula f: this){
			PropositionalFormula newF = f.collapseAssociativeFormulas();
			if(newF instanceof Disjunction)
				newMe.addAll((Disjunction) newF);
			else newMe.add(newF);
		}
		return newMe;
	}
	
	/**
	 * Creates a new disjunction with the two given formulae
	 * @param first a propositional formula.
	 * @param second a propositional formula.
	 */
	public Disjunction(PropositionalFormula first, PropositionalFormula second){
		this();
		this.add(first);
		this.add(second);
	}
	 
  /* (non-Javadoc)
   * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toNNF()
   */
	@Override
	public PropositionalFormula toNnf() {
	  Disjunction d = new Disjunction();
    for(PropositionalFormula p : this) {
      d.add( p.toNnf() );
    }
    return d;
	}

	@Override
	public PropositionalFormula clone() {
		return new Disjunction(support.copyHelper(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Disjunction createEmptyFormula() {
		return new Disjunction();
	}

	@Override
	public String getOperatorSymbol() {
		return LogicalSymbols.DISJUNCTION();
	}

	@Override
	public String getEmptySymbol() {
		return LogicalSymbols.TAUTOLOGY();
	}
	
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula#toCnf()
	 */
	@Override
	public Conjunction toCnf() {	
		Set<Set<PropositionalFormula>> conjs = new HashSet<Set<PropositionalFormula>>();
		for(PropositionalFormula f: this)
			conjs.add(new HashSet<PropositionalFormula>(f.toCnf()));				
		Collection<PropositionalFormula> newConjs = new HashSet<PropositionalFormula>();
		SetTools<PropositionalFormula> setTools = new SetTools<PropositionalFormula>();		
		for(Set<PropositionalFormula> permut: setTools.permutations(conjs)){
			Disjunction disj = new Disjunction();
			for(PropositionalFormula f: permut)
				disj.addAll(((Disjunction)f));
			newConjs.add(disj);
		}		
		return new Conjunction(newConjs);
	}
}
