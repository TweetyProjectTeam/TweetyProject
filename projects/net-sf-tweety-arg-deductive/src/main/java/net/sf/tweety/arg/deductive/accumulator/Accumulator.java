package net.sf.tweety.arg.deductive.accumulator;

import java.util.List;

/**
 * Classes implementing this interface represent accumulators in the sense
 * of Definition 8.11 in<br/>
 * <br/>
 * Philippe Besnard and Anthony Hunter. A logic-based theory of deductive arguments.
 * In Artificial Intelligence, 128(1-2):203-235, 2001.
 * 
 * @author Matthias Thimm
 */
public interface Accumulator {

	/**
	 * Accumulates the pros and contras of the given list into a
	 * single double. The higher the value the more convincing
	 * the pros, the lower the more convincing the contras. In
	 * general, if the value is greater 0 then the query under
	 * consideration can be accepted.
	 * @param pro some doubles.
	 * @param contra some doubles.
	 * @return a double.
	 */
	public double accumulate(List<Double> pro, List<Double> contra);
}
