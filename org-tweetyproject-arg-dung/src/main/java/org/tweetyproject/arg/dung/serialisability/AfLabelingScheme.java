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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Labeling;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.serialisability.SerialisationScheme;

/**
 * Serialisation scheme for abstract argumentation frameworks whose models are
 * partial labelings, i.e. labelings that only record IN and OUT decisions;
 * unrecorded arguments are undecided. Labelings are ordered by inclusion of
 * their recorded decisions. A model of the base notion is turned into a
 * labeling by {@link #toLabeling(DungTheory, Extension)}; the base notion,
 * reduct and classification are given by an {@link AfSerialisationBasis} and
 * applied to the IN part.
 * <p>
 * {@link #toLabeling(DungTheory, Extension)} must label exactly the arguments
 * that the reduct of the basis removes. The default (IN = E, OUT = E⁺) fits
 * reducts removing E ∪ E⁺, such as that of {@link AfSerialisationBasis#ADMISSIBLE};
 * for other reducts it must be overridden. With admissibility, the base models
 * are the images of the admissible extensions under this mapping.
 *
 * @author Lars Bengel
 */
public class AfLabelingScheme implements SerialisationScheme<DungTheory, Labeling> {
	/** the basis of this scheme */
	private final AfSerialisationBasis basis;

	/**
	 * Creates the scheme for admissibility
	 */
	public AfLabelingScheme() {
		this(AfSerialisationBasis.ADMISSIBLE);
	}

	/**
	 * Creates the scheme for the given basis
	 * @param basis some serialisation basis
	 */
	public AfLabelingScheme(AfSerialisationBasis basis) {
		this.basis = basis;
	}

	/**
	 * Turns a model of the base notion into a partial labeling: IN = E, OUT = E⁺
	 * @param af  some argumentation framework
	 * @param ext a model of the base notion
	 * @return the partial labeling of the extension
	 */
	protected Labeling toLabeling(DungTheory af, Extension<DungTheory> ext) {
		Labeling result = new Labeling();
		for (Argument a : ext)
			result.put(a, ArgumentStatus.IN);
		for (Argument a : af.getAttacked(ext))
			if (!ext.contains(a))
				result.put(a, ArgumentStatus.OUT);
		return result;
	}

	/**
	 * Completes a partial labeling by labeling all unrecorded arguments as undecided
	 * @param af      some argumentation framework
	 * @param labeling a partial labeling of the framework
	 * @return the total labeling
	 * @throws IllegalArgumentException if the labeling records arguments not in the framework
	 */
	public static Labeling toTotalLabeling(DungTheory af, Labeling labeling) {
		if (!af.containsAll(labeling.keySet()))
			throw new IllegalArgumentException("The labeling records arguments that are not in the framework.");
		Labeling result = new Labeling();
		result.putAll(labeling);
		for (Argument a : af)
			if (!result.containsKey(a))
				result.put(a, ArgumentStatus.UNDECIDED);
		return result;
	}

	/**
	 * @param labeling some labeling
	 * @return the arguments labeled IN
	 */
	private static Extension<DungTheory> in(Labeling labeling) {
		return new Extension<>(labeling.getArgumentsOfStatus(ArgumentStatus.IN));
	}

	@Override
	public Labeling empty(DungTheory af) {
		return new Labeling();
	}

	@Override
	public Collection<Labeling> getBaseModels(DungTheory af) {
		List<Labeling> result = new ArrayList<>();
		for (Extension<DungTheory> ext : this.basis.getModels(af))
			result.add(this.toLabeling(af, ext));
		return result;
	}

	@Override
	public boolean isUnattacked(DungTheory af, Labeling initialModel) {
		return this.basis.isUnattacked(af, in(initialModel));
	}

	@Override
	public boolean isConflicting(DungTheory af, Labeling initialModel, Labeling other) {
		return this.basis.isConflicting(af, in(initialModel), in(other));
	}

	@Override
	public DungTheory getReduct(DungTheory af, Labeling labeling) {
		return this.basis.getReduct(af, in(labeling));
	}

	/**
	 * Returns the union of the recorded decisions of both labelings
	 * @throws IllegalArgumentException if an argument is labeled IN in one and OUT
	 *                                  in the other labeling, or if a labeling
	 *                                  records UNDECIDED
	 */
	@Override
	public Labeling union(Labeling labeling, Labeling other) {
		Labeling result = new Labeling();
		for (Labeling l : List.of(labeling, other)) {
			for (Map.Entry<Argument, ArgumentStatus> entry : l.entrySet()) {
				if (entry.getValue() == ArgumentStatus.UNDECIDED)
					throw new IllegalArgumentException("Partial labelings do not record undecided arguments: " + entry.getKey());
				ArgumentStatus previous = result.put(entry.getKey(), entry.getValue());
				if (previous != null && previous != entry.getValue())
					throw new IllegalArgumentException("Conflicting decisions for " + entry.getKey());
			}
		}
		return result;
	}

	@Override
	public boolean isLessOrEqual(Labeling labeling, Labeling other) {
		for (Map.Entry<Argument, ArgumentStatus> entry : labeling.entrySet())
			if (entry.getValue() != other.get(entry.getKey()))
				return false;
		return true;
	}
}
