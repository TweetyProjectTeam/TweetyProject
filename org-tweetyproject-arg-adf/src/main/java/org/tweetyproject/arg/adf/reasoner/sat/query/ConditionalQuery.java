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
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;
import org.tweetyproject.arg.adf.transform.TseitinTransformer;

/**
 * A decorator which adds a condition to a query.
 * 
 * @author Mathias Hofer
 *
 */
final class ConditionalQuery<T> extends SatQuery<T>{

	private final SatQuery<T> query;
	
	private final AcceptanceCondition condition;
	
	/**
	 * @param query
	 * @param condition
	 */
	public ConditionalQuery(SatQuery<T> query, AcceptanceCondition condition) {
		super(query.adf, query.semantics, query.configuration); // implicit null check of query
		this.query = query;
		this.condition = Objects.requireNonNull(condition);
	}
	
	@Override
	public SatQuery<T> configure(Configuration configuration) {
		return new ConditionalQuery<T>(query.configure(configuration), condition);
	}

	@Override
	T execute(Execution execution) {
		execution.update(this::applyCondition);
		return query.execute(execution);
	}
	
	private void applyCondition(SatSolverState state) {
		TseitinTransformer transformer = TseitinTransformer.ofPositivePolarity(true);
		Literal name = transformer.collect(condition, state::add);
		state.add(Clause.of(name));
	}

}
