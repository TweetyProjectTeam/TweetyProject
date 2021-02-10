package org.tweetyproject.arg.adf.reasoner.sat.pipeline;

import java.util.Objects;

import org.tweetyproject.arg.adf.reasoner.sat.parallel.Decomposer;
import org.tweetyproject.arg.adf.reasoner.sat.parallel.RandomDecomposer;
import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.solver.NativePicosatSolver;

public final class Configuration {

	private final IncrementalSatSolver satSolver;
	
	private final int parallelism;
	
	private final Decomposer decomposer;

	private Configuration(Builder builder) {
		this.satSolver = builder.satSolver;
		this.parallelism = builder.parallelism;
		this.decomposer = builder.decomposer;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public IncrementalSatSolver getSatSolver() {
		return satSolver;
	}
	
	public int getParallelism() {
		return parallelism;
	}
	
	public Decomposer getDecomposer() {
		return decomposer;
	}
	
	public static final class Builder {
		
		private IncrementalSatSolver satSolver = new NativePicosatSolver();
		
		private int parallelism = Runtime.getRuntime().availableProcessors();
		
		private Decomposer decomposer = new RandomDecomposer();
		
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
		
		public Builder setDecomposer(Decomposer decomposer) {
			this.decomposer = Objects.requireNonNull(decomposer);
			return this;
		}
		
		public Configuration build() {
			return new Configuration(this);
		}
	}
	
}
