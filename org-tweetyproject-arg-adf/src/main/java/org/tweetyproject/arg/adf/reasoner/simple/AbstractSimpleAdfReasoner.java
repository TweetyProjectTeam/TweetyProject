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
import java.util.List;
import java.util.concurrent.CancellationException;

import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.semantics.CompiledAdf;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * Base class of the brute-force reasoners. They enumerate all 3^n (or 2^n for
 * two-valued semantics) interpretations of a compiled snapshot of the ADF and
 * check the definition of the semantics for each.
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
		CompiledAdf compiled = CompiledAdf.of(adf);
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		for (byte[] v : models(compiled)) {
			models.add(compiled.decode(v));
		}
		return models;
	}

	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		Collection<ThreeValuedInterpretation> models = getModels(adf);
		return models.isEmpty() ? null : models.iterator().next();
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		CompiledAdf compiled = CompiledAdf.of(adf);
		return verify(compiled, compiled.encode(v));
	}

	/**
	 * @param adf a compiled ADF
	 * @return all its models in array representation
	 */
	protected abstract List<byte[]> models(CompiledAdf adf);

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
	 * @return all three-valued interpretations that pass the check
	 */
	protected static List<byte[]> threeValued(CompiledAdf adf, Check check) {
		return enumerate(adf, check, new byte[] { CompiledAdf.U, CompiledAdf.T, CompiledAdf.F });
	}

	/**
	 * @param adf   a compiled ADF
	 * @param check the definition of the semantics
	 * @return all two-valued interpretations that pass the check
	 */
	protected static List<byte[]> twoValued(CompiledAdf adf, Check check) {
		return enumerate(adf, check, new byte[] { CompiledAdf.T, CompiledAdf.F });
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

	/** checks every assignment of the given values to the arguments, counting like an odometer */
	private static List<byte[]> enumerate(CompiledAdf adf, Check check, byte[] values) {
		List<byte[]> models = new ArrayList<>();
		byte[] v = new byte[adf.size()];
		int[] digits = new int[adf.size()];
		java.util.Arrays.fill(v, values[0]);
		for (long step = 1;; step++) {
			if (step % INTERRUPT_CHECK_INTERVAL == 0 && Thread.currentThread().isInterrupted()) {
				throw new CancellationException("Enumeration interrupted.");
			}
			if (check.test(adf, v)) {
				models.add(v.clone());
			}
			int i = 0;
			while (i < digits.length && digits[i] == values.length - 1) {
				digits[i] = 0;
				v[i] = values[0];
				i++;
			}
			if (i == digits.length) {
				return models;
			}
			v[i] = values[++digits[i]];
		}
	}

}
