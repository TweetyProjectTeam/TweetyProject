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
package org.tweetyproject.arg.aba.reasoner;

import java.util.AbstractMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.aba.syntax.InferenceRule;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.commons.Formula;

/**
 * This reasoner for flat ABA theories reduces the theory to its support-unique
 * abstract argumentation framework and evaluates it with a Dung reasoner.
 * There is one argument per support set, claiming every sentence
 * that set derives subset-minimally
 * <p>
 * Only proven to coincide for: complete, preferred, grounded and stable
 * Non-admissible semantics do not work in general. All other semantics are unclear as of now.
 *
 * @see "Lehtonen, 'Constructing Compact Structured Argumentation Frameworks', COMMA 2026"
 * @see "Lehtonen, 'ABu: Efficient Argument Builder for Assumption-Based Argumentation', SAFA 2026"
 *
 * @author Lars Bengel
 *
 * @param <T> the language of the underlying ABA theory
 */
public class AfReductionReasoner<T extends Formula> extends GeneralAbaReasoner<T> {

	/**
	 * An argument: a support set and the sentences it minimally derives
	 *
	 * @param <T> the language of the underlying ABA theory
	 */
	public static class SupportArgument<T extends Formula> {
		private final Set<Assumption<T>> support;
		private final Set<T> claims = new HashSet<>();

		private SupportArgument(Set<Assumption<T>> support) {
			this.support = Collections.unmodifiableSet(support);
		}

		/**
		 * @return the assumptions this argument is based on
		 */
		public Set<Assumption<T>> getSupport() {
			return support;
		}

		/**
		 * @return the sentences derived minimally from the support
		 */
		public Set<T> getClaims() {
			return Collections.unmodifiableSet(claims);
		}

		@Override
		public String toString() {
			return "(" + support + ", " + claims + ")";
		}
	}

	/** Semantics whose AF extensions do not map back to ABA extensions */
	private static final EnumSet<Semantics> UNSUPPORTED = EnumSet.of(Semantics.CF, Semantics.NA, Semantics.STG,
			Semantics.STG2, Semantics.CF2, Semantics.SCF2, Semantics.WAD, Semantics.WCO, Semantics.WPR, Semantics.WGR,
			Semantics.UD, Semantics.SUD, Semantics.CG);

	private final Semantics semantics;

