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
package org.tweetyproject.arg.adf.reasoner.sat.encodings;

import java.util.Objects;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public class RestrictedBipolarSatEncoding implements SatEncoding {

	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;
	
	private final Interpretation partial;
	
	/**
	 * @param adf adf
	 * @param mapping mapping
	 */
	public RestrictedBipolarSatEncoding(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
		this.partial = Objects.requireNonNull(partial);
	}
	
	@Override
	public void encode(Consumer<Clause> consumer) {
		for (Argument to : adf.getArguments()) {
			if (!partial.undecided(to)) {
				for (Link link : adf.linksTo(to)) {
					if (link.getType() == LinkType.ATTACKING) {
						encodeAttacking(consumer, to, link);
					} 
					if (link.getType() == LinkType.SUPPORTING) {
						encodeSupporting(consumer, to, link);
					}
				}
			}
		}
	}
	
	private void encodeAttacking(Consumer<Clause> consumer, Argument to, Link l) {
		Literal link = mapping.getLink(l);
		if (!partial.unsatisfied(to)) {
			consumer.accept(Clause.of(mapping.getTrue(to).neg(), mapping.getFalse(l.getFrom()), link));			
		}
		if (!partial.satisfied(to)) {
			consumer.accept(Clause.of(mapping.getFalse(to).neg(), mapping.getTrue(l.getFrom()), link.neg()));			
		}
	}
	
	private void encodeSupporting(Consumer<Clause> consumer, Argument to, Link l) {
		Literal link = mapping.getLink(l);
		if (!partial.unsatisfied(to)) {
			consumer.accept(Clause.of(mapping.getTrue(to).neg(), mapping.getTrue(l.getFrom()), link.neg()));			
		}
		if (!partial.satisfied(to)) {
			consumer.accept(Clause.of(mapping.getFalse(to).neg(), mapping.getFalse(l.getFrom()), link));			
		}
	}

}
