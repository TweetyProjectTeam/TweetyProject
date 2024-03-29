/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.prob.lotteries;

import java.util.Collection;
import java.util.HashMap;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.divisions.Division;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.DungTheory;

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
	public Double getUtility(DungTheory theory, Semantics semantics){
		AbstractExtensionReasoner reasoner = AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics);
		Collection<Extension<DungTheory>> extensions = reasoner.getModels(theory);
		//average utility across extensions
		double util = 0;
		for(Extension<DungTheory> e: extensions){
			for(Division d: this.keySet()){
				if(e.containsAll(d.getFirst())){
					Extension<DungTheory> tmp = new Extension<DungTheory>(e);
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
