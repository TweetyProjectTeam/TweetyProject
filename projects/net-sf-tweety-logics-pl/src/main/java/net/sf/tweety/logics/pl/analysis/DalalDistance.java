package net.sf.tweety.logics.pl.analysis;

import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalSignature;

/**
 * This class models the dalal distance measure between possible worlds,
 * see e.g. [Grant, Hunter. Distance-based Measures of Inconsistency, ECSQARU'13].
 * It returns the number of propositions two possible possible worlds differ.
 * 
 * @author Matthias Thimm
 *
 */
public class DalalDistance extends PossibleWorldDistance {

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.pl.analysis.PossibleWorldDistance#distance(net.sf.tweety.logics.pl.semantics.PossibleWorld, net.sf.tweety.logics.pl.semantics.PossibleWorld)
	 */
	@Override
	public double distance(PossibleWorld a, PossibleWorld b) {
		int n = 0;
		PropositionalSignature sig = new PropositionalSignature();
		sig.addAll(a);
		sig.addAll(b);
		for(Proposition p: sig){
			if(a.contains(p) && !b.contains(p))
				n++;
			if(b.contains(p) && !a.contains(p))
				n++;
		}
		return n;
	}	
}
