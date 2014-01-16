package net.sf.tweety.logics.pcl.syntax;

import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.commons.syntax.interfaces.ProbabilityAware;
import net.sf.tweety.logics.pl.syntax.*;
import net.sf.tweety.math.probability.Probability;


/**
 * This class represents a probabilistic conditional of the form (B|A)[p]
 * with formulas A,B and a probability p.
 * @author Matthias Thimm
 *
 */
public class ProbabilisticConditional extends Conditional implements ProbabilityAware {

	/**
	 * The probability of this conditional.
	 */
	private Probability probability;
	
	/**
	 * Creates a new probabilistic conditional with a tautological premise
	 * and given conclusion and probability.
	 * @param conclusion the conclusion (a formula) of this conditional.
	 * @param probability a probability.
	 */
	public ProbabilisticConditional(PropositionalFormula conclusion, Probability probability){
		super(conclusion);
		this.probability = probability;
	}
	
	/**
	 * Creates a new probabilistic conditional with the given premise,
	 * conclusion, and probability.
	 * @param premise the premise (a formula) of this conditional.
	 * @param conclusion the conclusion (a formula) of this conditional.
	 * @param probability a probability.
	 */
	public ProbabilisticConditional(PropositionalFormula premise, PropositionalFormula conclusion, Probability probability){
		super(premise,conclusion);
		this.probability = probability;
	}
	
	/**
	 * Creates a new probabilistic conditional using the given conditional
	 * and probability.
	 * @param conditional a conditional. 
	 * @param probability a probability.
	 */
	public ProbabilisticConditional(Conditional conditional, Probability probability){
		this(conditional.getPremise().iterator().next(),conditional.getConclusion(),probability);
	}
	
	/** Checks whether this and the given probabilistic conditional are 
	 * qualitatively equivalent, i.e. whether they are equivalent when neglecting
	 * the probability.
	 * @param other some probabilistic conditional.
	 * @return "true" iff the two conditionals are qualitatively equivalent.
	 */
	public boolean qualitativeEquals(ProbabilisticConditional other){
		return super.equals(other);
	}
	
	/**
	 * Returns the probability of this conditional.
	 * @return the probability of this conditional.
	 */
	public Probability getProbability(){
		return this.probability;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return super.toString() + "[" + this.probability.getValue() + "]";
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.ClassicalFormula#complement()
	 */
	@Override
	public ProbabilisticConditional complement(){
		return new ProbabilisticConditional(this,this.probability.complement());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((probability == null) ? 0 : probability.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProbabilisticConditional other = (ProbabilisticConditional) obj;
		if (probability == null) {
			if (other.probability != null)
				return false;
		} else if (!probability.equals(other.probability))
			return false;
		return true;
	}

	@Override
	public Probability getUniformProbability() {
		Double n = ((PropositionalFormula)this.getConclusion().combineWithAnd(this.getPremise().iterator().next())).getUniformProbability().getValue();
		Double d = this.getPremise().iterator().next().getUniformProbability().getValue();
		if(d == 0)
			return new Probability(0d);
		return new Probability(n/d);
	}

}
