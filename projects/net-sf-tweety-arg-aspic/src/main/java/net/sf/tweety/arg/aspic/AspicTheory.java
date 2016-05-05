package net.sf.tweety.arg.aspic;

import java.util.Set;

import net.sf.tweety.arg.aspic.semantics.ArgumentationSystem;
import net.sf.tweety.arg.aspic.syntax.Word;
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
	
	ArgumentationSystem as;
	Set<Word> kb;

	public AspicTheory(ArgumentationSystem as, Set<Word> premises) {
		super();
		this.as = as;
		kb = premises;
		as.expand(kb);
	}



	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String toString() {
		return "AspicTheory [as=" + as + ",\n premises=" + kb + "]";
	}
	
	

}
