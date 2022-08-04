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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.tweetyproject.arg.aspic.syntax.AspicArgument;
import org.tweetyproject.arg.aspic.syntax.AspicArgumentationTheory;
import org.tweetyproject.arg.aspic.syntax.InferenceRule;
import org.tweetyproject.comparator.GeneralComparator;
import org.tweetyproject.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen
 * 
 * A comparator for Aspic Arguments, that compares the set of topmost deafeasible rules
 * 
 * @param <T>	is the type of the language that the ASPIC theory's rules range over 
 */
public class LastLinkOrder <T extends Invertable> extends GeneralComparator<AspicArgument<T>, AspicArgumentationTheory<T>> {
	

	
	/**
	 * Comparators for defeasible rules and ordinary premises
	 */
	private Comparator<Collection<InferenceRule<T>>> ruleset_comp;
	private Comparator<Collection<InferenceRule<T>>> premset_comp;

	

	/**
	 * Constructs a new last link ordering
	 * @param rule_comp	comparator for defeasible rules	
	 * @param prem_comp	comparator for ordinary premises
	 * @param elitist	some boolean
	 */
	public LastLinkOrder(Comparator<InferenceRule<T>> rule_comp, Comparator<InferenceRule<T>> prem_comp, boolean elitist) {
		ruleset_comp = new SetComparator<>(rule_comp, elitist);
		premset_comp = new SetComparator<>(prem_comp, elitist);
		
	}



	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(AspicArgument<T> a, AspicArgument<T> b) {
		if(!a.hasDefeasibleSub() && ! b.hasDefeasibleSub()) {
			List<InferenceRule<T>> a_prems = new ArrayList<>();
			for (AspicArgument<T> arg :a.getOrdinaryPremises() )
				a_prems.add(arg.getTopRule());
			List<InferenceRule<T>> b_prems = new ArrayList<>();
			for (AspicArgument<T> arg :b.getOrdinaryPremises() )
				b_prems.add(arg.getTopRule());
			return premset_comp.compare(a_prems, b_prems);
		}
		
		Collection<InferenceRule<T>> alir = a.getListLastDefeasibleRules();
		Collection<InferenceRule<T>> blir = b.getListLastDefeasibleRules();
		return ruleset_comp.compare(alir, blir);
	}



	@Override
	public boolean satisfies(AspicArgument<T> formula) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean satisfies(AspicArgumentationTheory<T> beliefBase) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean isStrictlyLessOrEquallyAcceptableThan(AspicArgument<T> a, AspicArgument<T> b) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean isIncomparable(AspicArgument<T> a, AspicArgument<T> b) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean containsIncomparableArguments() {
		// TODO Auto-generated method stub
		return false;
	}

}
