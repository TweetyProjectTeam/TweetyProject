/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.lp.asp.beliefdynamics.baserevision;

import java.util.Collection;

import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.reasoner.ASPSolver;
import net.sf.tweety.lp.asp.syntax.ASPRule;

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
public class ScreenedMaxichoiceConsolidation implements ConsolidationOperator<ASPRule> {
	private Program screen;
	private SelectionFunction<ASPRule> selection;
	private ASPSolver solver;
	
	/**
	 * Creates a new screened maxi-choice consolidation operator with the given screen,
	 * selection function and a link to an answer set solver.
	 * @param screen the screen to be retained in this consolidation operation
	 * @param selection a selection function
	 * @param solver an asp-solver
	 */
	public ScreenedMaxichoiceConsolidation(Program screen, SelectionFunction<ASPRule> selection, ASPSolver solver) {
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
	 */
	public Program consolidate(Program p) {
		return new Program(selection.select(new ScreenedRemainderSets(p, screen, solver)));
	}

	/*
	 * (non-Javadoc)
	 * @see net.sf.tweety.lp.asp.beliefdynamics.baserevision.ConsolidationOperator#consolidate(java.util.Collection)
	 */
	@Override
	public Collection<ASPRule> consolidate(Collection<ASPRule> p) {
		if(p instanceof Program) {
			try {
				return consolidate((Program) p);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		Program program = new Program();
		program.addAll(p);
		try {
			return consolidate(program);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
