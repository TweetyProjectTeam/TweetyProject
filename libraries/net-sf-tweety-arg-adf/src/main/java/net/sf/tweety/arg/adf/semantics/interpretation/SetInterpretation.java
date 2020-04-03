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
package net.sf.tweety.arg.adf.semantics.interpretation;

import java.util.Objects;
import java.util.Set;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.util.UnionSetView;

/**
 * This implementation is backed by three sets which allows
 * {@link #satisfied(Argument)}, {@link #unsatisfied(Argument)} and {@link #undecided(Argument)} to
 * return in constant time.
 * 
 * @author Mathias Hofer
 *
 */
public final class SetInterpretation implements Interpretation {

	private final Set<Argument> satisfied;

	private final Set<Argument> unsatisfied;

	private final Set<Argument> undecided;

	/**
	 * 
	 * @param satisfied
	 * @param unsatisfied
	 * @param undecided
	 */
	public SetInterpretation(Set<Argument> satisfied, Set<Argument> unsatisfied, Set<Argument> undecided) {
		if (satisfied.isEmpty() && unsatisfied.isEmpty() && undecided.isEmpty()) {
			throw new IllegalArgumentException("There must be at least one non-empty set!");
		}
		this.satisfied = Set.copyOf(satisfied);
		this.unsatisfied = Set.copyOf(unsatisfied);
		this.undecided = Set.copyOf(undecided);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.Interpretation#satisfied(net.sf.tweety.
	 * arg.adf.syntax.Argument)
	 */
	@Override
	public boolean satisfied(Argument arg) {
		return satisfied.contains(arg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.Interpretation#unsatisfied(net.sf.tweety.
	 * arg.adf.syntax.Argument)
	 */
	@Override
	public boolean unsatisfied(Argument arg) {
		return unsatisfied.contains(arg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.Interpretation#undecided(net.sf.tweety.
	 * arg.adf.syntax.Argument)
	 */
	@Override
	public boolean undecided(Argument arg) {
		return undecided.contains(arg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.semantics.Interpretation#satisfied()
	 */
	@Override
	public Set<Argument> satisfied() {
		return satisfied;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#arguments()
	 */
	@Override
	public Set<Argument> arguments() {
		return UnionSetView.of(satisfied, unsatisfied, undecided);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.semantics.Interpretation#unsatisfied()
	 */
	@Override
	public Set<Argument> unsatisfied() {
		return unsatisfied;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.tweety.arg.adf.semantics.Interpretation#undecided()
	 */
	@Override
	public Set<Argument> undecided() {
		return undecided;
	}
	
	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Interpretation)) {
			return false;
		}
		Interpretation other = (Interpretation) obj;
		return Objects.equals(satisfied, other.satisfied()) && 
				Objects.equals(undecided, other.undecided()) && 
				Objects.equals(unsatisfied, other.unsatisfied());
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

}
