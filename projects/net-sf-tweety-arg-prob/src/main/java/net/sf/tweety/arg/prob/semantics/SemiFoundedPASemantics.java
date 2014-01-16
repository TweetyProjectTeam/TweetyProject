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

/**
 * P is semi-founded wrt. AF if P(A) > 0.5 for every A ∈ Arg with Attackers(A) = ∅.
 * @author Matthias Thimm
 */
public class SemiFoundedPASemantics extends AbstractPASemantics{

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#satisfies(net.sf.tweety.arg.prob.semantics.ProbabilisticExtension, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public boolean satisfies(ProbabilisticExtension p, DungTheory theory) {
		for(Argument arg: theory){
			if(theory.getAttackers(arg).isEmpty()){
				if(p.probability(arg).doubleValue() <= 0.5 - Probability.PRECISION)
					return false;
			}
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
			if(theory.getAttackers(arg).isEmpty()){
				stats.add(new Inequation(this.probabilityTerm(arg, worlds2vars),new FloatConstant(0.5),Inequation.GREATER));
			}
		}
		return stats;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#toString()
	 */
	@Override
	public String toString() {
		return "Semi-Founded Semantics";
	}

}
