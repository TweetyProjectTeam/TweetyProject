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
import net.sf.tweety.logics.fol.semantics.*;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.rpcl.semantics.*;


/**
 * This class implements a simple writer for writing probability distributions.
 * 
 * @author Matthias Thimm
 */
public class DefaultProbabilityDistributionWriter extends Writer {

	/**
	 * Creates a new writer.
	 */
	public DefaultProbabilityDistributionWriter() {
		this(null);	
	}
	
	/**
	 * Creates a new writer for the given probability distribution.
	 * @param distribution a probability distribution.
	 */
	public DefaultProbabilityDistributionWriter(RpclProbabilityDistribution distribution) {
		super(distribution);	
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Writer#writeToString()
	 */
	@Override
	public String writeToString() {
		String result = "";
		RpclProbabilityDistribution distribution = (RpclProbabilityDistribution) this.getObject();
		NumberFormat formatter = new DecimalFormat("#.###################");
		for(Interpretation interpretation: distribution.keySet()){
			result += "{";
			boolean first = true;
			for(FOLAtom a: (HerbrandInterpretation)interpretation)
				if(first){
					result += a.toString();
					first = false;
				} else result += "," + a.toString();
			result += "}";
			result += " = " + formatter.format(distribution.get(interpretation).getValue()) + "\n";
		}		
		return result;
	}

}
