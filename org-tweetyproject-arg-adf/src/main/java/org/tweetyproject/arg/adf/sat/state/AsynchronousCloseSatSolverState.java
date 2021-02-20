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
package org.tweetyproject.arg.adf.sat.state;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;

import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * The {@link #close()} call is handled by the provided executor. This allows asynchronous cleanup.
 * 
 * @author Mathias Hofer
 *
 */
public class AsynchronousCloseSatSolverState implements SatSolverState {

	private final SatSolverState delegate;
	
	private final Executor executor;
	
	/**
	 * @param delegate the state
	 * @param executor the executor which is used to execute the calls to the delegate state
	 */
	public AsynchronousCloseSatSolverState(SatSolverState delegate, Executor executor) {
		this.delegate = Objects.requireNonNull(delegate);
		this.executor = Objects.requireNonNull(executor);
	}
	
	@Override
	public boolean satisfiable() {
		return delegate.satisfiable();
	}

	@Override
	public Set<Literal> witness() {
		return delegate.witness();
	}
	
	@Override
	public Set<Literal> witness(Collection<? extends Literal> filter) {
		return delegate.witness(filter);
	}

	@Override
	public void assume(Literal literal) {
		delegate.assume(literal);
	}

	@Override
	public boolean add(Clause clause) {
		return delegate.add(clause);
	}

	@Override
	public void close() {
		executor.execute(delegate::close);
	}

}
