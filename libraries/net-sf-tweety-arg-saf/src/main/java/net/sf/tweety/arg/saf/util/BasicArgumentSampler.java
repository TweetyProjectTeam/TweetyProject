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
package net.sf.tweety.arg.saf.util;

import java.util.*;

import net.sf.tweety.arg.saf.syntax.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements a formula sampler for structured argumentation
 * frameworks.
 * 
 * @author Matthias Thimm
 *
 */
public class BasicArgumentSampler extends FormulaSampler<BasicArgument> {

	/**
	 * Creates a new basic argument sampler for the given signature.
	 * @param signature a signature
	 */
	public BasicArgumentSampler(Signature signature) {
		super(signature);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.FormulaSampler#randomSample(net.sf.tweety.kr.Signature, int)
	 */
	@Override
	public BasicArgument randomSample(int formula_length) {
		if(!(this.getSignature() instanceof PlSignature))
			throw new IllegalArgumentException("Signature must be a propositional signature.");
		// randomly choose conclusion
		Random random = new Random();
		List<Formula> propositions = new ArrayList<Formula>((PlSignature)this.getSignature()); 
		Proposition claim = (Proposition) propositions.get(random.nextInt(propositions.size()));
		propositions.remove(claim);
		// the int formula_length is interpreted as length of support.
		int thisLength = Math.min(random.nextInt(formula_length+1), propositions.size());
		Set<Proposition> support = new HashSet<Proposition>();
		for(int i = 0; i < thisLength; i++){
			int nextInt = random.nextInt(propositions.size());
			support.add((Proposition)propositions.get(nextInt));
			propositions.remove(nextInt);
		}
		return new BasicArgument(claim,support);
	}

}
