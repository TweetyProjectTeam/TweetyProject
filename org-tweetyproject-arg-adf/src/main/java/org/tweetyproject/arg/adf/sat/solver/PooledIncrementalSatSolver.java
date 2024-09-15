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
package org.tweetyproject.arg.adf.sat.solver;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.SatSolverState;

/**
 * A decorator which provides a pool of pre computed {@link SatSolverState}.
 *
 * @author Mathias Hofer
 *
 */
public final class PooledIncrementalSatSolver implements IncrementalSatSolver {

	private final Executor executor;

	private final IncrementalSatSolver satSolver;

	private final BlockingQueue<SatSolverState> states;

	private final Function<SatSolverState, SatSolverState> stateDecorator;

	private boolean closed = false;

	/**
	 *
	 * @param executor
	 * @param satSolver
	 * @param size the number of states
	 */
	private PooledIncrementalSatSolver(Builder builder) {

			this.satSolver = builder.satSolver;
			this.executor = builder.executor;
			this.stateDecorator = builder.stateDecorator;
			this.states = new LinkedBlockingQueue<SatSolverState>(builder.poolSize);
			for (int i = 0; i < builder.poolSize; i++) {
				executor.execute(() -> states.offer(createDecoratedState()));
			}



	}
	/**
	 *
	 * @param satSolver satSolver
	 * @return Builder builder
	 */
	public static Builder builder(IncrementalSatSolver satSolver) {
		return new Builder(satSolver);
	}

	private SatSolverState createDecoratedState() {
		return stateDecorator.apply(satSolver.createState());
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.sat.IncrementalSatSolver#createState()
	 */
	@Override
	public SatSolverState createState() {
		if (closed) {
			throw new IllegalStateException("State pool already closed!");
		}

		try {
			SatSolverState taken = states.take();
			executor.execute(() -> states.offer(createDecoratedState()));
			return taken;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
/**
 * close
 */
	public void close() {
		if (closed) {
			throw new IllegalStateException("State pool already closed!");
		}
		closed = true;
		for (SatSolverState state : states) {
			executor.execute(state::close);
		}
		states.clear();
	}
	/**
	 * class Builder
	 * @author Sebastian
	 *
	 */
	public static final class Builder {

		private final IncrementalSatSolver satSolver;

		private int poolSize;

		private Executor executor = Executors.newSingleThreadExecutor();

		private Function<SatSolverState, SatSolverState> stateDecorator = Function.identity();

		private Builder(IncrementalSatSolver satSolver) {
			this.satSolver = Objects.requireNonNull(satSolver);
		}

		/**
		 * The executor used to perform the creation of the sat solver states.
		 * <p>
		 * As a default {@link Executors#newSingleThreadExecutor()} is used.
		 *
		 * @param executor the executor to set
		 * @return Builder setExecutor
		 */
		public Builder setExecutor(Executor executor) {
			this.executor = Objects.requireNonNull(executor);
			return this;
		}

		/**
		 * @param poolSize the poolSize to set
		 * @return Builder setPoolSize
		 */
		public Builder setPoolSize(int poolSize) {
			if (poolSize < 1) {
				throw new IllegalArgumentException("PoolSize must be > 0");
			}
			this.poolSize = poolSize;
			return this;
		}

		/**
		 * @param stateDecorator the stateDecorator to set
		 * @return Builder setStateDecorator
		 */
		public Builder setStateDecorator(Function<SatSolverState, SatSolverState> stateDecorator) {
			if (stateDecorator == null) {
				this.stateDecorator = Function.identity();
			} else {
				this.stateDecorator = stateDecorator;
			}
			return this;
		}
		/**
		 *
		 * @return PooledIncrementalSatSolver
		 */
		public PooledIncrementalSatSolver build() {
			return new PooledIncrementalSatSolver(this);
		}

	}

}
