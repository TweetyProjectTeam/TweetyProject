package net.sf.tweety.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;

import net.sf.tweety.lp.asp.solver.Solver;
import net.sf.tweety.lp.asp.solver.SolverException;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;

/**
 * This class implements the screened maxi-choice consolidation operator from [1].
 * 
 *  [1] Kruempelmann, Patrick und Gabriele Kern-Isberner: 
 * 	Belief Base Change Operations for Answer Set Programming. 
 *  In: Cerro, Luis Farinas, Andreas Herzig und Jerome Mengin (Herausgeber):
 *  Proceedings of the 13th European conference on Logics in Artificial 
 *  Intelligence, Band 7519, Seiten 294-306, Toulouse, France, 2012. 
 *  Springer Berlin Heidelberg.
 *  
 * @author Sebastian Homann
 *
 */
public class ScreenedMaxichoiceConsolidation implements ConsolidationOperator<Rule> {
	private Program screen;
	private SelectionFunction<Rule> selection;
	private Solver solver;
	
	/**
	 * Creates a new screened maxi-choice consolidation operator with the given screen,
	 * selection function and a link to an answer set solver.
	 * @param screen the screen to be retained in this consolidation operation
	 * @param selection a selection function
	 * @param solver an asp-solver
	 */
	public ScreenedMaxichoiceConsolidation(Program screen, SelectionFunction<Rule> selection, Solver solver) {
		this.screen = screen;
		this.selection = selection;
		this.solver = solver;
	}
	
	/**
	 * Calculates the screened consolidation !r of program p using a maxi-choice selection
	 * function s, i.e. p!r = s(p \bot r) where p \bot r is the set of remainder sets of p
	 * with screen r.
	 * 
	 * @param p a program
	 * @return consolidation of program p
	 * @throws SolverException
	 */
	public Program consolidate(Program p) throws SolverException {
		return new Program(selection.select(new ScreenedRemainderSets(p, screen, solver)));
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.lp.asp.beliefdynamics.baserevision.ConsolidationOperator#consolidate(java.util.Collection)
	 */
	@Override
	public Collection<Rule> consolidate(Collection<Rule> p) {
		if(p instanceof Program) {
			try {
				return consolidate((Program) p);
			} catch (SolverException e) {
				throw new RuntimeException(e);
			}
		}
		Program program = new Program();
		program.addAll(p);
		try {
			return consolidate(program);
		} catch (SolverException e) {
			e.printStackTrace();
			return null;
		}
	}
}
