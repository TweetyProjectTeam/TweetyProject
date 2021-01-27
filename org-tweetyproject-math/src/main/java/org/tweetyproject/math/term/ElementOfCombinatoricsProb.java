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

package org.tweetyproject.math.term;

import java.util.ArrayList;

/**
 * This class implements an element for a combinatorial problem. It can be expanded later to implement more behavior
 * @author Sebastian Franke
 */

public class ElementOfCombinatoricsProb extends OptProbElement{
	public ArrayList<Term> components;
	
	public ElementOfCombinatoricsProb(ArrayList<Term> elements) {
		this.components = elements;
	}


}
