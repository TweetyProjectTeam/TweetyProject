package net.sf.tweety.arg.prob.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Term;

/**
 * P is semi-optimistic wrt. AF if P(A) ≥ 1 − \sum_{B∈Attackers(A)} P(B) for every A ∈ Arg with Attackers(A)!=∅.
 * @author Matthias Thimm
 */
public class SemiOptimisticPASemantics extends AbstractPASemantics{
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#satisfies(net.sf.tweety.arg.prob.semantics.ProbabilisticExtension, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public boolean satisfies(ProbabilisticExtension p, DungTheory theory) {
		for(Argument arg: theory){
			if(theory.getAttackers(arg).isEmpty())
				continue;
			double prob = 0;
			for(Argument attacker: theory.getAttackers(arg)){
				prob += p.probability(attacker).doubleValue();
			}
			if(p.probability(arg).doubleValue() < 1 - prob - Probability.PRECISION)
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#getSatisfactionStatement(net.sf.tweety.arg.dung.DungTheory, java.util.Map)
	 */
	@Override
	public Collection<Statement> getSatisfactionStatements(DungTheory theory, Map<Collection<Argument>, FloatVariable> worlds2vars) {
		Set<Statement> stats = new HashSet<Statement>();
		for(Argument arg: theory){
			if(theory.getAttackers(arg).isEmpty())
				continue;
			Term prob = null;
			for(Argument attacker: theory.getAttackers(arg)){
				if(prob == null)
					prob = this.probabilityTerm(attacker, worlds2vars);
				else
					prob = prob.add(this.probabilityTerm(attacker, worlds2vars));
			}
			stats.add(new Inequation(this.probabilityTerm(arg, worlds2vars),new FloatConstant(1).minus(prob),Inequation.GREATER_EQUAL));
		}
		return stats;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#toString()
	 */
	@Override
	public String toString() {
		return "Semi-Optimistic Semantics";
	}
}