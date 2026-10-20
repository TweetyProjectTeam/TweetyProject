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
package org.tweetyproject.arg.dung.serialisability;

import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.serialisability.SerialisationScheme;

/**
 * Serialisation scheme for abstract argumentation frameworks whose models are
 * extensions, ordered by set inclusion. The base notion, the reduct and the
 * classification of initial sets are given by an {@link AfSerialisationBasis},
 * by default {@link AfSerialisationBasis#ADMISSIBLE}.
 *
 * @see "Matthias Thimm. 'Revisiting initial sets in abstract argumentation' Argument &amp; Computation (2022)"
 *
 * @author Lars Bengel
 */
public class AfExtensionScheme implements SerialisationScheme<DungTheory, Extension<DungTheory>> {
	/** the basis of this scheme */
	private final AfSerialisationBasis basis;

	/**
	 * Creates the scheme for admissibility
	 */
	public AfExtensionScheme() {
		this(AfSerialisationBasis.ADMISSIBLE);
	}

	/**
	 * Creates the scheme for the given basis
	 * @param basis some serialisation basis
	 */
	public AfExtensionScheme(AfSerialisationBasis basis) {
		this.basis = basis;
	}

	@Override
	public Extension<DungTheory> empty(DungTheory af) {
		return new Extension<>();
	}

	@Override
	public Collection<Extension<DungTheory>> getBaseModels(DungTheory af) {
		return this.basis.getModels(af);
	}

	@Override
	public boolean isUnattacked(DungTheory af, Extension<DungTheory> initialSet) {
		return this.basis.isUnattacked(af, initialSet);
	}

	@Override
	public boolean isConflicting(DungTheory af, Extension<DungTheory> initialSet, Extension<DungTheory> other) {
		return this.basis.isConflicting(af, initialSet, other);
	}

	@Override
	public DungTheory getReduct(DungTheory af, Extension<DungTheory> ext) {
		return this.basis.getReduct(af, ext);
	}

	@Override
	public Extension<DungTheory> union(Extension<DungTheory> ext, Extension<DungTheory> other) {
		Extension<DungTheory> result = new Extension<>(ext);
		result.addAll(other);
		return result;
	}

	@Override
	public boolean isLessOrEqual(Extension<DungTheory> ext, Extension<DungTheory> other) {
		return other.containsAll(ext);
	}
}
