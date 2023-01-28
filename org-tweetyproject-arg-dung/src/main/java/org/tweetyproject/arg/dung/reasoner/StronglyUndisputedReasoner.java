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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.reasoner;


/**
 * Implements the strongly undisputed semantics, as proposed in
 * [Thimm. On Undisputed Sets in Abstract Argumentation. AAAI2023].
 * This is an instance of the vacuous reduct reasoner with 
 * conflict-free semantics as base semantics and undisputed semantics as
 * reduct semantics.
 * 
 * @author Matthias Thimm
 *
 */
public class StronglyUndisputedReasoner extends VacuousReductReasoner{
	/**
	 * simple constructor
	 */
	public StronglyUndisputedReasoner() {
		super(new SimpleConflictFreeReasoner(), new UndisputedReasoner());
	}
}
