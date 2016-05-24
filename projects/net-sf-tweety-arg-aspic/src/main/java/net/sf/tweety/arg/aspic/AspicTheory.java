package net.sf.tweety.arg.aspic;

import net.sf.tweety.arg.aspic.semantics.AspicArgumentationSystem;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Signature;

/**
 * This class implements argumentation theories of the ASPIC+ framework, cf. Def. 3.5
 * in [Modgil, Prakken. The ASPIC+ framework for structured argumentation: a tutorial.
 * Argument and Computation 5 (2014): 31-62]
 * 
 * @author Matthias Thimm
 *
 */
public class AspicTheory implements BeliefBase {
	
	AspicArgumentationSystem as;

	public AspicTheory(AspicArgumentationSystem as) {
		super();
		this.as = as;		
		
		as.expand();
	}



	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String toString() {
		return as.toString();
	}
	
	

}
