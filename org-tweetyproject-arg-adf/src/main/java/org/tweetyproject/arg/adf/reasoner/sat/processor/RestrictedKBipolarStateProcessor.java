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
package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.Objects;
import java.util.function.Consumer;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RestrictedBipolarSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RestrictedKBipolarSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Clause;

/**
 * @author Mathias Hofer
 *
 */
public final class RestrictedKBipolarStateProcessor implements StateProcessor {
	
	private final AbstractDialecticalFramework adf;
	
	private final SatEncoding bipolar;
	
	private final SatEncoding kBipolar;
	
	/**
	 * @param adf adf
	 * @param mapping mapping
	 * @param partial Interpretation
	 */
	public RestrictedKBipolarStateProcessor(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
		this.adf = Objects.requireNonNull(adf);
		this.bipolar = new RestrictedBipolarSatEncoding(adf, mapping, partial);
		this.kBipolar = new RestrictedKBipolarSatEncoding(adf, mapping, partial);
	}

	@Override
	public void process(Consumer<Clause> consumer) {
		bipolar.encode(consumer);
		if (!adf.bipolar()) {
			kBipolar.encode(consumer);
		}
	}

}
