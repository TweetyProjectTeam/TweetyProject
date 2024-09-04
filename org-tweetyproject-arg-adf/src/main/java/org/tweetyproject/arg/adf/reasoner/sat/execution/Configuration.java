package org.tweetyproject.arg.adf.reasoner.sat.execution;

import java.util.Objects;

import org.tweetyproject.arg.adf.sat.IncrementalSatSolver;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;

/**
 * The {@code Configuration} class encapsulates the settings used for SAT solving and parallel execution
 * in an abstract dialectical framework (ADF) system. It provides a fluent API for configuring the solver
 * and the parallelism level.
 * <p>
 * The configuration is built using the {@link Builder} pattern, allowing users to specify an
 * {@link IncrementalSatSolver} and a level of parallelism for parallel execution. The class is immutable
 * once built.
 * </p>
 *
 * <pre>
 * Example usage:
 * Configuration config = Configuration.builder()
 *                                      .setSatSolver(new SomeSatSolver())
 *                                      .setParallelism(4)
 *                                      .build();
 * </pre>
 *
 * @author Sebastian Matthias Thimm
 */
public final class Configuration {

    /** The SAT solver used in the configuration. */
	private final IncrementalSatSolver satSolver;

	/** The parallelism level used for parallel execution. */
	private final int parallelism;

	/**
	 * Private constructor for the {@code Configuration} class.
	 * Instances are created using the {@link Builder}.
	 *
	 * @param builder the builder object containing the configuration settings
	 */
	private Configuration(Builder builder) {
		this.satSolver = builder.satSolver;
		this.parallelism = builder.parallelism;
	}

	/**
	 * Returns a new {@link Builder} for constructing {@code Configuration} objects.
	 *
	 * @return a new {@link Builder} instance
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Gets the configured {@link IncrementalSatSolver}.
	 *
	 * @return the configured SAT solver
	 */
	public IncrementalSatSolver getSatSolver() {
		return satSolver;
	}

	/**
	 * Gets the configured level of parallelism.
	 *
	 * @return the parallelism level
	 */
	public int getParallelism() {
		return parallelism;
	}

	/**
	 * The {@code Builder} class for constructing {@link Configuration} instances.
	 * It allows for the customization of the SAT solver and the parallelism level.
	 *
	 * <pre>
	 * Example usage:
	 * Configuration config = Configuration.builder()
	 *                                      .setSatSolver(new SomeSatSolver())
	 *                                      .setParallelism(4)
	 *                                      .build();
	 * </pre>
	 *
	 * @author Sebastian Matthias Thimm
	 */
	public static final class Builder {

		/** The SAT solver used by default, initialized to {@link NativeMinisatSolver}. */
		private IncrementalSatSolver satSolver = new NativeMinisatSolver();

		/** The parallelism level, initialized to the number of available processors. */
		private int parallelism = Runtime.getRuntime().availableProcessors();

		/**
		 * Sets the {@link IncrementalSatSolver} to be used in the {@link Configuration}.
		 *
		 * @param satSolver the SAT solver to use, must not be null
		 * @return this builder instance for method chaining
		 * @throws NullPointerException if {@code satSolver} is null
		 */
		public Builder setSatSolver(IncrementalSatSolver satSolver) {
			this.satSolver = Objects.requireNonNull(satSolver);
			return this;
		}

		/**
		 * Sets the parallelism level used for parallel execution.
		 *
		 * @param parallelism the parallelism level, must be greater than 1
		 * @return this builder instance for method chaining
		 * @throws IllegalArgumentException if {@code parallelism} is less than 2
		 */
		public Builder setParallelism(int parallelism) {
			if (parallelism < 2) {
				throw new IllegalArgumentException("Parallelism level must be > 1");
			}
			this.parallelism = parallelism;
			return this;
		}

		/**
		 * Builds and returns a new {@link Configuration} instance based on the current settings.
		 *
		 * @return a new {@link Configuration} instance
		 */
		public Configuration build() {
			return new Configuration(this);
		}
	}
}

