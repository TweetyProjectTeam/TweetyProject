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

import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.adf.semantics.Link;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.util.Cache;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;
import net.sf.tweety.logics.pl.syntax.Proposition;

/**
 * Stores the shared context which is needed to interconnect the sat encodings,
 * e.g. shared propositional variables.
 * 
 * @author Mathias Hofer
 *
 */
public class SatEncodingContext {

	private Map<Argument, Proposition> falses;

	private Map<Argument, Proposition> trues;

	private Cache<Link, Proposition> links = new Cache<Link, Proposition>(
			l -> new Proposition("p_" + l.getFrom().getName() + "_" + l.getTo().getName()));

	private AbstractDialecticalFramework adf;

	/**
	 * @param adf
	 */
	public SatEncodingContext(AbstractDialecticalFramework adf) {
		this.adf = adf;
		this.falses = new HashMap<Argument, Proposition>();
		this.trues = new HashMap<Argument, Proposition>();
		for (Argument a : adf.getArguments()) {
			this.falses.put(a, new Proposition(a.getName() + "_f"));
			this.trues.put(a, new Proposition(a.getName() + "_t"));
		}
	}
		
	public Proposition getFalseRepresentation(Argument argument) {
		if (!falses.containsKey(argument)) {
			throw new IllegalArgumentException("The given argument is unknown to this context.");
		}

		return falses.get(argument);
	}

	public Proposition getTrueRepresentation(Argument argument) {
		if (!trues.containsKey(argument)) {
			throw new IllegalArgumentException("The given argument is unknown to this context.");
		}

		return trues.get(argument);
	}

	public Proposition getLinkRepresentation(Argument from, Argument to) {
		Link link = adf.link(from, to);
		if (link == null) {
			throw new IllegalArgumentException("The given link is unknown to this context.");
		}
		return links.apply(link);
	}

	public Proposition getLinkRepresentation(Link link) {
		return links.apply(link);
	}

	/**
	 * @return the adf
	 */
	public AbstractDialecticalFramework getAbstractDialecticalFramework() {
		return adf;
	}

	/**
	 * Constructs an ADF interpretation from the given SAT witness.
	 * 
	 * @param witness
	 *            the SAT witness
	 * @return the constructed ADF interpretation
	 */
	public Interpretation interpretationFromWitness(
			net.sf.tweety.commons.Interpretation<PlBeliefSet, PlFormula> witness) {
		if (witness == null) {
			return null;
		}
		
		Map<Argument, Boolean> assignment = new HashMap<Argument, Boolean>();
		for (Argument a : adf.getArguments()) {
			if (witness.satisfies(trues.get(a))) {
				assignment.put(a, true);
			} else if (witness.satisfies(falses.get(a))) {
				assignment.put(a, false);
			} else {
				assignment.put(a, null);
			}
		}
		return Interpretation.fromMap(assignment);
	}
}
