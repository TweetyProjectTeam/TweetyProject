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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public class GroundReasoner extends AbstractDialecticalFrameworkReasoner {

	public IncrementalSatSolver solver;

	/**
	 * @param solver
	 */
	public GroundReasoner(IncrementalSatSolver solver) {
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
		Collection<Interpretation> models = new LinkedList<Interpretation>();
		models.add(getModel(adf));
		return models;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner#
	 * getModel(net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public Interpretation getModel(AbstractDialecticalFramework adf) {
		SatEncoding enc = new SatEncoding(adf);
		Interpretation interpretation = new Interpretation(adf);
		try (SatSolverState state = solver.createState()) {
			state.add(enc.conflictFreeInterpretation());
			state.add(enc.bipolar());
			if (!adf.bipolar()) {
				state.add(enc.kBipolar(interpretation));
			}
			while (true) {
				Iterator<Argument> undecided = interpretation.getUndecided().iterator();
				Map<Argument, Boolean> valMap = new HashMap<Argument, Boolean>();
				while (undecided.hasNext()) {
					Argument s = undecided.next();
					state.add(enc.fixTwoValued(interpretation));
					Pair<Proposition, Collection<Disjunction>> definitional = enc.definitionalAcc(s);
					Proposition accName = definitional.getFirst();
					Collection<Disjunction> acc = definitional.getSecond();
					state.add(acc);

					// check not-taut
					state.assume(accName, false);
					boolean notTaut = state.satisfiable();

					// check not-unsat
					state.assume(accName, true);
					boolean notUnsat = state.satisfiable();

					if (!notTaut) {
						valMap.put(s, true);
					} else if (!notUnsat) {
						valMap.put(s, false);
					} else {
						valMap.put(s, null);
					}
				}
				Interpretation newInterpretation = new Interpretation(valMap);
				if (newInterpretation.equals(interpretation)) {
					return interpretation;
				} else {
					interpretation = newInterpretation;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
