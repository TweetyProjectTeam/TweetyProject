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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.FixPartialSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public abstract class GroundGenerator implements CandidateGenerator {
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
		
	private final RelativeSatEncoding fixPartial;
	
	private final SatEncoding conflictFree;

	/**
	 * @param adf
	 * @param mapping
	 */
	private GroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
		this.fixPartial = new FixPartialSatEncoding(mapping);
		this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
	}
	
	/**
	 * The resulting {@link CandidateGenerator} only computes the ground interpretation if it extends the given prefix.
	 * 
	 * @param adf
	 * @param mapping
	 * @param prefix the fixed prefix
	 * @return
	 */
	public static CandidateGenerator withPrefix(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
		return new PrefixGroundGenerator(adf, mapping, prefix);
	}
	
	/**
	 * The resulting {@link CandidateGenerator} computes the ground interpretation.
	 * 
	 * @param adf
	 * @param mapping
	 * @return
	 */
	public static CandidateGenerator withoutPrefix(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		return new WithoutPrefixGroundGenerator(adf, mapping);
	}
	
	@Override
	public void prepare(Consumer<Clause> consumer) {
		conflictFree.encode(consumer);
	}

	@Override
	public Interpretation generate(SatSolverState state) {
		if (!state.satisfiable()) return null;

		Interpretation oldInterpretation = null;
		Interpretation newInterpretation = Interpretation.empty(adf);
		do {
			oldInterpretation = newInterpretation;
			fixPartial.encode(state::add, oldInterpretation);
			
			Map<Argument, Boolean> valMap = new HashMap<>();
			for (Argument s : adf.getArguments()) {
				TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), false);
				Literal accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);

				// check not-taut
				state.assume(accName.neg());
				boolean notTaut = state.satisfiable();
				
				// check not-unsat
				state.assume(accName);
				boolean notUnsat = state.satisfiable();
				
				if (!notTaut) {
					valMap.put(s, true);
				} else if (!notUnsat) {
					valMap.put(s, false);
				} else {
					valMap.put(s, null);
				}
			}
			
			newInterpretation = Interpretation.fromMap(valMap);
		} while (!newInterpretation.equals(oldInterpretation));
		
		makeUnsat(state); // signal the execution framework that we are done by returning null for subsequent calls
		return newInterpretation;
	}


	private static void makeUnsat(SatSolverState state) {
		Literal p = Literal.create("unsat");
		state.add(Clause.of(p));
		state.add(Clause.of(p.neg()));
	}
	
	private static final class WithoutPrefixGroundGenerator extends GroundGenerator {

		private WithoutPrefixGroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}	
		
	}
	
	private static final class PrefixGroundGenerator extends GroundGenerator {
		
		private final Interpretation prefix;
								
		private PrefixGroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping);
			this.prefix = Objects.requireNonNull(prefix);
		}
		
		
		
		@Override
		public Interpretation generate(SatSolverState state) {			
			return null;
		}
		
	}
	

}
