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
package net.sf.tweety.arg.adf.reasoner.strategy.ground;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.sf.tweety.arg.adf.reasoner.strategy.SatSearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.ConflictFreeInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.FixPartialSatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncodingContext;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.DefinitionalCNFTransform;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public class SatGroundReasonerStrategy implements GroundReasonerStrategy {

	private static final SatEncoding FIX_PARTIAL = new FixPartialSatEncoding();
	
	private static final SatEncoding CONFLICT_FREE = new ConflictFreeInterpretationSatEncoding();
	
	private IncrementalSatSolver satSolver;
	
	/**
	 * @param satSolver
	 */
	public SatGroundReasonerStrategy(IncrementalSatSolver satSolver) {
		this.satSolver = satSolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.ReasonerStrategy#createSearchSpace(net.sf.
	 * tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public SearchSpace createSearchSpace(AbstractDialecticalFramework adf) {
		SatSolverState solverState = satSolver.createState();
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		return new SatSearchSpace(solverState, encodingContext, adf, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.ground.GroundReasonerStrategy#
	 * nextGround(net.sf.tweety.arg.adf.reasoner.strategy.SearchSpace)
	 */
	@Override
	public Interpretation nextGround(SearchSpace searchSpace) {
		// TODO find better solution
		if (searchSpace.candidate() == null) {
			return null;
		}
		
		AbstractDialecticalFramework adf = searchSpace.getAbstractDialecticalFramework();
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		Interpretation interpretation = new Interpretation(adf);
		try (SatSolverState state = satSolver.createState()) {
			state.add(CONFLICT_FREE.encode(encodingContext));
			while (true) {
				Iterator<Argument> undecided = interpretation.iterator();// .getUndecided().iterator();
				Map<Argument, Boolean> valMap = new HashMap<Argument, Boolean>();
				while (undecided.hasNext()) {
					Argument s = undecided.next();
					state.add(FIX_PARTIAL.encode(encodingContext, interpretation));
			
					Collection<Disjunction> accClauses = new LinkedList<Disjunction>();
					DefinitionalCNFTransform transform = new DefinitionalCNFTransform(
							r -> encodingContext.getLinkRepresentation(r, s));
					AcceptanceCondition acc = adf.getAcceptanceCondition(s);
					Proposition accName = acc.collect(transform, Collection::add, accClauses);						
					state.add(accClauses);

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
					// explicitly make searchspace only usable once
					searchSpace.close();
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
