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
package org.tweetyproject.arg.setaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.reasoner.StronglyAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyAdmissibleReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyCompleteReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyGroundedReasoner;
import org.tweetyproject.arg.dung.reasoner.WeaklyPreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.setaf.reasoners.AbstractSetAfExtensionReasoner;
import org.tweetyproject.arg.setaf.reasoners.SimpleStronglyAdmissibleSetAfReasoner;
import org.tweetyproject.arg.setaf.reasoners.SimpleWeaklyAdmissibleSetAfReasoner;
import org.tweetyproject.arg.setaf.reasoners.SimpleWeaklyCompleteSetAfReasoner;
import org.tweetyproject.arg.setaf.reasoners.SimpleWeaklyGroundedSetAfReasoner;
import org.tweetyproject.arg.setaf.reasoners.SimpleWeaklyPreferredSetAfReasoner;
import org.tweetyproject.arg.setaf.syntax.SetAf;
import org.tweetyproject.arg.setaf.syntax.SetAttack;

/**
 * Tests for the reduct, strong admissibility and the weak semantics of SetAfs,
 * using the examples of Blümel, König, Ulbricht, 'Weak Admissibility for ABA via Abstract Set-Attacks', KR 2024
 *
 * @author Lars Bengel
 */
public class WeakStrongAdmissibilityTest {

	@Test
	public void Reduct() {
		SetAf reduct = example23().getReduct(arguments("d"));
		assertEquals(arguments("a b c"), new HashSet<>(reduct));
		assertEquals(Set.of(attack("b", "a"), attack("c", "b"), attack("a", "c")), reduct.getAttacks());
	}

	@Test
	public void WeaklyAdmissibleExamples() {
		SimpleWeaklyAdmissibleSetAfReasoner reasoner = new SimpleWeaklyAdmissibleSetAfReasoner();
		// Ex. 25: the attack ({c,d}, d) only leaves an isolated odd cycle in the reduct
		assertTrue(reasoner.isWeaklyAdmissible(example23(), extension("d")));
		// Ex. 26: f disrupts the odd cycle, so c can be accepted in the reduct of {d}
		SetAf ex26 = example23();
		ex26.add(new Argument("f"));
		ex26.add(attack("b", "f"));
		assertTrue(reasoner.isWeaklyAdmissible(ex26, extension("c f")));
		assertFalse(reasoner.isWeaklyAdmissible(ex26, extension("d")));
		// Ex. 27: the conflicting set {a, b} cannot defeat c
		assertTrue(reasoner.isWeaklyAdmissible(theory("a b c", attack("b", "a"), attack("a", "b"), attack("c", "a", "b")),
				extension("c")));
		// Ex. 34
		SetAf ex34 = theory("a b", attack("a", "a", "b"), attack("b", "a", "b"));
		assertEquals(names("", "a", "b"), names(reasoner.getModels(ex34)));
	}

	@Test
	public void WeakSemanticsExamples() {
		// Ex. 37 and 42
		SetAf ex37 = theory("a b c d e", attack("b", "a"), attack("a", "b"), attack("c", "a", "b"), attack("c", "d"),
				attack("d", "e"));
		assertEquals(names("c e"), names(new SimpleWeaklyGroundedSetAfReasoner().getModels(ex37)));
		assertEquals(names("a c e", "b c e"), names(new SimpleWeaklyPreferredSetAfReasoner().getModels(ex37)));
		assertEquals(names("c e", "a c e", "b c e"), names(new SimpleWeaklyCompleteSetAfReasoner().getModels(ex37)));
		assertEquals(names("", "e"), names(new SimpleStronglyAdmissibleSetAfReasoner().getModels(ex37)));
	}

	@Test
	public void MatchDungOnAfs() {
		Map<AbstractSetAfExtensionReasoner, AbstractExtensionReasoner> reasoners = Map.of(
				new SimpleStronglyAdmissibleSetAfReasoner(), new StronglyAdmissibleReasoner(),
				new SimpleWeaklyAdmissibleSetAfReasoner(), new WeaklyAdmissibleReasoner(),
				new SimpleWeaklyCompleteSetAfReasoner(), new WeaklyCompleteReasoner(),
				new SimpleWeaklyPreferredSetAfReasoner(), new WeaklyPreferredReasoner(),
				new SimpleWeaklyGroundedSetAfReasoner(), new WeaklyGroundedReasoner());
		Random random = new Random(0);
		for (int i = 0; i < 50; i++) {
			SetAf setaf = new SetAf();
			DungTheory af = new DungTheory();
			List<Argument> args = new ArrayList<>();
			for (int j = 0; j < 2 + random.nextInt(4); j++) {
				Argument a = new Argument("a" + j);
				args.add(a);
				setaf.add(a);
				af.add(a);
			}
			for (Argument a : args)
				for (Argument b : args)
					if (random.nextDouble() < 0.3) {
						setaf.addAttack(a, b);
						af.addAttack(a, b);
					}
			for (Map.Entry<AbstractSetAfExtensionReasoner, AbstractExtensionReasoner> e : reasoners.entrySet())
				assertEquals(names(e.getValue().getModels(af)), names(e.getKey().getModels(setaf)),
						e.getKey().getClass().getSimpleName() + ": " + af);
		}
	}

	// Ex. 23
	private static SetAf example23() {
		return theory("a b c d e", attack("b", "a"), attack("c", "b"), attack("a", "c", "d"), attack("d", "c", "d"),
				attack("d", "b", "e"), attack("e", "d"));
	}

	private static SetAf theory(String args, SetAttack... attacks) {
		SetAf setaf = new SetAf();
		for (Argument a : arguments(args))
			setaf.add(a);
		for (SetAttack att : attacks)
			setaf.add(att);
		return setaf;
	}

	private static SetAttack attack(String attacked, String... attackers) {
		Set<Argument> tail = new HashSet<>();
		for (String a : attackers)
			tail.add(new Argument(a));
		return new SetAttack(tail, new Argument(attacked));
	}

	private static Set<Argument> arguments(String args) {
		Set<Argument> result = new HashSet<>();
		if (!args.isEmpty())
			for (String a : args.split(" "))
				result.add(new Argument(a));
		return result;
	}

	private static Extension<SetAf> extension(String args) {
		return new Extension<>(arguments(args));
	}

	private static Set<Set<String>> names(Collection<? extends Collection<Argument>> exts) {
		Set<Set<String>> result = new HashSet<>();
		for (Collection<Argument> ext : exts) {
			Set<String> names = new HashSet<>();
			for (Argument a : ext)
				names.add(a.getName());
			result.add(names);
		}
		return result;
	}

	private static Set<Set<String>> names(String... exts) {
		Set<Set<String>> result = new HashSet<>();
		for (String ext : exts)
			result.add(ext.isEmpty() ? Set.of() : Set.of(ext.split(" ")));
		return result;
	}
}
