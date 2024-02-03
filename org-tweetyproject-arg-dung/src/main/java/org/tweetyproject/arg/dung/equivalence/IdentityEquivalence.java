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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence;

import java.util.Collection;

import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class defines 'syntactic' equivalence of {@link DungTheory Argumentation Frameworks},
 * i.e., it checks whether two AFs have exactly the same arguments and attacks.
 *
 * @author Julian Sander
 */
public class IdentityEquivalence implements Equivalence<DungTheory>  {

	@Override
	public boolean isEquivalent(DungTheory obj1, DungTheory obj2) {
		return obj1.equals(obj2);
	}

	@Override
	public boolean isEquivalent(Collection<DungTheory> objects) {
		DungTheory first = objects.iterator().next();
		for (DungTheory framework : objects) {
			if(framework == first) {
				continue;
			}
			if(!this.isEquivalent(framework, first)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return "Syntactic Equivalence";
	}

}
