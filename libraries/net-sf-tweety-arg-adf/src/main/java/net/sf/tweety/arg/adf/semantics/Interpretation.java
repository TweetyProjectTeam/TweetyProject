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
package net.sf.tweety.arg.adf.semantics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.commons.AbstractInterpretation;

/**
 * 
 * This class represents an immutable three-valued interpretation of an Abstract
 * Dialectical Framework (ADF).
 * 
 * 
 * @author Mathias Hofer
 *
 */
public class Interpretation extends AbstractInterpretation<AbstractDialecticalFramework, Argument>
		implements Comparable<Interpretation> {

	private Set<Argument> satisfied;

	private Set<Argument> unsatisfied;

	private Set<Argument> undecided;

	public Interpretation(Collection<Argument> undecided) {
		this.satisfied = new HashSet<Argument>();
		this.unsatisfied = new HashSet<Argument>();
		this.undecided = new HashSet<Argument>(undecided);
	}
	
	public Interpretation(Iterable<Argument> undecided) {
		this.satisfied = new HashSet<Argument>();
		this.unsatisfied = new HashSet<Argument>();
		this.undecided = new HashSet<Argument>();
		for (Argument a : undecided) {
			this.undecided.add(a);
		}
	}

	public Interpretation(Map<Argument, Boolean> assignments) {
		this.satisfied = new HashSet<Argument>();
		this.unsatisfied = new HashSet<Argument>();
		this.undecided = new HashSet<Argument>();
		for (Argument a : assignments.keySet()) {
			if (assignments.get(a) == null) {
				undecided.add(a);
			} else if (assignments.get(a)) {
				satisfied.add(a);
			} else if (!assignments.get(a)) {
				unsatisfied.add(a);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		boolean first = true;
		for (Argument a : satisfied) {
			if (first) {
				s.append("t(" + a + ")");
				first = false;
			} else
				s.append(" t(" + a + ")");
		}
		for (Argument a : unsatisfied) {
			s.append(" f(" + a + ")");
		}
		for (Argument a : undecided) {
			s.append(" u(" + a + ")");
		}
		return s.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((satisfied == null) ? 0 : satisfied.hashCode());
		result = prime * result + ((undecided == null) ? 0 : undecided.hashCode());
		result = prime * result + ((unsatisfied == null) ? 0 : unsatisfied.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interpretation other = (Interpretation) obj;
		if (satisfied == null) {
			if (other.satisfied != null)
				return false;
		} else if (!satisfied.equals(other.satisfied))
			return false;
		if (undecided == null) {
			if (other.undecided != null)
				return false;
		} else if (!undecided.equals(other.undecided))
			return false;
		if (unsatisfied == null) {
			if (other.unsatisfied != null)
				return false;
		} else if (!unsatisfied.equals(other.unsatisfied))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Interpretation that) {
		// compares in terms of informativeness, i.e. if this has less undecided
		// truth values than that, then this is more informative and vice versa.
		// does however not satisfy (x.compareTo(y)==0) == (x.equals(y)).
		return that.undecided.size() - undecided.size();
	}

	@Override
	public boolean satisfies(Argument formula) throws IllegalArgumentException {
		return satisfied.contains(formula);
	}

	@Override
	public boolean satisfies(AbstractDialecticalFramework beliefBase) throws IllegalArgumentException {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return an unmodifiable set of all the arguments labeled by this
	 *         interpretation
	 */
	public Stream<Argument> arguments() {
		return Stream.concat(satisfied.stream(), Stream.concat(unsatisfied.stream(), undecided.stream()));
	}

	/**
	 * @return a copy of the set of satisfied arguments
	 */
	public Set<Argument> getSatisfied() {
		return new HashSet<Argument>(satisfied);
	}
	
	public Stream<Argument> satisfied() {
		return satisfied.stream();
	}
	
	/**
	 * @return a copy of the set of unsatisfied arguments
	 */
	public Set<Argument> getUnsatisfied() {
		return new HashSet<Argument>(unsatisfied);
	}

	public Stream<Argument> unsatisfied() {
		return unsatisfied.stream();
	}
	
	/**
	 * @return a copy of the set of undecided arguments
	 */
	public Set<Argument> getUndecided() {
		return new HashSet<Argument>(undecided);
	}

	public Stream<Argument> undecided() {
		return undecided.stream();
	}

	public int size() {
		return satisfied.size() + unsatisfied.size() + undecided.size();
	}

	public boolean isSatisfied(Argument a) {
		return satisfied.contains(a);
	}

	public boolean isUnsatisfied(Argument a) {
		return unsatisfied.contains(a);
	}

	public boolean isUndecided(Argument a) {
		return undecided.contains(a);
	}

	public boolean contains(Argument a) {
		return satisfied.contains(a) || unsatisfied.contains(a) || undecided.contains(a);
	}
	
	/**
	 * TODO define functionality
	 * @param a some argument
	 * @return true, false or null if this argument is labeled as satisfied,
	 *         unsatisfied or undecided.
	 * @throws IllegalArgumentException if the interpretation does not contain
	 *         the given argument.
	 */
	public Boolean get(Argument a) {
		if (satisfied.contains(a)) {
			return true;
		} else if (unsatisfied.contains(a)) {
			return false;
		} else if (undecided.contains(a)) {
			return null;
		}
		throw new IllegalArgumentException(a + " not labeled in this interpretation.");
	}

}
