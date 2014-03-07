package net.sf.tweety.arg.prob.lotteries;

import java.util.HashMap;

import net.sf.tweety.arg.dung.divisions.Division;

/**
 * A utility function that maps divisions to utilities
 * @author Matthias Thimm
 *
 */
public class UtilityFunction extends HashMap<Division,Double>{

	/** For serialization.  */
	private static final long serialVersionUID = -8506619629340455862L;

	/**
	 * Returns the expected utility of the given lottery.
	 * @param lottery some lottery
	 * @return the expected utility of the given lottery.
	 */
	public Double getExpectedUtility(ArgumentationLottery lottery){
		double d = 0;
		for(Division div: this.keySet())
			d += this.get(div) * lottery.get(div).doubleValue();		
		return d;
	}
	
	
}
