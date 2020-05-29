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
package net.sf.tweety.arg.adf.reasoner.ordering;

import java.util.stream.Stream;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public final class ArgumentDegreeOrdering extends AbstractOrdering<Argument> {

	public static enum DegreeType {
		INCOMING,
		OUTGOING,
		UNDIRECTED
	}
	
	private final DegreeType degreeType;
	
	/**
	 * 
	 * @param degreeType specifies the type of the degree
	 */
	public ArgumentDegreeOrdering(DegreeType degreeType) {
		this.degreeType = degreeType;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.ordering.AbstractOrdering#compare(java.
	 * lang.Object, java.lang.Object,
	 * net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	protected int compare(Argument a1, Argument a2, AbstractDialecticalFramework adf) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.ordering.AbstractOrdering#stream(net.sf.
	 * tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	protected Stream<Argument> stream(AbstractDialecticalFramework adf) {
		return adf.getArguments().stream();
	}

}
