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

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.Link;
import org.tweetyproject.arg.adf.semantics.link.LinkType;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.pl.Clause;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
 * @author Mathias Hofer
 *
 */
public final class RelativeBipolarSatEncoding implements SatEncoding {
	
	private final Interpretation interpretation;
	
	private final Link link;
	
	private final PropositionalMapping mapping;
		
	/**
	 * @param interpretation the interpretation which makes the link bipolar
	 * @param link the bipolarized link
	 */
	/**
	 * 
	 * @param interpretation interpretation
	 * @param link link
	 * @param mapping mapping
	 */
	public RelativeBipolarSatEncoding(Interpretation interpretation, Link link, PropositionalMapping mapping) {
		this.interpretation = Objects.requireNonNull(interpretation);
		this.link = Objects.requireNonNull(link);
		this.mapping = Objects.requireNonNull(mapping);
	}

	public void encode(Consumer<Clause> consumer) {
		Argument parent = link.getFrom();
		Argument child = link.getTo();

		// the relative part makes sure that the clauses are only activated if the decided arguments match
		// in other words, the clauses are always satisfiable until the current partial interpretation makes the link bipolar
		Collection<Literal> relativePart = relativePart(interpretation, mapping);
		if (link.getType() == LinkType.ATTACKING) {
			consumer.accept(Clause.of(relativePart, mapping.getTrue(child).neg(), mapping.getFalse(parent), mapping.getLink(link)));
			consumer.accept(Clause.of(relativePart, mapping.getFalse(child).neg(), mapping.getTrue(parent), mapping.getLink(link).neg()));
		} else if (link.getType() == LinkType.SUPPORTING) {
			consumer.accept(Clause.of(relativePart, mapping.getTrue(child).neg(), mapping.getTrue(parent), mapping.getLink(link).neg()));
			consumer.accept(Clause.of(relativePart, mapping.getFalse(child).neg(), mapping.getFalse(parent), mapping.getLink(link)));
		}
	}
	
	private static Collection<Literal> relativePart(Interpretation interpretation, PropositionalMapping mapping) {
		Collection<Literal> literals = new LinkedList<>();
		for (Argument a : interpretation.unsatisfied()) {
			literals.add(mapping.getFalse(a).neg());
		}
		for (Argument a : interpretation.satisfied()) {
			literals.add(mapping.getTrue(a).neg());
		}
		return literals;
	}
	
}
