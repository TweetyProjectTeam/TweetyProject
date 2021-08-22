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
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.FixPartialSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
public abstract class GroundGenerator extends AbstractCandidateGenerator {
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
		
	private final RelativeSatEncoding fixPartial;
	
	private final SatEncoding conflictFree;

	/**
	 * @param adf
	 * @param mapping
	 */
	private GroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
		super(stateSupplier);
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
	public static CandidateGenerator restricted(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
		return new PrefixGroundGenerator(adf, mapping, prefix, stateSupplier);
	}
	
	/**
	 * The resulting {@link CandidateGenerator} computes the ground interpretation.
	 * 
	 * @param adf
	 * @param mapping
	 * @return
	 */
	public static GroundGenerator unrestricted(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
		return new WithoutPrefixGroundGenerator(adf, mapping, stateSupplier);
	}
	
	@Override
	public void prepare(SatSolverState state) {
		conflictFree.encode(state::add);
	}
	
	/**
	 * Computes the ground interpretation and keeps the state sat.
	 * 
	 * @param state
	 * @return
	 */
	Interpretation compute(SatSolverState state) {
		Interpretation oldInterpretation = null;
		Interpretation newInterpretation = Interpretation.empty(adf);
		
		Map<Argument, Boolean> valMap = new HashMap<>();
		for (Argument arg : adf.getArguments()) {
			valMap.put(arg, null);
		}
		
		Map<Argument, Literal> accNameByArgument = new HashMap<>();
		for (Argument s : adf.getArguments()) {
			TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(r -> mapping.getLink(r, s), false);
			Literal accName = transformer.collect(adf.getAcceptanceCondition(s), state::add);
			accNameByArgument.put(s, accName);
		}
		
		do {
			oldInterpretation = newInterpretation;
			fixPartial.encode(state::add, oldInterpretation);
			
			for (Argument s : oldInterpretation.undecided()) {
				Literal accName = accNameByArgument.get(s);
				
				// if -acc is satisfiable -> acc not a tautology
				state.assume(accName.neg());
				boolean notTaut = state.satisfiable();
				
				if (!notTaut) {
					valMap.put(s, true);
				} else {
					// if acc is satisfiable -> acc not unsat
					state.assume(accName);
					boolean notUnsat = state.satisfiable();
					
					if (!notUnsat) {
						valMap.put(s, false);
					} else {
						valMap.put(s, null);
					}
				}
			}
			
			newInterpretation = Interpretation.fromMap(valMap);
		} while (!newInterpretation.equals(oldInterpretation));
		
		return newInterpretation;
	}

	@Override
	public Interpretation generate(SatSolverState state) {
		if (!state.satisfiable()) return null;

		Interpretation newInterpretation = compute(state);
		
		makeUnsat(state); // signal the execution framework that we are done by returning null for subsequent calls
		return newInterpretation;
	}
	
	static void makeUnsat(SatSolverState state) {
		Literal p = Literal.create("unsat");
		state.add(Clause.of(p));
		state.add(Clause.of(p.neg()));
	}
	
	private static final class WithoutPrefixGroundGenerator extends GroundGenerator {

		private WithoutPrefixGroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Supplier<SatSolverState> stateSupplier) {
			super(adf, mapping, stateSupplier);
		}	
		
	}
	
	private static final class PrefixGroundGenerator extends GroundGenerator {
		
		private final Interpretation prefix;
		
		private final AbstractDialecticalFramework adf;
		
		private final PropositionalMapping mapping;
								
		private PrefixGroundGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix, Supplier<SatSolverState> stateSupplier) {
			super(adf, mapping, stateSupplier);
			this.adf = adf;
			this.mapping = mapping;
			this.prefix = Objects.requireNonNull(prefix);
		}
		
		@Override
		public Interpretation generate(SatSolverState state) {
			Set<Argument> evaluated = new HashSet<>();
			
			for (Argument arg : prefix.arguments()) {
				evaluate(arg, evaluated, state);
			}
			
			for (Argument arg : adf.getArguments()) {
				evaluate(arg, evaluated, state);
			}
			
			Set<Literal> witness = state.witness();
			
			makeUnsat(state); // signal the execution framework that we are done by returning null for subsequent calls
			
			if (witness == null) return null;
			
			return Interpretation.fromWitness(witness, mapping);
		}
		
		private void evaluate(Argument arg, Set<Argument> evaluated, SatSolverState state) {
			if (!evaluated.add(arg)) return; // handle graph cycles
			
			AcceptanceCondition acc = adf.getAcceptanceCondition(arg);
			
			if (acc == AcceptanceCondition.CONTRADICTION) {
				state.add(Clause.of(mapping.getFalse(arg)));
			} else if (acc == AcceptanceCondition.TAUTOLOGY) {
				state.add(Clause.of(mapping.getTrue(arg)));
			} else {
				acc.arguments().forEach(parent -> evaluate(parent, evaluated, state));

				TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(parent -> mapping.getLink(parent, arg), false);
				Literal accName = transformer.collect(acc, state::add);
				
				state.assume(accName.neg());
				boolean notTaut = state.satisfiable();
				
				if (!notTaut) {
					state.add(Clause.of(mapping.getTrue(arg)));
				} else {
					state.assume(accName);
					boolean notUnsat = state.satisfiable();
					
					if (!notUnsat) {
						state.add(Clause.of(mapping.getFalse(arg)));						
					} else {
						state.add(Clause.of(mapping.getTrue(arg).neg()));
						state.add(Clause.of(mapping.getFalse(arg).neg()));
					}
				}
			}
		}
		
	}
	

}
