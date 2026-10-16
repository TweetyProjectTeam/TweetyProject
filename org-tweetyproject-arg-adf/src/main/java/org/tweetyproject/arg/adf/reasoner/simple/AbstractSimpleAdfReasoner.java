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
package org.tweetyproject.arg.adf.reasoner.simple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * Base class of the brute-force reasoners. By default they generate all 3^n
 * (or 2^n for two-valued semantics) interpretations of the ADF and keep those
 * for which {@link #isModel(AbstractDialecticalFramework, ThreeValuedInterpretation)}
 * holds, so a subclass only has to implement the verification of its
 * semantics. Semantics computed differently (e.g. by maximality or a fixpoint
 * iteration) override {@link #getModels(AbstractDialecticalFramework)}.
 * <p>
 * The enumeration stops with a {@link CancellationException} if the current
 * thread is interrupted.
 *
 * @author Lars Bengel
 */
public abstract class AbstractSimpleAdfReasoner extends AbstractAdfReasoner {

	private static final int INTERRUPT_CHECK_INTERVAL = 1 << 12;

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		long step = 0;
		for (Iterator<ThreeValuedInterpretation> it = candidates(adf); it.hasNext();) {
			if (++step % INTERRUPT_CHECK_INTERVAL == 0 && Thread.currentThread().isInterrupted()) {
				throw new CancellationException("Enumeration interrupted.");
			}
			ThreeValuedInterpretation v = it.next();
			if (isModel(adf, v)) {
				models.add(v);
			}
		}
		return models;
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		Collection<ThreeValuedInterpretation> models = getModels(adf);
		return models.isEmpty() ? null : models.iterator().next();
	}

	/**
	 * @return true if the models of this semantics are two-valued, so only the
	 *         2^n two-valued interpretations are candidates
	 */
	protected boolean isTwoValued() {
		return false;
	}

	/**
	 * @param adf an ADF
	 * @return all candidate interpretations over the arguments of the ADF
	 */
	private Iterator<ThreeValuedInterpretation> candidates(AbstractDialecticalFramework adf) {
		ThreeValuedInterpretation undecided = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		return isTwoValued() ? undecided.completions() : new AllInterpretations(adf.getNodes());
	}

	/** all 3^n interpretations over the given arguments, counting like an odometer */
	private static final class AllInterpretations implements Iterator<ThreeValuedInterpretation> {
		private static final TruthValue[] VALUES = { TruthValue.UNDECIDED, TruthValue.TRUE, TruthValue.FALSE };
		private final List<Argument> arguments;
		private final int[] digits;
		private boolean hasNext = true;

		AllInterpretations(Collection<Argument> arguments) {
			this.arguments = new ArrayList<>(arguments);
			this.digits = new int[arguments.size()];
		}

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public ThreeValuedInterpretation next() {
			if (!hasNext) {
				throw new NoSuchElementException();
			}
			Map<Argument, TruthValue> values = new LinkedHashMap<>();
			for (int i = 0; i < digits.length; i++) {
				values.put(arguments.get(i), VALUES[digits[i]]);
			}
			int i = 0;
			while (i < digits.length && digits[i] == VALUES.length - 1) {
				digits[i++] = 0;
			}
			if (i == digits.length) {
				hasNext = false;
			} else {
				digits[i]++;
			}
			return ThreeValuedInterpretation.of(values);
		}
	}

}
