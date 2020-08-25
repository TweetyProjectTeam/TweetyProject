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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner.heuristics;

import java.util.Comparator;
import java.util.Objects;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public final class AcceptanceConditionSizeComparator implements Comparator<Argument>{

	private final AbstractDialecticalFramework adf;
	
	/**
	 * @param adf
	 */
	public AcceptanceConditionSizeComparator(AbstractDialecticalFramework adf) {
		this.adf = Objects.requireNonNull(adf);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Argument a1, Argument a2) {
		AcceptanceCondition acc1 = adf.getAcceptanceCondition(a1);
		AcceptanceCondition acc2 = adf.getAcceptanceCondition(a2);
		return Long.compare(acc1.arguments().count(), acc2.arguments().count());
	}

}
