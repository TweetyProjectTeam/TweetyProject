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
package net.sf.tweety.arg.aspic.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import net.sf.tweety.arg.aspic.syntax.AspicArgument;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * A simple comparator for Aspic Arguments, that compares their top rules according to a given list of rules
 * 
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class SimpleAspicOrder<T extends Invertable> implements Comparator<AspicArgument<T>> {
	
	/**
	 * The name of the rules ordered by size ascending
	 */
	private List<String> rules = new ArrayList<>();
	
	/**
	 * Creates a comparator for AspicArguments, that always returns 0
	 */
	public SimpleAspicOrder() {
		
	}
	
	
	/**
	 * Creates a comparator for AspicArguments from a list of AspicInferneceRules
	 * This will return a value &lt;0, ==0 or &gt;0 if the first argument's top rule is &lt;,=,&gt; the second 
	 * argument's top rule
	 * @param rules	list of rules, ordered by their value ascending
	 */
	public SimpleAspicOrder(Collection<String> rules) {
		this.rules.addAll(rules);
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(AspicArgument<T> a, AspicArgument<T> b) {
		int NULL = -1, val_a = NULL, val_b = NULL;
		for( int i = 0; i< rules.size(); i++) {
			if(rules.get(i).equals(a.getTopRule().getName()))
				val_a = i;
			if(rules.get(i).equals(b.getTopRule().getName()))
				val_b = i;
		}
		if(val_a == NULL || val_b == NULL) {
			return 0;
		}
		
		int result = val_a - val_b;
		
		//System.out.println(a +" - "+b+" = "+result);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimpleAspicOrder [" + rules + "]";
	}
	
	

}
