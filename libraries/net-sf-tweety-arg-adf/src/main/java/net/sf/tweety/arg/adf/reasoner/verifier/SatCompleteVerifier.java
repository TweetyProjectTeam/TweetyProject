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
package net.sf.tweety.arg.adf.reasoner.verifier;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.sf.tweety.arg.adf.reasoner.SatReasonerContext;
import net.sf.tweety.arg.adf.reasoner.encodings.ConflictFreeInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.FixPartialSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.LargerInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncoding;
import net.sf.tweety.arg.adf.reasoner.encodings.SatEncodingContext;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.transform.DefinitionalCNFTransform;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public class SatCompleteVerifier implements Verifier<SatReasonerContext>{
	
	private static final SatEncoding CONFLICT_FREE_ENCODING = new ConflictFreeInterpretationSatEncoding();

	private static final SatEncoding FIX_PARTIAL_ENCODING = new FixPartialSatEncoding();

	private static final SatEncoding LARGER_INTERPRETATION_ENCODING = new LargerInterpretationSatEncoding();

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.verifier.Verifier#verify(java.lang.Object, net.sf.tweety.arg.adf.semantics.Interpretation, net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public boolean verify(SatReasonerContext context, Interpretation candidate, AbstractDialecticalFramework adf) {
		SatEncodingContext encodingContext = new SatEncodingContext(adf);
		IncrementalSatSolver solver = context.getSolver();
		boolean complete = true;
		Iterator<Argument> undecided = candidate.undecided().iterator();
		try (SatSolverState newState = solver.createState()) {
			newState.add(FIX_PARTIAL_ENCODING.encode(encodingContext, candidate));
			newState.add(CONFLICT_FREE_ENCODING.encode(encodingContext));
			newState.add(LARGER_INTERPRETATION_ENCODING.encode(encodingContext, candidate));
			while (undecided.hasNext() && complete) {
				Argument s = undecided.next();
				Collection<Disjunction> acc = new LinkedList<Disjunction>();
				DefinitionalCNFTransform transform = new DefinitionalCNFTransform(
						r -> encodingContext.getLinkRepresentation(r, s));
				AcceptanceCondition acceptanceCondition = adf.getAcceptanceCondition(s);
				Proposition accName = acceptanceCondition.collect(transform, Collection::add, acc);

				newState.add(acc);

				// check not-taut
				newState.assume(accName, false);
				boolean notTaut = newState.satisfiable();

				// check not-unsat
				newState.assume(accName, true);
				boolean notUnsat = newState.satisfiable();

				complete = notTaut && notUnsat;
			}
		} catch (Exception e) {
			return false;
		}
		return complete;
	}

}
