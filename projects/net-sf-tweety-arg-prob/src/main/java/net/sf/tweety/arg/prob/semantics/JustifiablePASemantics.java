package net.sf.tweety.arg.prob.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.math.equation.Statement;
import net.sf.tweety.math.term.FloatVariable;

/**
 * P is justifiable wrt. AF if P is coherent and optimistic.
 * @author Matthias Thimm
 */
public class JustifiablePASemantics extends AbstractPASemantics{

	/** The semantics this semantics is based upon. */
	private PASemantics cohSemantics = new CoherentPASemantics();
	private PASemantics optSemantics = new OptimisticPASemantics();
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#satisfies(net.sf.tweety.arg.prob.semantics.ProbabilisticExtension, net.sf.tweety.arg.dung.DungTheory)
	 */
	@Override
	public boolean satisfies(ProbabilisticExtension p, DungTheory theory) {
		return this.cohSemantics.satisfies(p, theory) && this.optSemantics.satisfies(p, theory);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#getSatisfactionStatement(net.sf.tweety.arg.dung.DungTheory, java.util.Map)
	 */
	@Override
	public Collection<Statement> getSatisfactionStatements(DungTheory theory, Map<Collection<Argument>, FloatVariable> worlds2vars) {
		Set<Statement> stats = new HashSet<Statement>();
		stats.addAll(this.cohSemantics.getSatisfactionStatements(theory, worlds2vars));
		stats.addAll(this.optSemantics.getSatisfactionStatements(theory, worlds2vars));
		return stats;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.prob.semantics.AbstractPASemantics#toString()
	 */
	@Override
	public String toString() {
		return "Justifiable Semantics";
	}

}
