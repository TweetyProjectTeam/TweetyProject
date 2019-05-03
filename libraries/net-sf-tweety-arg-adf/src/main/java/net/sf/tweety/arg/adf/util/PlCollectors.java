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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collector;

import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.commons.util.Triple;
import net.sf.tweety.logics.pl.syntax.AssociativePlFormula;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * A helper class providing some comfort functions dealing with stream
 * collecting of PlFormula.
 * 
 * @author Mathias Hofer
 *
 */
public class PlCollectors {

	public static Collector<PlFormula, ?, Conjunction> toConjunction() {
		return Collector.of(Conjunction::new, Conjunction::add, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	public static Collector<PlFormula, ?, Disjunction> toDisjunction() {
		return Collector.of(Disjunction::new, Disjunction::add, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	public static Collector<Pair<? extends PlFormula, ? extends PlFormula>, ?, Disjunction> toDisjunctionOfConjunctivePairs() {
		return Collector.of(Disjunction::new, (t, u) -> {
			t.add(new Conjunction(u.getFirst(), u.getSecond()));
		}, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	public static Collector<Triple<? extends PlFormula, ? extends PlFormula, ? extends PlFormula>, ?, Disjunction> toDisjunctionOfConjunctiveTriples() {
		return Collector.of(Disjunction::new, (t, u) -> {
			t.add(new Conjunction(Arrays.asList(u.getFirst(), u.getSecond(), u.getThird())));
		}, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	public static Collector<Collection<? extends PlFormula>, ?, Disjunction> toDisjunctionOfConjunctions() {
		return Collector.of(Disjunction::new, (t, u) -> t.add(new Conjunction(u)), (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	public static Collector<Pair<? extends PlFormula, ? extends PlFormula>, ?, Conjunction> toConjunctionOfDisjunctivePairs() {
		return Collector.of(Conjunction::new, (t, u) -> {
			t.add(new Disjunction(u.getFirst(), u.getSecond()));
		}, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	public static Collector<Triple<? extends PlFormula, ? extends PlFormula, ? extends PlFormula>, ?, Conjunction> toConjunctionOfDisjunctiveTriples() {
		return Collector.of(Conjunction::new, (t, u) -> {
			t.add(new Disjunction(Arrays.asList(u.getFirst(), u.getSecond(), u.getThird())));
		}, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}
	
	public static Collector<Collection<? extends PlFormula>, ?, Conjunction> toConjunctionOfDisjunctions() {
		return Collector.of(Conjunction::new, (t, u) -> t.add(new Disjunction(u)), (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	public static <T> Collector<T, ?, Disjunction> toDisjunction(Function<T, ? extends PlFormula>[] functions) {
		BiConsumer<Disjunction, T> accumulator = new MapAccumulator<Disjunction, T>(functions);
		return Collector.of(Disjunction::new, accumulator, (left, right) -> {
			left.addAll(right);
			return left;
		});
	}

	private static class MapAccumulator<R extends AssociativePlFormula, T> implements BiConsumer<R, T> {

		private Function<T, ? extends PlFormula>[] functions;

		public MapAccumulator(Function<T, ? extends PlFormula>[] functions) {
			this.functions = functions;
		}

		@Override
		public void accept(R t, T u) {
			for (Function<T, ? extends PlFormula> f : functions) {
				t.add(f.apply(u));
			}
		}
	}
}
