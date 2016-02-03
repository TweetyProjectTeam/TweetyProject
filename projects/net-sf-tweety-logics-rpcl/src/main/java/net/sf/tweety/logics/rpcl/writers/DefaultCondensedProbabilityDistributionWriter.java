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
package net.sf.tweety.logics.rpcl.writers;

import java.text.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.rpcl.*;

/**
 * This class implements a simple writer for writing condensed probability distributions.
 * 
 * @author Matthias Thimm
 */
public class DefaultCondensedProbabilityDistributionWriter extends Writer {
	
	/**
	 * Creates a new writer.
	 */
	public DefaultCondensedProbabilityDistributionWriter() {
		this(null);	
	}
	
	/**
	 * Creates a new writer for the given condensed probability distribution.
	 * @param distribution a condensed probability distribution.
	 */
	public DefaultCondensedProbabilityDistributionWriter(CondensedProbabilityDistribution distribution) {
		super(distribution);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Writer#writeToString()
	 */
	@Override
	public String writeToString() {
		String result = "";
		CondensedProbabilityDistribution distribution = (CondensedProbabilityDistribution) this.getObject();
		NumberFormat formatter = new DecimalFormat("#.###################"); 
		for(Interpretation interpretation: distribution.keySet()){
			result += "{";
			boolean first = true;
			for(InstanceAssignment ia: ((ReferenceWorld)interpretation).values()){
				if(first){
					result += ia.toString();
					first = false;
				}else{
					result += "," + ia.toString();
				}
			}
			result += "}";
			result += " = " + formatter.format(distribution.get(interpretation).getValue()) + "\n";
		}
		return result;
	}	

}
