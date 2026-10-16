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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.ModelIterator;
import org.tweetyproject.arg.adf.semantics.CompiledAdf;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * Base class of the brute-force reasoners. They enumerate all 3^n (or 2^n for
 * two-valued semantics) interpretations of a compiled snapshot of the ADF and
 * check the definition of the semantics for each, so they are meant for small
 * ADFs (up to about 12 arguments) and serve as reference implementations and
 * test oracles. Enumeration is lazy and needs constant memory, except for
 * semantics defined by maximality.
 * <p>
 * The enumeration stops with a {@link CancellationException} if the current
 * thread is interrupted.
 *
 * @author Lars Bengel
 */
public abstract class AbstractSimpleAdfReasoner extends AbstractAdfReasoner {

	private static final int INTERRUPT_CHECK_INTERVAL = 1 << 12;

	@Override
	public ModelIterator modelIterator(AbstractDialecticalFramework adf) {
		return models(CompiledAdf.of(adf));
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		CompiledAdf compiled = CompiledAdf.of(adf);
		return verify(compiled, compiled.encode(v));
	}

	/**
	 * @param adf a compiled ADF
	 * @return an iterator over its models
	 */
	protected abstract ModelIterator models(CompiledAdf adf);

	/**
	 * @param adf a compiled ADF
	 * @param v   an interpretation in array representation; may be modified
	 *            temporarily but must be restored
	 * @return true if v is a model
	 */
	protected abstract boolean verify(CompiledAdf adf, byte[] v);

	/** A test of a candidate interpretation; it may modify v temporarily but must restore it. */
	protected interface Check {
		/**
		 * @param adf a compiled ADF
		 * @param v   a candidate interpretation
		 * @return true if the candidate is a model
		 */
		boolean test(CompiledAdf adf, byte[] v);
	}

	/**
	 * @param adf   a compiled ADF
	 * @param check the definition of the semantics
	 * @return a lazy iterator over all three-valued interpretations that pass the check
	 */
	protected static ModelIterator threeValued(CompiledAdf adf, Check check) {
		return new Enumeration(adf, check, new byte[] { CompiledAdf.U, CompiledAdf.T, CompiledAdf.F });
	}

	/**
	 * @param adf   a compiled ADF
	 * @param check the definition of the semantics
	 * @return a lazy iterator over all two-valued interpretations that pass the check
	 */
	protected static ModelIterator twoValued(CompiledAdf adf, Check check) {
		return new Enumeration(adf, check, new byte[] { CompiledAdf.T, CompiledAdf.F });
	}

	/**
	 * @param adf    a compiled ADF
	 * @param models some interpretations in array representation
	 * @return an iterator over them
	 */
	protected static ModelIterator of(CompiledAdf adf, List<byte[]> models) {
		Iterator<byte[]> it = models.iterator();
		return ModelIterator.of(new Iterator<>() {
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public ThreeValuedInterpretation next() {
				return adf.decode(it.next());
			}
		});
	}

	/**
	 * @param a an interpretation
	 * @param b an interpretation over the same arguments
	 * @return true if b ≤_i a, i.e. a agrees with every decided value of b
	 */
	protected static boolean isAtLeastAsInformativeAs(byte[] a, byte[] b) {
		for (int i = 0; i < a.length; i++) {
			if (b[i] != CompiledAdf.U && b[i] != a[i]) {
				return false;
			}
		}
		return true;
	}

	/** odometer over all assignments of the given values to the arguments */
	private static final class Enumeration implements ModelIterator {
		private final CompiledAdf adf;
		private final Check check;
		private final byte[] values;
		private final byte[] v;
		/** digit (index into values) per argument */
		private final int[] digits;
		private boolean exhausted;
		private boolean found;
		private long steps;

		Enumeration(CompiledAdf adf, Check check, byte[] values) {
			this.adf = adf;
			this.check = check;
			this.values = values;
			this.v = new byte[adf.size()];
			this.digits = new int[adf.size()];
			java.util.Arrays.fill(v, values[0]);
		}

		@Override
		public boolean hasNext() {
			while (!found && !exhausted) {
				if (++steps % INTERRUPT_CHECK_INTERVAL == 0 && Thread.currentThread().isInterrupted()) {
					close();
					throw new CancellationException("Enumeration interrupted.");
				}
				if (check.test(adf, v)) {
					found = true;
				} else {
					advance();
				}
			}
			return found;
		}

		@Override
		public ThreeValuedInterpretation next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			ThreeValuedInterpretation model = adf.decode(v);
			found = false;
			advance();
			return model;
		}

		private void advance() {
			int i = 0;
			while (i < digits.length && digits[i] == values.length - 1) {
				digits[i] = 0;
				v[i] = values[0];
				i++;
			}
			if (i == digits.length) {
				exhausted = true;
			} else {
				v[i] = values[++digits[i]];
			}
		}

		@Override
		public void close() {
			exhausted = true;
			found = false;
		}
	}

}
