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
package net.sf.tweety.arg.adf.reasoner.sat.generator;

import java.util.Set;

import net.sf.tweety.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import net.sf.tweety.arg.adf.reasoner.sat.encodings.SatEncoding;
import net.sf.tweety.arg.adf.sat.SatSolverState;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.pl.Atom;

/**
 * @author Mathias Hofer
 *
 */
public final class ConflictFreeGenerator implements CandidateGenerator {

	private final SatEncoding conflictFree = new ConflictFreeInterpretationSatEncoding();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator#initialize()
	 */
	@Override
	public void initialize(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		conflictFree.encode(state::add, mapping, adf);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.generator.CandidateGenerator#generate(java
	 * .lang.Object)
	 */
	@Override
	public Interpretation generate(SatSolverState state, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		Set<Atom> witness = state.witness(mapping.getArguments());
		if (witness != null) {
			// prevent the same interpretation from being computed again
			Interpretation conflictFree = Interpretation.fromWitness(witness, mapping, adf);
			new RefineUnequalSatEncoding(conflictFree).encode(state::add, mapping, adf);
			return conflictFree;
		}
		return null;
	}

}
