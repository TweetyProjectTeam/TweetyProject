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
package org.tweetyproject.arg.adf.reasoner.sat.query;

import java.util.Objects;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.reasoner.query.ConditionalTaskStep;
import org.tweetyproject.arg.adf.reasoner.query.ConditionlessTaskStep;
import org.tweetyproject.arg.adf.reasoner.query.Query;
import org.tweetyproject.arg.adf.reasoner.query.SemanticsStep;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Configuration;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Semantics;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public final class SatQueryBuilder {
	
	private final AbstractDialecticalFramework adf;
	/**
	 * 
	 * @param adf adf
	 */
	public SatQueryBuilder(AbstractDialecticalFramework adf) {
		this.adf = Objects.requireNonNull(adf);
	}
	/**
	 * 
	 * @return SemanticsStep defaultConfiguration()
	 */
	public SemanticsStep defaultConfiguration() {
		return configure(Configuration.builder().build());
	}
	/**
	 * 
	 * @param configuration configuration
	 * @return SemanticsStep configure
	 */
	public SemanticsStep configure(Configuration configuration) {
		return new SatSemanticsStep(configuration);
	}

	private final class SatSemanticsStep implements SemanticsStep {
		
		private final Configuration configuration;

		SatSemanticsStep(Configuration configuration) {
			this.configuration = Objects.requireNonNull(configuration);
		}

		@Override
		public ConditionlessTaskStep conflictFree() {
			return new DefaultConditionlessSatTask(Semantics.conflictFree(adf));
		}

		@Override
		public ConditionlessTaskStep naive() {
			return new DefaultConditionlessSatTask(Semantics.naive(adf));
		}

		@Override
		public ConditionlessTaskStep admissible() {
			return new DefaultConditionlessSatTask(Semantics.admissible(adf));
		}

		@Override
		public ConditionlessTaskStep preferred() {
			return new DefaultConditionlessSatTask(Semantics.preferred(adf));
		}

		@Override
		public ConditionlessTaskStep stable() {
			return new DefaultConditionlessSatTask(Semantics.stable(adf));
		}

		@Override
		public ConditionlessTaskStep complete() {
			return new DefaultConditionlessSatTask(Semantics.complete(adf));
		}

		@Override
		public ConditionlessTaskStep model() {
			return new DefaultConditionlessSatTask(Semantics.model(adf));
		}

		@Override
		public ConditionlessTaskStep ground() {
			return new DefaultConditionlessSatTask(Semantics.ground(adf));
		}

		@Override
		public ConditionlessTaskStep custom(Semantics semantics) {
			return new DefaultConditionlessSatTask(semantics);
		}
			
		private final class DefaultConditionlessSatTask implements ConditionlessTaskStep {

			private final Semantics semantics;

			public DefaultConditionlessSatTask(Semantics semantics) {
				this.semantics = Objects.requireNonNull(semantics);
			}

			@Override
			public Query<Stream<Interpretation>> interpretations() {
				return new InterpretationsSatQuery(semantics, configuration);
			}

			@Override
			public Query<Interpretation> interpretation() {
				return new InterpretationSatQuery(semantics, configuration);
			}

			@Override
			public Query<Boolean> exists() {
				return new ExistsSatQuery(semantics, configuration);
			}

			@Override
			public ConditionalTaskStep where(Argument condition) {
				return new DefaultConditionalSatTask(semantics, condition);
			}
	
		}
		
		private final class DefaultConditionalSatTask implements ConditionalTaskStep {

			private final Semantics semantics;
			
			private final Argument condition;

			public DefaultConditionalSatTask(Semantics semantics, Argument condition) {
				this.semantics = Objects.requireNonNull(semantics);
				this.condition = Objects.requireNonNull(condition);
			}

			@Override
			public Query<Stream<Interpretation>> interpretations() {
				return new InterpretationsSatQuery(semantics.restrict(Interpretation.ofSatisfied(condition)), configuration);
			}

			@Override
			public Query<Interpretation> interpretation() {
				return new InterpretationSatQuery(semantics.restrict(Interpretation.ofSatisfied(condition)), configuration);
			}

			@Override
			public Query<Boolean> exists() {
				return new ExistsSatQuery(semantics.restrict(Interpretation.ofSatisfied(condition)), configuration);
			}

			@Override
			public Query<Boolean> forAll() {
				return new ForAllSatQuery(semantics, configuration, condition);
			}
			
		}
	}
	
}
