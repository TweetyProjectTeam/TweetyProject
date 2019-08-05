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
package net.sf.tweety.arg.adf.syntax;

import java.util.function.Consumer;
import java.util.stream.Stream;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.graphs.Node;

/**
 * An immutable representation of an ADF argument
 * 
 * @author Mathias Hofer
 *
 */
public class Argument extends AcceptanceCondition implements Formula, Node {

	private String name;
	
	/**
	 * @param name
	 */
	public Argument(String name) {
		super();
		this.name = name;
	}

	@Override
	public Stream<Argument> arguments() {
		return Stream.of(this);
	}

	@Override
	public Signature getSignature() {
		return new AbstractDialecticalFrameworkSignature(this);
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.syntax.AcceptanceCondition#transform(net.sf.tweety.arg.adf.syntax.Transform, java.util.function.Consumer)
	 */
	@Override
	protected <C, R> R transform(Transform<C, R> transform, Consumer<C> consumer, int polarity) {
		return transform.transformArgument(consumer, this, polarity);
	}
}
