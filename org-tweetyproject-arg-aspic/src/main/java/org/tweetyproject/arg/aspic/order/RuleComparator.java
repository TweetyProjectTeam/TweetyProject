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
 package org.tweetyproject.arg.aspic.order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.tweetyproject.arg.aspic.syntax.AspicArgument;
import org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory;
import org.tweetyproject.arg.aspic.syntax.InferenceRule;
import org.tweetyproject.comparator.TweetyComparator;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * A simple comparator, that compares inference rules
 * 
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class RuleComparator <T extends Invertable> extends TweetyComparator<InferenceRule<T>, AspicArgumentationTheory<T>> {

	/**
	 * The name of the rules ordered by size ascending
	 */
	private List<String> rules = new ArrayList<>();
	
	

	/**
	 * Constructs a new comparator for rules
	 * @param rules	rules in ascending order
	 */
	public RuleComparator(List<String> rules) {
		super();
		this.rules = rules;
	}



	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(InferenceRule<T> o1, InferenceRule<T> o2) {
		int NULL = -1, val_a = NULL, val_b = NULL;
		for( int i = 0; i< rules.size(); i++) {
			if(rules.get(i).equals(o1.getName()))
				val_a = i;
			if(rules.get(i).equals(o2.getName()))
				val_b = i;
		}
		if(val_a == NULL || val_b == NULL) {
			return 0;
		}
		
		int result = val_a - val_b;
		
		//System.out.println(a +" - "+b+" = "+result);
		
		return result;
	}



	@Override
	public boolean satisfies(InferenceRule<T> formula) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean satisfies(AspicArgumentationTheory<T> beliefBase) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean isStrictlyLessOrEquallyAcceptableThan(InferenceRule<T> a, InferenceRule<T> b) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean isIncomparable(InferenceRule<T> a, InferenceRule<T> b) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean containsIncomparableArguments() {
		// TODO Auto-generated method stub
		return false;
	}

}
