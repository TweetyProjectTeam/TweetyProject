/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.prob.lotteries;

import java.util.HashMap;
import java.util.Set;

import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.ldo.semantics.LdoInterpretation;
import net.sf.tweety.arg.dung.ldo.syntax.LdoFormula;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;

/**
 * A utility function that maps LDO formulas to utilities
 * @author Matthias Thimm
 *
 */
public class LdoUtilityFunction extends HashMap<LdoFormula,Double>{

	/** For serialization.  */
	private static final long serialVersionUID = -1506613623345465762L;

	/**
	 * Creates a new utility function. 
	 */
	public LdoUtilityFunction(){
		super();
	}
	
	/**
	 * Returns the expected utility of the given lottery.
	 * @param lottery some lottery
	 * @return the expected utility of the given lottery.
	 */
	public Double getExpectedUtility(LdoArgumentationLottery lottery){
		double d = 0;
		for(LdoFormula f: this.keySet())
			d += this.get(f) * lottery.get(f).doubleValue();		
		return d;
	}
	
	/**
	 * Gets the utility of the given theory wrt. the given semantics.
	 * @param theory some AAF
	 * @param semantics some semantics
	 * @return the utility of the theory wrt. the given semantics.
	 */
	public Double getUtility(DungTheory theory, Semantics semantics){
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getReasonerForSemantics(theory, semantics, Semantics.CREDULOUS_INFERENCE);
		Set<Extension> extensions = reasoner.getExtensions();
		//average utility across extensions
		double util = 0;
		for(Extension e: extensions){
			for(LdoFormula f: this.keySet()){
				LdoInterpretation i = new LdoInterpretation(theory,e,semantics);
				if(i.satisfies(f))
					util += this.get(f);				
			}
		}
		return util/extensions.size();
	}
}
