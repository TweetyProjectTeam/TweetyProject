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

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.tweetyproject.arg.aba.reasoner.AfReductionReasoner.SupportArgument;
import org.tweetyproject.arg.aba.semantics.AbaExtension;
import org.tweetyproject.arg.aba.syntax.AbaTheory;
import org.tweetyproject.arg.aba.syntax.Assumption;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.reasoners.AbstractSetAfExtensionReasoner;
import org.tweetyproject.arg.setaf.reasoners.ReductionBasedSetAfReasoner;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.commons.Formula;

/**
 * This reasoner for flat ABA theories reduces the theory to a SETAF over its
 * assumptions: a set T attacks a iff T minimally derives a contrary of a. An
 * assumption with a contrary derivable from the empty set is never accepted; it
 * is left out together with the attacks it takes part in.
 * <p>
 * Semantics with a native SETAF reasoner (cf, adm, co, gr, pr, st, sst, id, ea,
 * stg) are evaluated on the SETAF; all others via the metalevel reduction of
 * the SETAF to a Dung framework.
 * <p>
 * Proven to coincide for: complete, preferred, grounded and stable.
 * naive, cf2, scf2 and stg2 are rejected; the other reduction-based
 * semantics are unclear as of now.
 *
 * @see "König, Rapberger, Ulbricht, 'Just a Matter of Perspective', COMMA 2022"
 *
 * @author Lars Bengel
 *
 * @param <T> the language of the underlying ABA theory
 */
public class SetafReductionReasoner<T extends Formula> extends GeneralAbaReasoner<T> {

	/** Semantics evaluated directly on the SETAF */
	private static final EnumSet<Semantics> NATIVE = EnumSet.of(Semantics.CF, Semantics.ADM, Semantics.CO,
			Semantics.GR, Semantics.PR, Semantics.ST, Semantics.SST, Semantics.ID, Semantics.EA, Semantics.STG);

	/** Semantics whose metalevel reduction does not preserve conflicts */
	private static final EnumSet<Semantics> UNSUPPORTED = EnumSet.of(Semantics.NA, Semantics.CF2, Semantics.SCF2,
			Semantics.STG2);

	private final AbstractSetAfExtensionReasoner reasoner;

	/**
	 * Creates a new reasoner
	 *
	 * @param semantics the semantics to evaluate the SETAF with
	 * @throws IllegalArgumentException for unsupported semantics
	 */
	public SetafReductionReasoner(Semantics semantics) {
		if (UNSUPPORTED.contains(semantics))
			throw new IllegalArgumentException("The SETAF reduction does not support " + semantics.description() + ".");
		this.reasoner = NATIVE.contains(semantics) ? AbstractSetAfExtensionReasoner.getSimpleReasonerForSemantics(semantics)
				: new ReductionBasedSetAfReasoner(semantics);
	}

	@Override
	public Collection<AbaExtension<T>> getModels(AbaTheory<T> abat) {
		Map<Argument, Assumption<T>> origin = new HashMap<>();
		SetAf setaf = getSetaf(abat, origin);
		Collection<AbaExtension<T>> result = new HashSet<>();
		for (Extension<SetAf> ext : reasoner.getModels(setaf)) {
			AbaExtension<T> abaExt = new AbaExtension<T>();
			for (Argument arg : ext)
				abaExt.add(origin.get(arg));
			result.add(abaExt);
		}
		return result;
	}

	/**
	 * Builds the SETAF of a flat theory
	 *
	 * @param abat a flat ABA theory
	 * @return the SETAF over the assumptions of the theory
	 * @throws IllegalArgumentException if the theory is not flat
	 */
	public SetAf getSetaf(AbaTheory<T> abat) {
		return getSetaf(abat, new HashMap<>());
	}

	private SetAf getSetaf(AbaTheory<T> abat, Map<Argument, Assumption<T>> origin) {
		Collection<SupportArgument<T>> args = new AfReductionReasoner<T>(Semantics.CO).getArguments(abat);
		Map<T, Set<Set<Assumption<T>>>> supports = new HashMap<>();
		for (SupportArgument<T> arg : args)
			for (T claim : arg.getClaims())
				supports.computeIfAbsent(claim, k -> new HashSet<>()).add(arg.getSupport());
		Map<Assumption<T>, Set<Set<Assumption<T>>>> attackers = new HashMap<>();
		Set<Assumption<T>> dead = new HashSet<>();
		for (Assumption<T> a : abat.getAssumptions()) {
			Set<Set<Assumption<T>>> tails = new HashSet<>();
			for (T contrary : abat.getContraries(a.getConclusion()))
				tails.addAll(supports.getOrDefault(contrary, Set.of()));
			attackers.put(a, tails);
			if (tails.contains(Set.of()))
				dead.add(a);
		}
		SetAf setaf = new SetAf();
		Map<Assumption<T>, Argument> nodes = new HashMap<>();
		for (Assumption<T> a : abat.getAssumptions())
			if (!dead.contains(a)) {
				Argument node = new Argument(a.toString());
				nodes.put(a, node);
				origin.put(node, a);
				setaf.add(node);
			}
		for (Map.Entry<Assumption<T>, Argument> e : nodes.entrySet())
			for (Set<Assumption<T>> tail : attackers.get(e.getKey())) {
				if (tail.stream().anyMatch(dead::contains))
					continue;
				Set<Argument> tailNodes = new HashSet<>();
				for (Assumption<T> t : tail)
					tailNodes.add(nodes.get(t));
				setaf.addSetAttack(tailNodes, e.getValue());
			}
		return setaf;
	}

	/**
	 * the solver is natively installed and is therefore always installed
	 */
	@Override
	public boolean isInstalled() {
		return true;
	}
}
