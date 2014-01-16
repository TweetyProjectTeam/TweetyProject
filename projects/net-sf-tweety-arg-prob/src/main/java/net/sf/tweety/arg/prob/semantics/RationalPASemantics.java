package net.sf.tweety.arg.prob.semantics;

import java.util.Collection;
import java.util.Map;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.math.term.FloatVariable;

/**
 * P is rational wrt. AF if for every A,B ∈ Arg, if A → B then P(A) > 0.5 implies P(B) ≤ 0.5.
 * @author Matthias Thimm
 */
public class RationalPASemantics extends AbstractPASemantics {

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#satisfies(net.sf.tweety.arg.prob.semantics.ProbabilisticExtension, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public boolean satisfies(ProbabilisticExtension p, DungTheory theory) {
		for(Attack att: theory.getAttacks()){
			if(p.probability(att.getAttacker()).doubleValue() > 0.5 + Probability.PRECISION)
				if(p.probability(att.getAttacked()).doubleValue() > 0.5 + Probability.PRECISION)
					return false;
		}		
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#getSatisfactionStatement(net.sf.tweety.arg.dung.DungTheory, java.util.Map)
	 */
	@Override
	public Collection<Statement> getSatisfactionStatements(DungTheory theory, Map<Collection<Argument>, FloatVariable> worlds2vars) {
		throw new UnsupportedOperationException("The rationality condition cannot be expressed as a closed statement.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#toString()
	 */
	@Override
	public String toString() {
		return "Rational Semantics";
	}

}
