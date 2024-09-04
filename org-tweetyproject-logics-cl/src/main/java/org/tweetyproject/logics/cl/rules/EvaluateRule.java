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
package org.tweetyproject.logics.cl.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.tweetyproject.logics.cl.kappa.KappaMin;
import org.tweetyproject.logics.cl.kappa.KappaSum;
import org.tweetyproject.logics.cl.kappa.KappaTerm;
import org.tweetyproject.logics.cl.kappa.KappaValue;

/**
 * The evaluate rule tries to evaluate the kappa values by using logical
 * constraints, therefore the {@link KappaTerm} evaluate() methods are called.
 *
 * In fact this rule uses multiple rules, because evaluate() is implemented in a
 * different way on the classes that implement the {@link KappaTerm} interface:
 * - It evaluates {@link KappaMin} instances by proofing if there is an element e
 *   which is evaluated and uses the value of this element if there is no greater
 *   or equal constraint on another element that is smaller as the value of element e.
 * - The {@link KappaSum} instances are simply evaluated if every sub-element of the sum can be evaluated
 * - The {@link KappaValue} instances are evaluated as soon as their {@link KappaMin} members are
 *   evaluated.
 *
 * @author Tim Janus
 */
public class EvaluateRule extends RuleAdapter {
	/** Default */
	public EvaluateRule(){
		super();
	}

	/** stores the old values of the kappa and is used to realize if some progress in calculation occured */
	private Map<KappaTerm, Integer> progressMap = new HashMap<KappaTerm, Integer>();

	@Override
	public void setKappas(Collection<KappaValue> kappas) {
		this.kappas = new ArrayList<KappaValue>(kappas);
		for(KappaValue k : kappas) {
			progressMap.put(k, k.value());
			for(KappaTerm subterm : k.getSubTerms()) {
				progressMap.put(subterm, subterm.value());
			}
		}
	}

	@Override
	public boolean apply() {
		// first update the progress-map otherwise there might be changes of other rules:
		changeOccurred();

		// then evaluate each kappa
		for(KappaValue kappa : kappas) {
			kappa.evaluate();
		}

		// and update the progress-map again and also return if a change occurred:
		return changeOccurred();
	}

	/**
	 * Updates the progressMap and also checks if a change to one kappa value is occurred sicnge the last application of
	 * this rule.
	 * @return true if a change occured and false otherwise
	 */
	private boolean changeOccurred() {
		boolean reval = false;
		for(Entry<KappaTerm, Integer> entry : progressMap.entrySet()) {
			Integer cur = entry.getKey().value();
			if(cur != entry.getValue()) {
				reval = true;
				entry.setValue(cur);
			}
		}
		return reval;
	}
}
