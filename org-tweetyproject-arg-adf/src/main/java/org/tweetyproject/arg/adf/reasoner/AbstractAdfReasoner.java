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

import org.tweetyproject.arg.adf.reasoner.sat.SatAdmissibleAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatCompleteAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatConflictFreeAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatGroundedAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatNaiveAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatPreferredAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatStableAdfReasoner;
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
 *
 * @author Lars Bengel
 */
public abstract class AbstractAdfReasoner implements QualitativeReasoner<AbstractDialecticalFramework, Argument>,
		ModelProvider<Argument, AbstractDialecticalFramework, ThreeValuedInterpretation>,
		ModelVerifier<Argument, AbstractDialecticalFramework, ThreeValuedInterpretation> {

	/**
	 * Decides whether the given interpretation is a model of the ADF.
	 *
	 * @param adf an ADF
	 * @param v   an interpretation over the arguments of the ADF
	 * @return true if v is a model
	 * @throws IllegalArgumentException if the domain of v differs from the arguments of the ADF
	 */
	@Override
	public abstract boolean isModel(AbstractDialecticalFramework adf, ThreeValuedInterpretation v);

	/**
	 * @param adf an ADF
	 * @return some model of the ADF, here the first of {@code getModels}, or
	 *         null if there is none
	 */
	@Override
	public ThreeValuedInterpretation getModel(AbstractDialecticalFramework adf) {
		Collection<ThreeValuedInterpretation> models = getModels(adf);
		return models.isEmpty() ? null : models.iterator().next();
	}

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
	 * Returns the simple reference reasoner for one of the semantics CF, NA,
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
	 * Returns the SAT-based reasoner with the {@code Sat4jIncrementalSolver} for
	 * one of the semantics CF, NA, ADM, CO, GR, PR and ST. Two-valued models have
	 * no constant in {@link Semantics}; use {@code SatTwoValuedModelAdfReasoner}
	 * directly.
	 *
	 * @param semantics a semantics
	 * @return the SAT-based reasoner for it
	 * @throws IllegalArgumentException if the semantics is not supported for ADFs
	 */
	public static AbstractAdfReasoner getSatReasonerForSemantics(Semantics semantics) {
		switch (semantics) {
			case CF:
				return new SatConflictFreeAdfReasoner();
			case NA:
				return new SatNaiveAdfReasoner();
			case ADM:
				return new SatAdmissibleAdfReasoner();
			case CO:
				return new SatCompleteAdfReasoner();
			case GR:
				return new SatGroundedAdfReasoner();
			case PR:
				return new SatPreferredAdfReasoner();
			case ST:
				return new SatStableAdfReasoner();
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
		if (!v.isDefinedOver(adf)) {
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
