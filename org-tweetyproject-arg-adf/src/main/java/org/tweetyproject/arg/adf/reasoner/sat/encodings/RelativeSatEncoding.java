package org.tweetyproject.arg.adf.reasoner.sat.encodings;

import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * A SatEncoding which is relative to a given interpretation.
 * 
 * @author Mathias
 *
 */
public interface RelativeSatEncoding {

	/**
	 * 
	 * @param consumer consumer
	 * @param interpretation interpretation
	 */
	void encode(Consumer<Clause> consumer, Interpretation interpretation);
	
}
