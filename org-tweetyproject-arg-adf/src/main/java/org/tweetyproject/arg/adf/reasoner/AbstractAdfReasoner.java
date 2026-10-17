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
package org.tweetyproject.arg.adf.reasoner;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.tweetyproject.arg.adf.reasoner.simple.SimpleAdmissibleAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleCompleteAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleConflictFreeAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleGroundedAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleNaiveAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimplePreferredAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleStableAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ModelProvider;
import org.tweetyproject.commons.ModelVerifier;
import org.tweetyproject.commons.QualitativeReasoner;

/**
 * Common base of all ADF reasoners. A reasoner computes the models of an ADF
 * under one semantics as {@link ThreeValuedInterpretation}s and answers
 * acceptance and verification queries.
 * <p>
 * Every reasoner implements {@link #isModel(AbstractDialecticalFramework, ThreeValuedInterpretation)}
 * directly. A reasoner whose models are computed from that check alone (generate
 * and test) needs nothing else, see {@code AbstractSimpleAdfReasoner}; others
 * also override {@link #getModels(AbstractDialecticalFramework)}. Implementations
 * of {@code isModel} must not rely on {@code getModels} of the same reasoner if
 * that one is based on {@code isModel}.
 * <p>
 * As in {@code AbstractExtensionReasoner} of the Dung module, the default
 * implementations of the query methods are based on
 * {@link #getModels(AbstractDialecticalFramework)}; subclasses may override them
 * with direct procedures. An argument is accepted skeptically if every model
 * maps it to t (vacuously true without models) and credulously if some model
 * does.
 *
 * @author Lars Bengel
 */
public abstract class AbstractAdfReasoner implements QualitativeReasoner<AbstractDialecticalFramework, Argument>,
		ModelProvider<Argument, AbstractDialecticalFramework, ThreeValuedInterpretation>,
		ModelVerifier<Argument, AbstractDialecticalFramework, ThreeValuedInterpretation> {

	/**
	 * Decides whether the given interpretation is a model of the ADF (the
	 * verification problem). Verification is usually much easier than
	 * enumeration: polynomial for two- and three-valued models, one Γ check per
	 * argument for admissible and complete interpretations.
	 *
	 * @param adf an ADF
	 * @param v   an interpretation over the arguments of the ADF
	 * @return true if v is a model
	 * @throws IllegalArgumentException if the domain of v differs from the arguments of the ADF
	 */
	@Override
	public abstract boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v);

	/**
	 * @param adf      an ADF
	 * @param argument an argument of the ADF
	 * @return true if the argument is skeptically accepted
	 */
	@Override
	public Boolean query(AbstractDialecticalFramework adf, Argument argument) {
		return query(adf, argument, InferenceMode.SKEPTICAL);
	}

	/**
	 * @param adf      an ADF
	 * @param argument an argument of the ADF
	 * @param mode     credulous or skeptical
	 * @return true if the argument is accepted under the given mode
	 * @throws IllegalArgumentException if the argument is not part of the ADF
	 */
	public Boolean query(AbstractDialecticalFramework adf, Argument argument, InferenceMode mode) {
		requireArgument(adf, argument);
		boolean credulous = mode == InferenceMode.CREDULOUS;
		for (ThreeValuedInterpretation model : getModels(adf)) {
			if (model.satisfies(argument) == credulous) {
				return credulous;
			}
		}
		return !credulous;
	}

	/**
	 * @param adf  an ADF
	 * @param mode credulous or skeptical
	 * @return all arguments accepted under the given mode
	 */
	public Collection<Argument> queryAll(AbstractDialecticalFramework adf, InferenceMode mode) {
		boolean credulous = mode == InferenceMode.CREDULOUS;
		Set<Argument> result = new LinkedHashSet<>(credulous ? Set.of() : adf.getNodes());
		for (ThreeValuedInterpretation model : getModels(adf)) {
			if (credulous) {
				result.addAll(model.satisfied());
			} else {
				result.retainAll(model.satisfied());
			}
		}
		return result;
	}

	@Override
	public boolean isInstalled() {
		return true;
	}

	/**
	 * Returns the brute-force reference reasoner for one of the semantics CF, NA,
	 * ADM, CO, GR, PR and ST. Two- and three-valued models have no constant in
	 * {@link Semantics}; use {@code SimpleTwoValuedModelAdfReasoner} and
	 * {@code SimpleThreeValuedModelAdfReasoner} directly.
	 *
	 * @param semantics a semantics
	 * @return the simple reasoner for it
	 * @throws IllegalArgumentException if the semantics is not supported for ADFs
	 */
	public static AbstractAdfReasoner getSimpleReasonerForSemantics(Semantics semantics) {
		switch (semantics) {
			case CF:
				return new SimpleConflictFreeAdfReasoner();
			case NA:
				return new SimpleNaiveAdfReasoner();
			case ADM:
				return new SimpleAdmissibleAdfReasoner();
			case CO:
				return new SimpleCompleteAdfReasoner();
			case GR:
				return new SimpleGroundedAdfReasoner();
			case PR:
				return new SimplePreferredAdfReasoner();
			case ST:
				return new SimpleStableAdfReasoner();
			default:
				throw new IllegalArgumentException("Unsupported semantics for ADFs: " + semantics);
		}
	}

	/**
	 * @param adf an ADF
	 * @param v   an interpretation
	 * @throws IllegalArgumentException if the domain of v differs from the arguments of the ADF
	 */
	protected static void requireDomain(AbstractDialecticalFramework adf, ThreeValuedInterpretation v) {
		if (!v.getDomain().equals(adf.getNodes())) {
			throw new IllegalArgumentException("The interpretation is not defined over the arguments of the ADF.");
		}
	}

	/**
	 * @param adf      an ADF
	 * @param argument an argument
	 * @throws IllegalArgumentException if the argument is not part of the ADF
	 */
	protected static void requireArgument(AbstractDialecticalFramework adf, Argument argument) {
		if (!adf.contains(argument)) {
			throw new IllegalArgumentException("Not an argument of the ADF: " + argument);
		}
	}

}
