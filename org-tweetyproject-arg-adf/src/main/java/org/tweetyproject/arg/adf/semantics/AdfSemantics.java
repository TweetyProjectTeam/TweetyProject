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
 *  Copyright 2026 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.semantics;

import java.util.NoSuchElementException;

/**
 * The semantics of Abstract Dialectical Frameworks. Semantics shared with
 * Dung's frameworks use the same abbreviations as
 * {@link org.tweetyproject.arg.dung.semantics.Semantics}.
 *
 * @author Lars Bengel
 */
public enum AdfSemantics {
	/** conflict-free */
	CF("conflict-free semantics", "CF"),
	/** naive */
	NA("naive semantics", "NA"),
	/** admissible */
	ADM("admissible semantics", "ADM"),
	/** complete */
	CO("complete semantics", "CO"),
	/** grounded */
	GR("grounded semantics", "GR"),
	/** preferred */
	PR("preferred semantics", "PR"),
	/** stable */
	ST("stable semantics", "ST"),
	/** semi-stable */
	SST("semi-stable semantics", "SST"),
	/** strongly admissible */
	SAD("strongly-admissible semantics", "SAD"),
	/** initial */
	IS("initial semantics", "IS"),
	/** two-valued models, called mod in the literature */
	MOD2("two-valued model semantics", "MOD2"),
	/** three-valued models */
	MOD3("three-valued model semantics", "MOD3"),
	/** semi-two-valued */
	S2V("semi-two-valued semantics", "S2V");

	private final String description;
	private final String abbreviation;

	private AdfSemantics(String description, String abbreviation) {
		this.description = description;
		this.abbreviation = abbreviation;
	}

	/**
	 * @return the description of the semantics
	 */
	public String description() {
		return this.description;
	}

	/**
	 * @return the abbreviation of the semantics
	 */
	public String abbreviation() {
		return this.abbreviation;
	}

	/**
	 * Returns the semantics with the given abbreviation.
	 *
	 * @param abbreviation an abbreviation
	 * @return the semantics with this abbreviation
	 * @throws NoSuchElementException if there is no such semantics
	 */
	public static AdfSemantics getSemantics(String abbreviation) {
		for (AdfSemantics element : AdfSemantics.values()) {
			if (element.abbreviation().equals(abbreviation)) {
				return element;
			}
		}
		throw new NoSuchElementException("No ADF semantics with abbreviation: " + abbreviation);
	}
}
