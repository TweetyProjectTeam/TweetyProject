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

import java.util.Objects;
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.semantics.link.Link;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;

/**
 * @author Mathias Hofer
 *
 */
public class RelativeBipolarSatEncoding implements SatEncoding {
	
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

	public void encode(Consumer<Disjunction> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		Argument parent = link.getFrom();
		Argument child = link.getTo();
		
		if (link.getType().isAttacking()) {
			Disjunction clause1 = new Disjunction();
			addRelativePart(clause1, interpretation, mapping);
			clause1.add(new Negation(mapping.getTrue(child)));
			clause1.add(mapping.getFalse(parent));
			clause1.add(mapping.getLink(link));
			consumer.accept(clause1);
			
			Disjunction clause2 = new Disjunction();
			addRelativePart(clause2, interpretation, mapping);
			clause2.add(new Negation(mapping.getFalse(child)));
			clause2.add(mapping.getTrue(parent));
			clause2.add(new Negation(mapping.getLink(link)));
			consumer.accept(clause2);
		} else if (link.getType().isSupporting()) {
			Disjunction clause1 = new Disjunction();
			addRelativePart(clause1, interpretation, mapping);
			clause1.add(new Negation(mapping.getTrue(child)));
			clause1.add(mapping.getTrue(parent));
			clause1.add(new Negation(mapping.getLink(link)));
			consumer.accept(clause1);
			
			Disjunction clause2 = new Disjunction();
			addRelativePart(clause2, interpretation, mapping);
			clause2.add(new Negation(mapping.getFalse(child)));
			clause2.add(mapping.getFalse(parent));
			clause2.add(mapping.getLink(link));
			consumer.accept(clause2);
		}
	}
	
	private static void addRelativePart(Disjunction clause, Interpretation interpretation, PropositionalMapping mapping) {
		for (Argument a : interpretation.satisfied()) {
			clause.add(new Negation(mapping.getTrue(a)));
		}
		for (Argument a : interpretation.unsatisfied()) {
			clause.add(new Negation(mapping.getFalse(a)));
		}
	}
	
}
