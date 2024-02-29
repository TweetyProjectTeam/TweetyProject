/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package reasoner;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.weighted.syntax.WeightedArgumentationFramework;
import org.tweetyproject.commons.util.SetTools;

/**
 * This reasoner for weighted Dung theories performs inference on the  alpha conflict-free extensions.
 * @author Sandra Hoffmann
 *
 */
public class SimpleWeightedConflictFreeReasoner<T> {

	public Collection<Extension<DungTheory>> getModels(WeightedArgumentationFramework<T> bbase, T alpha) {
		Set<Extension<DungTheory>> extensions = new HashSet<Extension<DungTheory>>();
		// Check all subsets

		for(Set<Argument> ext: new SetTools<Argument>().subsets(bbase))
			if(bbase.isAlphaConflictFree(alpha,new Extension<DungTheory>(ext)))
				extensions.add(new Extension<DungTheory>(ext));
		return extensions;
	}


	public Extension<DungTheory> getModel(WeightedArgumentationFramework<T> bbase, T alpha) {
		// as the empty set is always conflict-free we return that one.
		return new Extension<DungTheory>();
	}

}
