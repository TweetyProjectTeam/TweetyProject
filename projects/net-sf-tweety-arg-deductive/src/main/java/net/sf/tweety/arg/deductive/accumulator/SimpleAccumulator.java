package net.sf.tweety.arg.deductive.accumulator;

import java.util.List;

/**
 * This implementation of an accumulator simply sums
 * up the categorizations of the argument trees.
 * Values of pro-trees are added and values of
 * con-trees are subtracted.
 *  
 * @author Matthias Thimm
 *
 */
public class SimpleAccumulator implements Accumulator {

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.accumulator.Accumulator#accumulate(java.util.List, java.util.List)
	 */
	@Override
	public double accumulate(List<Double> pro, List<Double> contra) {
		double result = 0;
		for(Double d: pro) result += d;
		for(Double d: contra) result -= d;
		return result;
	}

}
