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
package org.tweetyproject.arg.adf.reasoner.sat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.semantics.TruthValue;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.util.IncreasingSubsetIterator;
import org.tweetyproject.commons.util.SubsetIterator;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Enumerates the strongly admissible interpretations (SAD) of an ADF: v is
 * strongly admissible iff iterating Γ_{D,v}(w) = Γ_D(w) ⊓ v from the
 * interpretation that maps every argument to u reaches v. Γ_D is evaluated with
 * one incremental SAT solver, and only for the arguments decided by v but not
 * yet by w.
 *
 * @see "Atefeh Keshavarzi Zafarghandi, Rineke Verbrugge, Bart Verheij. 'Strong Admissibility for Abstract Dialectical Frameworks.' SAC 2021."
 * @see "Atefeh Keshavarzi Zafarghandi, Wolfgang Dvořák, Rineke Verbrugge, Bart Verheij. 'Computational Complexity of Strong Admissibility for Abstract Dialectical Frameworks.' NMR 2021."
 *
 * @author Lars Bengel
 */
public class SatStronglyAdmissibleAdfReasoner extends AbstractSatAdfReasoner {

	/**
	 * Creates the reasoner with the {@link Sat4jIncrementalSolver}.
	 */
	public SatStronglyAdmissibleAdfReasoner() {
		this(Sat4jIncrementalSolver::new);
	}

	/**
	 * @param solvers creates a fresh solver for every call
	 */
	public SatStronglyAdmissibleAdfReasoner(Supplier<SatSolver> solvers) {
		super(solvers);
	}

	@Override
	public Collection<ThreeValuedInterpretation> getModels(AbstractDialecticalFramework adf) {
		ThreeValuedInterpretation grd = new SatGroundedAdfReasoner(solvers).getModel(adf);
		Set<Argument> decided = new HashSet<>(grd.satisfied());
		decided.addAll(grd.unsatisfied());
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		try (SatCharacteristicOperator gamma = new SatCharacteristicOperator(adf, solvers)) {
			for (SubsetIterator<Argument> it = new IncreasingSubsetIterator<>(decided); it.hasNext();) {
				checkInterrupted();
				Set<Argument> kept = it.next();
				ThreeValuedInterpretation v = grd;
				for (Argument a : decided) {
					if (!kept.contains(a)) {
						v = v.with(a, TruthValue.UNDECIDED);
					}
				}
				if (isStronglyAdmissible(adf, gamma, v)) {
					models.add(v);
				}
			}
		}
		return models;
	}

	@Override
	public boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		requireDomain(adf, v);
		try (SatCharacteristicOperator gamma = new SatCharacteristicOperator(adf, solvers)) {
			return isStronglyAdmissible(adf, gamma, v);
		}
	}

	/**
	 * @return true if the least fixpoint of Γ_{D,v} is v
	 */
	private static boolean isStronglyAdmissible(AbstractDialecticalFramework adf, SatCharacteristicOperator gamma,
			ThreeValuedInterpretation v) {
		ThreeValuedInterpretation w = ThreeValuedInterpretation.allUndecided(adf.getNodes());
		List<Argument> open = new ArrayList<>(v.satisfied());
		open.addAll(v.unsatisfied());
		boolean changed = true;
		while (changed) {
			checkInterrupted();
			changed = false;
			ThreeValuedInterpretation current = w;
			for (Iterator<Argument> it = open.iterator(); it.hasNext();) {
				Argument a = it.next();
				if (gamma.apply(current, a) == v.get(a)) {
					w = w.with(a, v.get(a));
					it.remove();
					changed = true;
				}
			}
		}
		return open.isEmpty();
	}

}
