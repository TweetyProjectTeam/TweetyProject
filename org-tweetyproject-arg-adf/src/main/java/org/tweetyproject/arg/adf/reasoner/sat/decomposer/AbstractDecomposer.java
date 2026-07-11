package org.tweetyproject.arg.adf.reasoner.sat.decomposer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.InterpretationIterator;
import org.tweetyproject.arg.adf.semantics.interpretation.TwoValuedInterpretationIterator;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Base implementation of an ADF decomposer.
 */
abstract class AbstractDecomposer implements Decomposer {

	/**
	 * The abstract dialectical framework to decompose
	 */
	private final AbstractDialecticalFramework adf;

	/**
	 * Constructs a decomposer for the given ADF.
	 *
	 * @param adf the abstract dialectical framework
	 */
	protected AbstractDecomposer(AbstractDialecticalFramework adf) {
		this.adf = Objects.requireNonNull(adf);
	}

	@Override
	public Collection<Interpretation> decompose(int desired) {
		Set<Argument> arguments = partition(adf, arguments(adf, desired, 3));
		Iterator<Interpretation> interpretations = new InterpretationIterator(arguments);

		List<Interpretation> prefixes = new ArrayList<>();
		while(interpretations.hasNext()) {
			prefixes.add(interpretations.next());
		}
		return prefixes;
	}

	/**
	 * Returns a decomposer using two-valued interpretations
	 * @return asTwoValued
	 */
	public Decomposer asTwoValued() {
		return new TwoValuedDecomposer();
	}

	/**
	 * Calculates the number of arguments based on desired decomposition level and truth values
	 * @param adf the abstract dialectical framework
	 * @param desired the desired number of interpretations
	 * @param truthValues the number of truth values
	 * @return the calculated number of arguments
	 */
	private static int arguments(AbstractDialecticalFramework adf, int desired, int truthValues) {
		return Math.min( adf.size(), (int) Math.round( Math.log( desired ) / Math.log(truthValues) ) );
	}

	/**
	 * Partitions arguments into a specified number of groups
	 * @param adf the abstract dialectical framework
	 * @param size the number of arguments to partition
	 * @return the set of partitioned arguments
	 */
	abstract Set<Argument> partition(AbstractDialecticalFramework adf, int size);

	/**
	 * Inner class implementing two-valued decomposition strategy.
	 */
	private final class TwoValuedDecomposer implements Decomposer {

		/** Creates a two-valued decomposer. */
		private TwoValuedDecomposer() {
		}

		@Override
		public Collection<Interpretation> decompose(int desired) {
			Set<Argument> arguments = partition(adf, arguments(adf, desired, 2));
			Iterator<Interpretation> interpretations = new TwoValuedInterpretationIterator(arguments);

			List<Interpretation> prefixes = new ArrayList<>();
			while(interpretations.hasNext()) {
				prefixes.add(interpretations.next());
			}
			return prefixes;
		}

	}

}
