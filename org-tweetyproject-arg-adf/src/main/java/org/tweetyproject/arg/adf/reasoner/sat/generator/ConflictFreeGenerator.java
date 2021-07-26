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
package org.tweetyproject.arg.adf.reasoner.sat.generator;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public abstract class ConflictFreeGenerator implements CandidateGenerator {
		
	private final PropositionalMapping mapping;
	
	private final RelativeSatEncoding refineUnequal;
	
	/**
	 * @param mapping
	 */
	private ConflictFreeGenerator(PropositionalMapping mapping) {
		this.mapping = Objects.requireNonNull(mapping);
		this.refineUnequal = new RefineUnequalSatEncoding(mapping);
	}
	
	/**
	 * The resulting {@link CandidateGenerator} only computes conflict free interpretations which are extensions of the defined prefix.
	 * 
	 * @param adf adf
	 * @param mapping mapping
	 * @param prefix the fixed prefix
	 * @return CandidateGeneratorwithPrefix
	 */
	public static CandidateGenerator withPrefix(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
		return new PrefixConflictFreeGenerator(adf, mapping, prefix);
	}
	
	/**
	 * The resulting {@link CandidateGenerator} computes all conflict free interpretations.
	 * 
	 * @param adf adf
	 * @param mapping mapping
	 * @return CandidateGeneratorwithoutPrefix
	 */
	public static CandidateGenerator withoutPrefix(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		return new WithoutPrefixConflictFreeGenerator(adf, mapping);
	}

	@Override
	public Interpretation generate(SatSolverState state) {
		Set<Literal> witness = state.witness(mapping.getArgumentLiterals());
		if (witness != null) {
			// prevent the same interpretation from being computed again
			Interpretation conflictFree = Interpretation.fromWitness(witness, mapping);
			refineUnequal.encode(state::add, conflictFree);
			return conflictFree;
		}
		return null;
	}

	private static final class WithoutPrefixConflictFreeGenerator extends ConflictFreeGenerator {
		
		private final SatEncoding conflictFree;
		
		private WithoutPrefixConflictFreeGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(mapping);
			this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
		}

		@Override
		public void prepare(Consumer<Clause> consumer) {
			conflictFree.encode(consumer);
		}
		
	}
	
	private static final class PrefixConflictFreeGenerator extends ConflictFreeGenerator {

		private final Interpretation prefix;
		
		private final RelativeSatEncoding conflictFree;
		
		private PrefixConflictFreeGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(mapping);
			this.prefix = Objects.requireNonNull(prefix);
			this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);	
		}
		
		@Override
		public void prepare(Consumer<Clause> consumer) {
			conflictFree.encode(consumer, prefix);
		}
		
	}
	
}
