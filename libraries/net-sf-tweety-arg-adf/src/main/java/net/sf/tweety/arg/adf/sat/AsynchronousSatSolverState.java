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
package net.sf.tweety.arg.adf.sat;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.tweety.commons.Interpretation;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Executes all operations of the underlying {@link SatSolverState} in a (possibly) separate thread, determined by the provided {@link ExecutorService}.
 * 
 * @author Mathias Hofer
 *
 */
public final class AsynchronousSatSolverState implements SatSolverState{

	private final SatSolverState delegate;
	
	private final ExecutorService executor;
		
	/**
	 * @param delegate the state
	 */
	public AsynchronousSatSolverState(SatSolverState delegate) {
		this(delegate, Executors.newSingleThreadExecutor());
	}

	/**
	 * @param delegate the state
	 * @param executor the executor which is used to execute the calls to the delegate state
	 */
	public AsynchronousSatSolverState(SatSolverState delegate, ExecutorService executor) {
		this.delegate = Objects.requireNonNull(delegate);
		this.executor = Objects.requireNonNull(executor);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.sat.SatSolverState#witness()
	 */
	@Override
	public Interpretation<PlBeliefSet, PlFormula> witness() {
		try {
			return executor.submit(delegate::witness).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.sat.SatSolverState#satisfiable()
	 */
	@Override
	public boolean satisfiable() {
		return delegate.satisfiable();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.sat.SatSolverState#assume(net.sf.tweety.logics.pl.syntax.Proposition, boolean)
	 */
	@Override
	public void assume(Proposition proposition, boolean value) {
		executor.execute(() -> delegate.assume(proposition, value));
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.sat.SatSolverState#add(net.sf.tweety.logics.pl.syntax.Disjunction)
	 */
	@Override
	public boolean add(Disjunction clause) {
		executor.execute(() -> delegate.add(clause));
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.sat.SatSolverState#add(java.util.Collection)
	 */
	@Override
	public boolean add(Collection<Disjunction> clauses) {
		executor.execute(() -> delegate.add(clauses));
		return true;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.sat.SatSolverState#close()
	 */
	@Override
	public void close() {
		executor.execute(delegate::close);
		executor.shutdown();
	}

}