	/**
	 * Creates a new reasoner
	 *
	 * @param semantics the Dung semantics to evaluate the framework with
	 * @throws IllegalArgumentException for unsupported semantics
	 */
	public AfReductionReasoner(Semantics semantics) {
		if (UNSUPPORTED.contains(semantics))
			throw new IllegalArgumentException("The AF reduction does not support " + semantics.description() + ".");
		this.semantics = semantics;
	}

	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		Collection<SupportArgument<T>> args = getArguments(abat);
		Map<T, Set<SupportArgument<T>>> byClaim = new HashMap<>();
		DungTheory af = new DungTheory();
		Map<SupportArgument<T>, Argument> nodes = new HashMap<>();
		Map<Argument, SupportArgument<T>> origin = new HashMap<>();
		for (SupportArgument<T> arg : args) {
			Argument node = new Argument("A" + nodes.size());
			nodes.put(arg, node);
			origin.put(node, arg);
			af.add(node);
			for (T claim : arg.claims)
				byClaim.computeIfAbsent(claim, k -> new HashSet<>()).add(arg);
		}
		for (SupportArgument<T> attacked : args)
			for (Assumption<T> a : attacked.support)
				for (T contrary : abat.getContraries(a.getConclusion()))
					for (SupportArgument<T> attacker : byClaim.getOrDefault(contrary, Set.of()))
						af.add(new Attack(nodes.get(attacker), nodes.get(attacked)));
		Collection<AbaExtension<T>> result = new HashSet<>();
		for (Extension<DungTheory> ext : AbstractExtensionReasoner.getSimpleReasonerForSemantics(semantics)
				.getModels(af)) {
			AbaExtension<T> abaExt = new AbaExtension<T>();
			for (Argument node : ext)
				abaExt.addAll(origin.get(node).support);
			result.add(abaExt);
		}
		return result;
	}

	/**
	 * Builds the support-minimal and support-unique arguments of a flat theory
	 *
	 * @param abat a flat ABA theory
	 * @return the arguments of the reduced framework
	 * @throws IllegalArgumentException if the theory is not flat
	 */
	public Collection<SupportArgument<T>> getArguments(AbaTheory<T> abat) {
		Map<Set<Assumption<T>>, SupportArgument<T>> bySupport = new HashMap<>();
		Map<T, Set<SupportArgument<T>>> byClaim = new HashMap<>();
		Deque<Map.Entry<SupportArgument<T>, T>> newArgs = new ArrayDeque<>();
		Map<T, List<InferenceRule<T>>> rulesByPremise = new HashMap<>();
		for (InferenceRule<T> r : abat.getRules())
			for (T p : new HashSet<T>(r.getPremise()))
				rulesByPremise.computeIfAbsent(p, k -> new ArrayList<>()).add(r);
		for (Assumption<T> a : abat.getAssumptions())
			createOrUpdate(a.getConclusion(), Set.of(a), bySupport, byClaim, newArgs);
		for (InferenceRule<T> r : abat.getRules())
			if (r.getPremise().isEmpty())
				createOrUpdate(r.getConclusion(), Set.of(), bySupport, byClaim, newArgs);
		while (!newArgs.isEmpty()) {
			Map.Entry<SupportArgument<T>, T> next = newArgs.pop();
			SupportArgument<T> arg = next.getKey();
			T h = next.getValue();
			// dropped since: a smaller argument for h is queued instead
			if (!arg.claims.contains(h) || bySupport.get(arg.support) != arg)
				continue;
			for (InferenceRule<T> r : rulesByPremise.getOrDefault(h, List.of())) {
				if (arg.claims.contains(r.getConclusion()))
					continue;
				Set<T> rest = new LinkedHashSet<T>(r.getPremise());
				rest.remove(h);
				for (Set<Assumption<T>> s : combineSupports(arg.support, rest, byClaim))
					createOrUpdate(r.getConclusion(), s, bySupport, byClaim, newArgs);
			}
		}
		// flat iff each assumption's only minimal support is itself
		for (Assumption<T> a : abat.getAssumptions()) {
			SupportArgument<T> singleton = bySupport.get(Set.of(a));
			if (singleton == null || !byClaim.get(a.getConclusion()).equals(Set.of(singleton)))
				throw new IllegalArgumentException("Only flat ABA theories are supported.");
		}
		return bySupport.values();
	}

	/**
	 * All unions of the given support with one support per remaining premise
	 */
	private Set<Set<Assumption<T>>> combineSupports(Set<Assumption<T>> support, Set<T> rest,
			Map<T, Set<SupportArgument<T>>> byClaim) {
		Set<Set<Assumption<T>>> result = new HashSet<>();
		result.add(support);
		for (T premise : rest) {
			Set<SupportArgument<T>> args = byClaim.get(premise);
			if (args == null || args.isEmpty())
				return Set.of();
			Set<Set<Assumption<T>>> next = new HashSet<>();
			for (Set<Assumption<T>> s : result)
				for (SupportArgument<T> arg : args) {
					Set<Assumption<T>> union = new HashSet<>(s);
					union.addAll(arg.support);
					next.add(union);
				}
			result = next;
		}
		return result;
	}

	/**
	 * Adds claim h with support s, unless a subset of s already derives h; then
	 * drops h from arguments with a superset of s
	 */
	private void createOrUpdate(T h, Set<Assumption<T>> s, Map<Set<Assumption<T>>, SupportArgument<T>> bySupport,
			Map<T, Set<SupportArgument<T>>> byClaim, Deque<Map.Entry<SupportArgument<T>, T>> newArgs) {
		Set<SupportArgument<T>> claimers = byClaim.computeIfAbsent(h, k -> new HashSet<>());
		for (SupportArgument<T> c : claimers)
			if (s.containsAll(c.support))
				return;
		SupportArgument<T> arg = bySupport.get(s);
		if (arg == null) {
			arg = new SupportArgument<>(new HashSet<>(s));
			bySupport.put(arg.support, arg);
		}
		arg.claims.add(h);
		Iterator<SupportArgument<T>> it = claimers.iterator();
		while (it.hasNext()) {
			SupportArgument<T> c = it.next();
			if (c.support.containsAll(s)) {
				it.remove();
				c.claims.remove(h);
				if (c.claims.isEmpty())
					bySupport.remove(c.support);
			}
		}
		claimers.add(arg);
		newArgs.add(new AbstractMap.SimpleEntry<>(arg, h));
	}

	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}
}
