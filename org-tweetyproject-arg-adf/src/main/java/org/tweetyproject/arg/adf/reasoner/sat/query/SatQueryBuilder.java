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
import org.tweetyproject.arg.adf.reasoner.sat.pipeline.Configuration;
import org.tweetyproject.arg.adf.reasoner.sat.pipeline.Semantics;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public final class SatQueryBuilder {
	
	private final AbstractDialecticalFramework adf;
	
	public SatQueryBuilder(AbstractDialecticalFramework adf) {
		this.adf = Objects.requireNonNull(adf);
	}
	
	public SemanticsStep defaultConfiguration() {
		return configure(Configuration.builder().build());
	}
	
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
			return new DefaultConditionlessSatTask(Semantics.conflictFree());
		}

		@Override
		public ConditionlessTaskStep naive() {
			return new DefaultConditionlessSatTask(Semantics.naive());
		}

		@Override
		public ConditionlessTaskStep admissible() {
			return new DefaultConditionlessSatTask(Semantics.admissible());
		}

		@Override
		public ConditionlessTaskStep preferred() {
			return new DefaultConditionlessSatTask(Semantics.preferred());
		}

		@Override
		public ConditionlessTaskStep stable() {
			return new DefaultConditionlessSatTask(Semantics.stable());
		}

		@Override
		public ConditionlessTaskStep complete() {
			return new DefaultConditionlessSatTask(Semantics.complete());
		}

		@Override
		public ConditionlessTaskStep model() {
			return new DefaultConditionlessSatTask(Semantics.model());
		}

		@Override
		public ConditionlessTaskStep ground() {
			return new DefaultConditionlessSatTask(Semantics.ground());
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
				return new InterpretationsSatQuery(adf, semantics, configuration);
			}

			@Override
			public Query<Interpretation> interpretation() {
				return new InterpretationSatQuery(adf, semantics, configuration);
			}

			@Override
			public Query<Boolean> exists() {
				return new ExistsSatQuery(adf, semantics, configuration);
			}

			@Override
			public ConditionalTaskStep where(AcceptanceCondition condition) {
				return new DefaultConditionalSatTask(semantics, condition);
			}
	
		}
		
		private final class DefaultConditionalSatTask implements ConditionalTaskStep {

			private final Semantics semantics;
			
			private final AcceptanceCondition condition;

			public DefaultConditionalSatTask(Semantics semantics, AcceptanceCondition condition) {
				this.semantics = Objects.requireNonNull(semantics);
				this.condition = Objects.requireNonNull(condition);
			}

			@Override
			public Query<Stream<Interpretation>> interpretations() {
				return new ConditionalQuery<>(new InterpretationsSatQuery(adf, semantics, configuration), condition);
			}

			@Override
			public Query<Interpretation> interpretation() {
				return new ConditionalQuery<>(new InterpretationSatQuery(adf, semantics, configuration), condition);
			}

			@Override
			public Query<Boolean> exists() {
				return new ConditionalQuery<>(new ExistsSatQuery(adf, semantics, configuration), condition);
			}

			@Override
			public Query<Boolean> forAll() {
				return new ForAllSatQuery(adf, semantics, configuration, condition);
			}
			
		}
	}
	
}
