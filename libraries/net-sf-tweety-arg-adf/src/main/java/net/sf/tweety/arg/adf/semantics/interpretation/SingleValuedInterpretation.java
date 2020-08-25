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

import java.util.Set;

import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.util.MinusSetView;
import net.sf.tweety.arg.adf.util.UnionSetView;

/**
 * 
 * @author Mathias Hofer
 *
 */
final class SingleValuedInterpretation implements Interpretation {

	private final Argument argument;

	private final boolean value;

	private final Set<Argument> undecided;

	/**
	 * 
	 * @param argument the argument with the value
	 * @param value the value of the argument
	 * @param adf the contextual ADF
	 */
	public SingleValuedInterpretation(Argument argument, boolean value, AbstractDialecticalFramework adf) {
		this.argument = argument;
		this.value = value;
		this.undecided = new MinusSetView<Argument>(adf.getArguments(), Set.of(argument));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#satisfied(
	 * net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public boolean satisfied(Argument arg) {
		return value && arg.equals(argument); // implicit null-check
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#unsatisfied
	 * (net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public boolean unsatisfied(Argument arg) {
		return !value && arg.equals(argument); // implicit null-check
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#undecided(
	 * net.sf.tweety.arg.adf.syntax.Argument)
	 */
	@Override
	public boolean undecided(Argument arg) {
		return undecided.contains(arg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#satisfied()
	 */
	@Override
	public Set<Argument> satisfied() {
		return value ? Set.of(argument) : Set.of();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#unsatisfied
	 * ()
	 */
	@Override
	public Set<Argument> unsatisfied() {
		return value ? Set.of() : Set.of(argument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#undecided()
	 */
	@Override
	public Set<Argument> undecided() {
		return undecided;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#numDecided()
	 */
	@Override
	public int numDecided() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.semantics.interpretation.Interpretation#arguments()
	 */
	@Override
	public Set<Argument> arguments() {
		return new UnionSetView<Argument>(Set.of(argument), undecided);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (value) {
			builder.append("t(");
		} else {
			builder.append("f(");
		}
		builder.append(argument).append(")");
		for (Argument u : undecided) {
			builder.append(" u(").append(u).append(")");
		}
		return builder.toString();
	}

}
