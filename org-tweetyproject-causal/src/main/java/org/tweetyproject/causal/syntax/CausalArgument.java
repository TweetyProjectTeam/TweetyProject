/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.causal.syntax;

import java.util.Collection;
import java.util.HashSet;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * This class represents a logical {@link Argument} induced from a {@link CausalKnowledgeBase}
 * An Argument consists of a set of assumptions (premises) and a conclusion
 *
 * @author Julian Sander
 * @author Lars Bengel
 */
public class CausalArgument extends Argument {
	/** Premises of the argument */
	private final Collection<PlFormula> premises;
	/** Conclusion of the argument */
	private final PlFormula conclusion;

	/**
	 * Initialize a new argument
	 * @param premises the set of premises of the argument
	 * @param conclusion the conclusion of the argument
	 */
	public CausalArgument(Collection<PlFormula> premises, PlFormula conclusion) {
		super(String.format("(%s -> %s)", premises.toString(), conclusion.toString()));

		this.premises = new HashSet<>(premises);
		this.conclusion = conclusion;
	}

    /**
     * Returns the conclusion of this argument.
     * 
     * @return The conclusion that can be drawn from the knowledge base given the premises.
     */
	public PlFormula getConclusion() {
		return this.conclusion;
	}

    /**
     * Returns the premises of this argument.
     * 
     * @return A set containing all the premises required for deriving the conclusion in this argument.
     */
	public Collection<PlFormula> getPremises() {
		return new HashSet<>(this.premises);
	}
}
