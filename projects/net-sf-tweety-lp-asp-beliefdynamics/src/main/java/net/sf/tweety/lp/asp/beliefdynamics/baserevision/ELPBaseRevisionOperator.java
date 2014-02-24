package net.sf.tweety.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;

import net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator;
import net.sf.tweety.lp.asp.solver.Solver;
import net.sf.tweety.lp.asp.solver.SolverException;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;

/**
 * This class implements the base revision operator for extended
 * logic programs as introduced in [KKI12]. The revision of a set
 * of rules is defined as the screened maxi-choice consolidation of the
 * union of the belief base and the new beliefs.
 * 
 *  [KKI12] Kruempelmann, Patrick und Gabriele Kern-Isberner: 
 * 	Belief Base Change Operations for Answer Set Programming. 
 *  In: Cerro, Luis Farinas, Andreas Herzig und Jerome Mengin (Herausgeber):
 *  Proceedings of the 13th European conference on Logics in Artificial 
 *  Intelligence, Band 7519, Seiten 294-306, Toulouse, France, 2012. 
 *  Springer Berlin Heidelberg.
 *  
 * @author Sebastian Homann
 */
public class ELPBaseRevisionOperator extends MultipleBaseRevisionOperator<Rule> {
	private Solver solver;
	private SelectionFunction<Rule> selection;
	
	public ELPBaseRevisionOperator(Solver solver, SelectionFunction<Rule> selection) {
		this.solver = solver;
		this.selection = selection;
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.beliefdynamics.MultipleBaseRevisionOperator#revise(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<Rule> revise(Collection<Rule> base,
			Collection<Rule> formulas) {
		Program newKnowledge = new Program(formulas);		
		
		ScreenedMaxichoiceConsolidation consolidationOperator = new ScreenedMaxichoiceConsolidation(newKnowledge, selection, solver);
		Program union = new Program();
		union.addAll(base);
		union.addAll(formulas);
		Program result;
		try {
			result = consolidationOperator.consolidate(union);
		} catch (SolverException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

}
