package net.sf.tweety.arg.prob.semantics;

import java.util.Collection;
import java.util.Map;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.term.FloatVariable;

/**
 * This interface contains common methods for probabilistic argumentation semantics.
 * 
 * @author Matthias Thimm
 */
public interface PASemantics {
	
	/**
	 * Checks whether the given probabilistic extension satisfies the given
	 * argumentation theory wrt. this semantics.
	 * @param p a probabilistic extension.
	 * @param theory an argumentation theory
	 * @return "true" iff the given distribution satisfies the given conditional.
	 */
	public boolean satisfies(ProbabilisticExtension p, DungTheory theory);
	
	/**
	 * Returns the mathematical statement corresponding to the satisfaction
	 * of the given theory wrt. this semantics.
	 * @param theory an argumentation theory.
	 * @param worlds2vars a map mapping the (probabilities of the) extensions to mathematical variables (for constructing the statement).
	 * @return the mathematical statement corresponding to the satisfaction
	 * of the given theory wrt. this semantics.
	 */
	public Collection<Statement> getSatisfactionStatements(DungTheory theory, Map<Collection<Argument>,FloatVariable> worlds2vars);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString();
}
