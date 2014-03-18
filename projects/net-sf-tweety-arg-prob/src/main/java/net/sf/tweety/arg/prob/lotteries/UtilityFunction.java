package net.sf.tweety.arg.prob.lotteries;

import java.util.HashMap;
import java.util.Set;

import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.divisions.Division;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;

/**
 * A utility function that maps divisions to utilities
 * @author Matthias Thimm
 *
 */
public class UtilityFunction extends HashMap<Division,Double>{

	/** For serialization.  */
	private static final long serialVersionUID = -8506619629340455862L;

	/**
	 * Creates a new utility function. 
	 */
	public UtilityFunction(){
		super();
	}
	
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
	
	/**
	 * Gets the utility of the given theory wrt. the given semantics.
	 * @param theory some AAF
	 * @param semantics some semantics
	 * @return the utility of the theory wrt. the given semantics.
	 */
	public Double getUtility(DungTheory theory, int semantics){
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(theory, semantics, Semantics.CREDULOUS_INFERENCE);
		Set<Extension> extensions = reasoner.getExtensions();
		//average utility across extensions
		double util = 0;
		for(Extension e: extensions){
			for(Division d: this.keySet()){
				if(e.containsAll(d.getFirst())){
					Extension tmp = new Extension(e);
					tmp.retainAll(d.getSecond());
					if(tmp.size() == 0){
						util += this.get(d);
					}
				}
			}
		}
		return util/extensions.size();
	}
}
