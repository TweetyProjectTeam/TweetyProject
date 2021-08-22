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

import org.tweetyproject.arg.adf.reasoner.sat.execution.Configuration;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Execution;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Semantics;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * @author Mathias Hofer
 *
 */
final class ForAllSatQuery extends SatQuery<Boolean> {
	
	private final AcceptanceCondition condition;

	public ForAllSatQuery(AbstractDialecticalFramework adf, Semantics semantics, Configuration configuration, AcceptanceCondition condition) {
		super(adf, semantics, configuration);
		this.condition = Objects.requireNonNull(condition);
	}
	
	@Override
	public SatQuery<Boolean> configure(Configuration configuration) {
		return new ForAllSatQuery(adf, semantics, configuration, condition);
	}

	@Override
	Boolean execute(Execution execution) {
		execution.update(this::applyCondition);
		return execution.stream().findAny().isEmpty();
	}
	
	private void applyCondition(SatSolverState state) {
		// check if there is a model that does not satisfy the condition
		TseitinTransformer transformer = TseitinTransformer.ofNegativePolarity(true);
		Literal name = transformer.collect(condition, state::add);
		state.add(Clause.of(name.neg()));
	}

}
