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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung.reasoner;

import java.util.Collection;
import java.util.HashSet;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.InferenceMode;

/**
 * Implements the IAQ approach ("iterative acceptability queries") to
 * determine the set of acceptable arguments of an AF, cf. [Thimm, Cerutti, Vallati; 2020, in preparation].
 * It uses another extension-based reasoner to query for each argument
 * separately whether it is accepted.
 * 
 * @author Matthias Thimm
 *
 */
public class IaqAcceptabilityReasoner extends AbstractAcceptabilityReasoner {

	private AbstractExtensionReasoner reasoner;
	private InferenceMode inferenceMode;
	
	/**
	 * Creates a new IaqAcceptabilityReasoner.
	 * @param reasoner the internal reasoner used.
	 * @param inferenceMode the inference mode.
	 */
	public IaqAcceptabilityReasoner(AbstractExtensionReasoner reasoner, InferenceMode inferenceMode) {
		this.reasoner = reasoner;
		this.inferenceMode = inferenceMode;
	}
	
	@Override
	public Collection<Argument> getAcceptableArguments(DungTheory aaf) {
		Collection<Argument> result = new HashSet<Argument>();
		for(Argument a: aaf)
			if(this.reasoner.query(aaf, a, this.inferenceMode))
				result.add(a);
		return result;
	}	
}
