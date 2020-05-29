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

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * @author Mathias Hofer
 *
 */
public final class EmptyInterpretation implements Interpretation {

	private final Set<Argument> undecided;
	
	/**
	 * An empty interpretation relative to the given ADF
	 * 
	 * @param adf the contextual ADF
	 */
	public EmptyInterpretation(AbstractDialecticalFramework adf) {
		this.undecided = Set.copyOf(adf.getArguments());
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
		return false;
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
		return false;
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
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#arguments()
	 */
	@Override
	public Set<Argument> arguments() {
		return undecided;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.Interpretation#satisfied()
	 */
	@Override
	public Set<Argument> satisfied() {
		return Set.of();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.Interpretation#unsatisfied()
	 */
	@Override
	public Set<Argument> unsatisfied() {
		return Set.of();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.Interpretation#undecided()
	 */
	@Override
	public Set<Argument> undecided() {
		return undecided;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#subset(net.sf.tweety.arg.adf.semantics.interpretation.Interpretation)
	 */
	@Override
	public boolean isSubsetOf(Interpretation superset) {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#numDecided()
	 */
	@Override
	public int numDecided() {
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#isStrictSupersetOf(net.sf.tweety.arg.adf.semantics.interpretation.Interpretation)
	 */
	@Override
	public boolean isStrictSupersetOf(Interpretation subset) {
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((undecided == null) ? 0 : undecided.hashCode());
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
		return Objects.equals(undecided, other.undecided()) && 
				other.satisfied().isEmpty() && 
				other.unsatisfied().isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Iterator<Argument> iterator = undecided.iterator();
		builder.append("u(");
		builder.append(iterator.next());
		builder.append(")");
		while (iterator.hasNext()) {
			builder.append(" u(");
			builder.append(iterator.next());
			builder.append(")");
		}
		return builder.toString();
	}
}
