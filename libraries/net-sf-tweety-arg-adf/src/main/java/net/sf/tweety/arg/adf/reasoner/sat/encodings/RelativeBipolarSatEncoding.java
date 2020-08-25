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
package net.sf.tweety.arg.adf.reasoner.sat.encodings;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.semantics.link.Link;
import net.sf.tweety.arg.adf.semantics.link.LinkType;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.pl.Clause;
import net.sf.tweety.arg.adf.syntax.pl.Literal;
import net.sf.tweety.arg.adf.syntax.pl.Negation;

/**
 * @author Mathias Hofer
 *
 */
public final class RelativeBipolarSatEncoding implements SatEncoding {
	
	private final Interpretation interpretation;
	
	private final Link link;
		
	/**
	 * @param interpretation the interpretation which makes the link bipolar
	 * @param link the bipolarized link
	 */
	public RelativeBipolarSatEncoding(Interpretation interpretation, Link link) {
		this.interpretation = Objects.requireNonNull(interpretation);
		this.link = Objects.requireNonNull(link);
	}

	public void encode(Consumer<Clause> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		Argument parent = link.getFrom();
		Argument child = link.getTo();
		
		// the relative part makes sure that the clauses are only activated if the decided arguments match
		// in other words, the clauses are always satisfiable until the current partial interpretation makes the link bipolar
		Collection<Literal> relativePart = relativePart(interpretation, mapping);
		if (link.getType() == LinkType.ATTACKING) {
			consumer.accept(Clause.of(relativePart, new Negation(mapping.getTrue(child)), mapping.getFalse(parent), mapping.getLink(link)));
			consumer.accept(Clause.of(relativePart, new Negation(mapping.getFalse(child)), mapping.getTrue(parent), new Negation(mapping.getLink(link))));
		} else if (link.getType() == LinkType.SUPPORTING) {
			consumer.accept(Clause.of(relativePart, new Negation(mapping.getTrue(child)), mapping.getTrue(parent), new Negation(mapping.getLink(link))));
			consumer.accept(Clause.of(relativePart, new Negation(mapping.getFalse(child)), mapping.getFalse(parent), mapping.getLink(link)));
		}
	}
	
	private static Collection<Literal> relativePart(Interpretation interpretation, PropositionalMapping mapping) {
		Collection<Literal> literals = new LinkedList<>();
		for (Argument a : interpretation.unsatisfied()) {
			literals.add(new Negation(mapping.getFalse(a)));
		}
		for (Argument a : interpretation.satisfied()) {
			literals.add(new Negation(mapping.getTrue(a)));
		}
		return literals;
	}
	
}
