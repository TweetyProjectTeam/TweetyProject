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
package net.sf.tweety.arg.adf.reasoner.encodings;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.transform.DefinitionalCNFTransform;
import net.sf.tweety.commons.util.DefaultSubsetIterator;
import net.sf.tweety.commons.util.SubsetIterator;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * @author Mathias Hofer
 *
 */
public class KBipolarSatEncoding implements SatEncoding {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.sf.tweety.arg.adf.reasoner.strategy.sat.SatEncoding#encode(net.sf.tweety.arg.
	 * adf.reasoner.sat.SatEncodingContext)
	 */
	@Override
	public Collection<Disjunction> encode(SatEncodingContext context, Interpretation interpretation) {
		AbstractDialecticalFramework adf = context.getAbstractDialecticalFramework();
		
		Collection<Disjunction> encoding = new LinkedList<Disjunction>();

		// use these propositions as a substitution for the special formulas
		// Tautology and Contradiction
		final Proposition TAUT = new Proposition("T");
		final Proposition CONT = new Proposition("F");

		// fix values for TAUT and CONT
		Disjunction tautClause = new Disjunction();
		tautClause.add(TAUT);
		Disjunction contClause = new Disjunction();
		contClause.add(new Negation(CONT));
		encoding.add(tautClause);
		encoding.add(contClause);

		for (Argument s : adf) {
			// set of arguments r with (r,s) non-bipolar and I(r) = u
			Set<Argument> xs = adf.linksFromParents(s).filter(Link::isDependent).map(Link::getFrom)
					.filter(interpretation::isUndecided).collect(Collectors.toSet());

			SubsetIterator<Argument> subsetIterator = new DefaultSubsetIterator<Argument>(xs);

			while (subsetIterator.hasNext()) {
				Set<Argument> subset = subsetIterator.next();

				DefinitionalCNFTransform transform = new DefinitionalCNFTransform(
						r -> !xs.contains(r) ? context.getLinkRepresentation(r, s) : (subset.contains(r) ? TAUT : CONT));
				AcceptanceCondition acc = adf.getAcceptanceCondition(s);
				Proposition accName = acc.collect(transform, Collection::add, encoding);

				// first implication
				Disjunction clause1 = new Disjunction();
				clause1.add(new Negation(context.getTrueRepresentation(s)));
				clause1.add(accName);

				// second implication
				Disjunction clause2 = new Disjunction();
				clause2.add(new Negation(context.getFalseRepresentation(s)));
				clause2.add(new Negation(accName));

				// stuff for both implications
				subset.stream().map(context::getFalseRepresentation).forEach(rFalse -> {
					clause1.add(rFalse);
					clause2.add(rFalse);
				});
				xs.stream().filter(r -> !subset.contains(r)).map(context::getTrueRepresentation).forEach(rTrue -> {
					clause1.add(rTrue);
					clause2.add(rTrue);
				});

				encoding.add(clause1);
				encoding.add(clause2);
			}
		}

		return encoding;
	}

}
