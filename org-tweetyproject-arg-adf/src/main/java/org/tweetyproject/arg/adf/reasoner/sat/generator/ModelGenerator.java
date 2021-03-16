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

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineUnequalSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.TwoValuedModelSatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public abstract class ModelGenerator implements CandidateGenerator {
	
	private final PropositionalMapping mapping;
		
	private final RelativeSatEncoding refineUnequal;
	
	/**
	 * @param mapping
	 */
	private ModelGenerator(PropositionalMapping mapping) {
		this.mapping = Objects.requireNonNull(mapping);
		this.refineUnequal = new RefineUnequalSatEncoding(mapping);
	}
	
	/**
	 * The resulting {@link CandidateGenerator} only computes two-valued models which are extensions of the defined prefix.
	 * 
	 * @param adf
	 * @param mapping
	 * @param prefix the fixed prefix
	 * @return
	 */
	public static CandidateGenerator withPrefix(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
		return new PrefixModelGenerator(adf, mapping, prefix);
	}
	
	/**
	 * The resulting {@link CandidateGenerator} computes all two-valued model interpretations.
	 * 
	 * @param adf
	 * @param mapping
	 * @return
	 */
	public static CandidateGenerator withoutPrefix(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		return new WithoutPrefixModelGenerator(adf, mapping);
	}	

	@Override
	public Interpretation generate(SatSolverState state) {
		Set<Literal> witness = state.witness(mapping.getArgumentLiterals());
		if (witness != null) {
			Interpretation model = Interpretation.fromWitness(witness, mapping);
			refineUnequal.encode(state::add, model);
			return model;
		}
		return null;
	}
	

	private static final class WithoutPrefixModelGenerator extends ModelGenerator {
		
		private final SatEncoding twoValuedModel;
		
		private WithoutPrefixModelGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(mapping);
			this.twoValuedModel = new TwoValuedModelSatEncoding(adf, mapping);
		}

		@Override
		public void prepare(Consumer<Clause> consumer) {
			twoValuedModel.encode(consumer);
		}
		
	}
	
	private static final class PrefixModelGenerator extends ModelGenerator {

		private final Interpretation prefix;
		
		private final RelativeSatEncoding twoValuedModel;
		
		private PrefixModelGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(mapping);
			this.prefix = Objects.requireNonNull(prefix);
			this.twoValuedModel = new TwoValuedModelSatEncoding(adf, mapping);
		}
		
		@Override
		public void prepare(Consumer<Clause> consumer) {
			twoValuedModel.encode(consumer, prefix);
		}
		
	}

}
