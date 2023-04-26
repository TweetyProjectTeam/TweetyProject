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
package org.tweetyproject.arg.dung.serialisibility.equivalence;

import java.util.Collection;

import org.tweetyproject.arg.dung.equivalence.IEquivalence;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents an comparator, which defines if 2 sets of extensions are equivalent, 
 * by comparing if they consist of equivalent elements (naiv approach). 
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationEquivalenceByExtensionNaiv
		implements IEquivalence<Collection<Extension<DungTheory>>> {

	@Override
	public boolean isEquivalent(Collection<Extension<DungTheory>> obj1, Collection<Extension<DungTheory>> obj2) {
		for (Extension<DungTheory> ext1 : obj1) {
			boolean hasFoundEqual = false;
			for (Extension<DungTheory> ext2 : obj2) {
				if(ext2.equals(ext1)) { 
					hasFoundEqual = true;
					break;
				}
			}
			if(!hasFoundEqual) return false;
		}
		return true;
	}

	@Override
	public boolean isEquivalent(Collection<Collection<Extension<DungTheory>>> objects) {
		for (Collection<Extension<DungTheory>> collection : objects) {
			boolean foundEquivalent = false;
			for (Collection<Extension<DungTheory>> collection2 : objects) {
				if(collection == collection2) continue;
				if(isEquivalent(collection, collection2)) 
				{
					foundEquivalent = true;
					break;
				}
			}
			if(!foundEquivalent) return false;
		}
		return true;
	}

	@Override
	public Collection<Collection<Extension<DungTheory>>> getEquivalentTheories(
			Collection<Extension<DungTheory>> object) {
		// not supported
		return null;
	}

}
