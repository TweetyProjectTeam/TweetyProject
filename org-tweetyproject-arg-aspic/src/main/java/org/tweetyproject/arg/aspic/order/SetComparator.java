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

import java.util.Collection;
import java.util.Comparator;

/**
 * @author Nils Geilen
 *
 * A comparator for sets of T according to def.3.19 in Mogdil and Prakken
 *
 * @param <T>	type of the compared sets' elements
 */
public class SetComparator<T> implements Comparator<Collection<T>> {

	/**
	 * A comparator for single elements of type T
	 */
	private Comparator<T> comp;

	/**
	 * When this is true the comparision will be elitist according to
	 * def.3.19 in Mogdil and Prakken
	 */
	private boolean elitist;

	/**
	 * Constructs an new set comparator
	 * @param comp	comparator for single elements
	 * @param elitist	elitist or democratic behvaiour
	 */
	public SetComparator(Comparator<T> comp, boolean elitist) {
		super();
		this.comp = comp;
		this.elitist = elitist;
	}

	private boolean isStrictlyLessOrEquallyAcceptableThan(Collection<T> gamma1, Collection<T> gamma2) {
		if (elitist) {
			for (T t : gamma1) {
				int i = 0;
				for (T u : gamma2) {
					if (comp.compare(t, u) <= 0)
						i++;
				}
				if (i == gamma2.size())
					return true;
			}
			return false;
		} else {
			int i = 0;
			for (T t : gamma1) {
				for (T u : gamma2) {
					if (comp.compare(t, u) <= 0) {
						i++;
						break;
					}
				}
			}
			if (i == gamma1.size())
				return true;
			else
				return false;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Collection<T> gamma1, Collection<T> gamma2) {
		if (gamma1.isEmpty() && gamma2.isEmpty())
			return 0;
		if (gamma1.isEmpty() && !gamma2.isEmpty())
			return -1;
		if (gamma2.isEmpty() && !gamma1.isEmpty())
			return 1;

		boolean soe = isStrictlyLessOrEquallyAcceptableThan(gamma1, gamma2);
		boolean goe = isStrictlyLessOrEquallyAcceptableThan(gamma2, gamma1);

		if(soe && goe)
			return 0;
		if(soe)
			return -1;
		return 1;
	}



}
