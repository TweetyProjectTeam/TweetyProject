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
package org.tweetyproject.arg.adf.reasoner.heuristics;

import java.util.Comparator;
import java.util.Objects;

import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public final class DegreeComparator implements Comparator<Argument> {

	public static enum DegreeType {
		INCOMING, OUTGOING, UNDIRECTED
	}

	private final DegreeType degreeType;
	
	private final AbstractDialecticalFramework adf;

	/**
	 * 
	 * @param degreeType
	 *            specifies the type of the degree
	 */
	public DegreeComparator(AbstractDialecticalFramework adf, DegreeType degreeType) {
		this.adf = Objects.requireNonNull(adf);
		this.degreeType = Objects.requireNonNull(degreeType);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Argument a1, Argument a2) {
		int degree1 = degree(a1, adf);
		int degree2 = degree(a2, adf);
		return Integer.compare(degree1, degree2);
	}

	private int degree(Argument arg, AbstractDialecticalFramework adf) {
		switch (degreeType) {
		case INCOMING:
			return adf.incomingDegree(arg);
		case OUTGOING:
			return adf.outgoingDegree(arg);
		case UNDIRECTED:
			return adf.incomingDegree(arg) + adf.outgoingDegree(arg);
		}
		throw new AssertionError();
	}

}
