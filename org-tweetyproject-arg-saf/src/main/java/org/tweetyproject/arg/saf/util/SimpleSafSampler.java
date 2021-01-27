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
package org.tweetyproject.arg.saf.util;

import java.util.*;

import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.arg.saf.syntax.*;
import org.tweetyproject.commons.*;


/**
 * This class implements a belief base sampler for structured argumentation
 * frameworks.
 * 
 * @author Matthias Thimm
 *
 */
public class SimpleSafSampler extends BeliefSetSampler<Argument,StructuredArgumentationFramework> {

	/**
	 * Creates a new SimpleSafSampler for the given signature. 
	 * @param signature a signature.
	 */
	public SimpleSafSampler(Signature signature) {
		super(signature);
	}
	

	/**
	 * Creates a new SimpleSafSampler for the given signature. 
	 * @param signature a signature.
	 * @param minLength the minimum length of knowledge bases
	 * @param maxLength the maximum length of knowledge bases
	 */
	public SimpleSafSampler(Signature signature, int minLength, int maxLength) {
		super(signature, minLength, maxLength);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.commons.BeliefSetSampler#next()
	 */
	@Override
	public StructuredArgumentationFramework next() {
		//bbLength is interpreted as the maximum number of basic arguments
		Random random = new Random();
		int thisLength = random.nextInt(this.getMaxLength()-this.getMinLength()+1) + this.getMinLength();
		BasicArgumentSampler argSampler = new BasicArgumentSampler(this.getSamplerSignature());
		StructuredArgumentationFramework saf = new StructuredArgumentationFramework();
		for(int i = 0; i < thisLength; i++)
			saf.add(argSampler.randomSample());
		//determine attacks with a probability of 1/5
		// - but do not allow self-attacks (these are useless in SAFs)
		// - avoid with a probability of 3/4 attacks between arguments with same claim
		// - avoid with a probability of 4/5 attacks between arguments where one
		//   argument might support the other
		for(Formula f1 : saf)
			for(Formula f2: saf)
				if(f1 != f2)
					if(random.nextInt(5) == 0){
						BasicArgument arg1 = (BasicArgument) f1;
						BasicArgument arg2 = (BasicArgument) f2;
						if(arg1.getConclusion().equals(arg2.getConclusion())){
							if(random.nextInt(4) == 0)
								saf.add(new Attack(arg1,arg2));
						}else if(arg1.getPremise().contains(arg2.getConclusion()) || arg2.getPremise().contains(arg1.getConclusion())){
							if(random.nextInt(5) == 0)
								saf.add(new Attack(arg1,arg2));
						}else{
							saf.add(new Attack(arg1,arg2));
						}
					}
		return saf;
	}
}
