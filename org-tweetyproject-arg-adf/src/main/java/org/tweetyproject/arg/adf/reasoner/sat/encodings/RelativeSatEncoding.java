package org.tweetyproject.arg.adf.reasoner.sat.encodings;

import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * A SAT encoding which is relative to a given interpretation.
 *
 * @author Mathias
 */
public interface RelativeSatEncoding {

	/**
	 * Encodes clauses relative to the given interpretation.
	 *
	 * @param consumer the consumer that receives generated clauses
	 * @param interpretation the reference interpretation
	 */
	void encode(Consumer<Clause> consumer, Interpretation interpretation);
	
}
