package net.sf.tweety.logics.ml.syntax;

import java.util.Set;

import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Conjuctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Disjunctable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.math.probability.Probability;

/**
 * Instances of this class represent first-order formulas with a weight.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public class MlnFormula extends RelationalFormula {

	/** the first-order formula. */
	private FolFormula formula;
	
	/** The weight of the formula (null means that the formula is strict). */
	private Double weight;
	
	/** Creates a new strict MLN formula with the given formula.
	 * @param formula the first-order formula.
	 */
	public MlnFormula(FolFormula formula){
		this.formula = formula;
		this.weight = null;
	}
	
	/** Creates a new MLN formula with the given formula and weight.
	 * @param formula the first-order formula.
	 * @param weight the weight of the formula (null means that the formula is strict).
	 */
	public MlnFormula(FolFormula formula, Double weight){
		this.formula = formula;
		this.weight = weight;
	}
		
	/** Creates a new MLN formula and estimates its weight w by the given
	 * probability p using the formula w = log(p/(1-p)*f) where "f" is the
	 * ratio of the number of worlds not satisfying the formula and the
	 * worlds satisfying the formula. 
	 * @param formula the first-order formula.
	 * @param p the intended probability of the formula.
	 */
	public MlnFormula(FolFormula formula, Probability p){
		this.formula = formula;
		this.weight = Math.log(p.doubleValue()/(1-p.doubleValue())*formula.getSatisfactionRatio());
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.ClassicalFormula#combineWithAnd(net.sf.tweety.ClassicalFormula)
	 */
	@Override
	public Conjunction combineWithAnd(Conjuctable f) {
		throw new UnsupportedOperationException("Combination with AND not supported for MLN formulas.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.ClassicalFormula#combineWithOr(net.sf.tweety.ClassicalFormula)
	 */
	@Override
	public Disjunction combineWithOr(Disjunctable f) {
		throw new UnsupportedOperationException("Combination with OR not supported for MLN formulas.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.ClassicalFormula#complement()
	 */
	@Override
	public RelationalFormula complement() {
		throw new UnsupportedOperationException("Complementing not supported for MLN formulas.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getPredicates()
	 */
	@Override
	public Set<? extends Predicate> getPredicates() {
		return this.formula.getPredicates();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getAtoms()
	 */
	@Override
	public Set<FOLAtom> getAtoms() {
		return this.formula.getAtoms();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#containsQuantifier()
	 */
	@Override
	public boolean containsQuantifier() {
		return this.formula.containsQuantifier();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#substitute(net.sf.tweety.logics.firstorderlogic.syntax.Term, net.sf.tweety.logics.firstorderlogic.syntax.Term)
	 */
	@Override
	public RelationalFormula substitute(Term<?> v, Term<?> t)	throws IllegalArgumentException {
		return new MlnFormula((FolFormula)this.formula.substitute(v, t),this.weight);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getUnboundVariables()
	 */
	@Override
	public Set<Variable> getUnboundVariables() {
		return this.formula.getUnboundVariables();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#isClosed()
	 */
	@Override
	public boolean isClosed() {
		return this.formula.isClosed();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#isClosed(java.util.Set)
	 */
	@Override
	public boolean isClosed(Set<Variable> boundVariables) {
		return this.formula.isClosed(boundVariables);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#isWellBound()
	 */
	@Override
	public boolean isWellBound() {
		return this.formula.isWellBound();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#isWellBound(java.util.Set)
	 */
	@Override
	public boolean isWellBound(Set<Variable> boundVariables) {
		return this.formula.isWellBound(boundVariables);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#toString()
	 */
	@Override
	public String toString() {
		return "<" + this.formula + ", " + this.weight + ">";
	}


	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.LogicStructure#getFunctors()
	 */
	@Override
	public Set<Functor> getFunctors() {
		return this.formula.getFunctors();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.LogicStructure#getVariables()
	 */
	
	
	/** Returns the inner formula.
	 * @return the inner formula.
	 */
	public FolFormula getFormula(){
		return this.formula;
	}
	
	/** Returns the weight.
	 * @return the weight.
	 */
	public Double getWeight(){
		return this.weight;
	}

	/** Returns "true" iff this formula is strict.
	 * @return "true" iff this formula is strict.
	 */
	public boolean isStrict(){
		return this.weight == null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.firstorderlogic.syntax.RelationalFormula#getUniformProbability()
	 */
	@Override
	public Probability getUniformProbability() {
		return this.formula.getUniformProbability();
	}

	@Override
	public boolean isLiteral() {
		return this.formula.isLiteral();
	}

	@Override
	public Set<Term<?>> getTerms() {
		return this.formula.getTerms();
	}

	@Override
	public <C extends Term<?>> Set<C> getTerms(Class<C> cls) {
		return this.formula.getTerms(cls);
	}

	@Override
	public Set<Variable> getQuantifierVariables() {
		return this.formula.getQuantifierVariables();
	}

	@Override
	public RelationalFormula clone() {
		return new MlnFormula(this.formula.clone(), this.weight);
	}
}
