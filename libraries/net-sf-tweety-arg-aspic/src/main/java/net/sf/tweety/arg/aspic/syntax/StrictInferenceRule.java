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
package net.sf.tweety.arg.aspic.syntax;

import java.util.Collection;

import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * Indefeasible implementation of <code>InferenceRule&lt;T&gt;</code>
 *
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class StrictInferenceRule<T extends Invertable> extends InferenceRule<T> {
	
	/**
	 * Constructs an empty instance
	 */
	public StrictInferenceRule(){	
	}
	
	/**
	 * Constructs a strict inference rule p -&gt; c 
	 * @param conclusion	^= p
	 * @param premise	^= c
	 */
	public StrictInferenceRule(T conclusion, Collection<T> premise) {
		super(conclusion, premise);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.aspic.syntax.InferenceRule#isDefeasible()
	 */
	@Override
	public boolean isDefeasible() {
		return false;
	}

}
