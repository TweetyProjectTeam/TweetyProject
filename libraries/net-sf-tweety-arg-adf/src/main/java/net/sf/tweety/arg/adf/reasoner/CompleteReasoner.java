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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public class CompleteReasoner extends AbstractDialecticalFrameworkReasoner {

	private IncrementalSatSolver solver;

	/**
	 * @param solver
	 */
	public CompleteReasoner(IncrementalSatSolver solver) {
		super();
		this.solver = solver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner#
	 * getModels(net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Collection<Interpretation> getModels(AbstractDialecticalFramework adf) {
		SatEncoding enc = new SatEncoding(adf);
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		Interpretation interpretation = new Interpretation(adf);
		try (SatSolverState state = solver.createState()) {
			state.add(enc.conflictFreeInterpretation());
			state.add(enc.bipolar());
			if (!adf.bipolar()) {
				state.add(enc.kBipolar(interpretation));
			}
			while ((interpretation = existsAdm(adf, new Interpretation(adf), state, enc)) != null) {
				boolean complete = true;
				Iterator<Argument> undecided = interpretation.undecided().iterator();
				try (SatSolverState newState = solver.createState()) {
					newState.add(enc.fixTwoValued(interpretation));
					newState.add(enc.conflictFreeInterpretation());
					newState.add(enc.largerInterpretation(interpretation));
					while (undecided.hasNext() && complete) {
						Argument s = undecided.next();
						Pair<Proposition, Collection<Disjunction>> definitional = enc.definitionalAcc(s);
						Proposition accName = definitional.getFirst();
						Collection<Disjunction> acc = definitional.getSecond();
						newState.add(acc);

						// check not-taut
						newState.assume(accName, false);
						boolean notTaut = newState.satisfiable();

						// check not-unsat
						newState.assume(accName, true);
						boolean notUnsat = newState.satisfiable();

						complete = notTaut && notUnsat;
					}
				}
				if (complete) {
					models.add(interpretation);
				}
				state.add(enc.refineUnequal(interpretation));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return models;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner#
	 * getModel(net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Interpretation getModel(AbstractDialecticalFramework bbase) {
		//TODO will be replaced
		return getModels(bbase).iterator().next();
	}

	private Interpretation existsAdm(AbstractDialecticalFramework adf, Interpretation interpretation,
			SatSolverState state, SatEncoding enc) {
		net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness = state.witness();
		Interpretation result = null;
		while (witness != null) {
			result = enc.interpretationFromWitness(witness);
			try (SatSolverState newState = solver.createState()) {
				newState.add(enc.verifyAdmissible(result));
				boolean sat = newState.satisfiable();
				if (sat) {
					Disjunction refineUnequal = enc.refineUnequal(result);
					state.add(refineUnequal);
				} else {
					return result;
				}
				witness = state.witness();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
