package org.tweetyproject.arg.adf.reasoner.sat.execution;

import java.util.Objects;

import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;

/**
 * 
 * @author Sebastian Matthias Thimm
 *
 */
public final class Configuration {

	
	private final IncrementalSatSolver satSolver;
	
	private final int parallelism;
	
	private Configuration(Builder builder) {
		this.satSolver = builder.satSolver;
		this.parallelism = builder.parallelism;
	}
	
	/**
	 * 
	 * @return builder
	 */
	public static Builder builder() {
		return new Builder();
	}
	
	/**
	 * 
	 * @return getSatSolver
	 */
	public IncrementalSatSolver getSatSolver() {
		return satSolver;
	}
	
	/**
	 * 
	 * @return getParallelism
	 */
	public int getParallelism() {
		return parallelism;
	}

	/**
	 * 
	 * @author Sebastian Matthias Thimm
	 *
	 */
	public static final class Builder {
		
		private IncrementalSatSolver satSolver = new NativeMinisatSolver();
		
		private int parallelism = Runtime.getRuntime().availableProcessors();
			
		/**
		 * 
		 * @param satSolver satSolver
		 * @return setSatSolver
		 */
		public Builder setSatSolver(IncrementalSatSolver satSolver) {
			this.satSolver = Objects.requireNonNull(satSolver);
			return this;
		}
		
		/** 
		 * @param parallelism the parallelism level used if a query is executed in parallel
		 * @return this builder
		 */
		public Builder setParallelism(int parallelism) {
			if (parallelism < 2) {
				throw new IllegalArgumentException("Parallelism level must be > 1");
			}
			this.parallelism = parallelism;
			return this;
		}
		
		/**
		 * 
		 * @return Configuration build
		 */
		public Configuration build() {
			return new Configuration(this);
		}
	}
	
}
