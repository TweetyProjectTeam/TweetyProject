package org.tweetyproject.arg.rankings;
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
 

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.rankings.semantics.LatticeArgumentRanking;
import org.tweetyproject.arg.rankings.semantics.NumericalArgumentRanking;

/**
 * Test class for basic ranking functionalities.
 * 
 * @author Anna Gessler
 *
 */
public class RankingsTest {
	
	public static final int DEFAULT_TIMEOUT = 5000;
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void AscendingNumericalRankingTest() throws Exception {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		ranking.setSortingType(NumericalArgumentRanking.SortingType.ASCENDING);
		ranking.put(a, -1.0);
		ranking.put(b, 0.0);
		ranking.put(c, 1.0);
		assertTrue(ranking.isStrictlyMoreAcceptableThan(a, b));
		assertTrue(ranking.isStrictlyMoreAcceptableThan(a, c));
		assertTrue(ranking.isStrictlyMoreAcceptableThan(b, c));
		
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void DescendingNumericalRankingTest() throws Exception {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		ranking.setSortingType(NumericalArgumentRanking.SortingType.DESCENDING);
		ranking.put(a, -1.0);
		ranking.put(b, 0.0);
		ranking.put(c, 1.0);
		assertTrue(ranking.isStrictlyMoreAcceptableThan(c, b));
		assertTrue(ranking.isStrictlyMoreAcceptableThan(c, a));
		assertTrue(ranking.isStrictlyMoreAcceptableThan(b, a));
		
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void LexicographicNumericalRankingTest() throws Exception {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		ranking.setSortingType(NumericalArgumentRanking.SortingType.LEXICOGRAPHIC);
		ranking.put(a, -1.0);
		ranking.put(d, -2.0);
		ranking.put(b, 0.0);
		ranking.put(c, 1.0);
		ranking.put(e, 10.0);
		ranking.put(f, 2.0);
		assertTrue(ranking.isStrictlyMoreAcceptableThan(a, d));
		assertTrue(ranking.isStrictlyMoreAcceptableThan(d, b));
		assertTrue(ranking.isStrictlyMoreAcceptableThan(b, c));
		assertTrue(ranking.isStrictlyMoreAcceptableThan(c, e));
		assertTrue(ranking.isStrictlyMoreAcceptableThan(e, f));
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void equivalentRankingsTest1() throws Exception {
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		dt.add(a);
		dt.add(b);
		NumericalArgumentRanking ranking = new NumericalArgumentRanking();
		ranking.put(a, -1.0);
		ranking.put(b, 0.0);
		NumericalArgumentRanking ranking2 = new NumericalArgumentRanking();
		ranking2.put(a, -1.0);
		ranking2.put(b, 0.0);
		
		assertTrue(ranking.isEquivalent(ranking2, dt));	
		
		ranking2.put(b, -1.0);
		assertFalse(ranking.isEquivalent(ranking2, dt));	
	}
	
	@Test(timeout = DEFAULT_TIMEOUT)
	public void equivalentRankingsTest2() throws Exception {
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		dt.add(a);
		dt.add(b);
		LatticeArgumentRanking ranking = new LatticeArgumentRanking(dt);
		ranking.setStrictlyLessOrEquallyAcceptableThan(a, b);
		LatticeArgumentRanking ranking2 = new LatticeArgumentRanking(dt);
		ranking2.setStrictlyLessOrEquallyAcceptableThan(a, b);
		
		assertTrue(ranking.isEquivalent(ranking2, dt));	
		
		ranking2.setStrictlyLessOrEquallyAcceptableThan(b, a);
		assertFalse(ranking.isEquivalent(ranking2, dt));	
	}

	@Test(timeout = DEFAULT_TIMEOUT)
	public void IncomparableArgumentsTest() throws Exception {
		DungTheory dt = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		dt.add(a);
		dt.add(b);
		LatticeArgumentRanking ranking = new LatticeArgumentRanking(dt);
		assertTrue(ranking.isIncomparable(a, b));
		assertFalse(ranking.isEquallyAcceptableThan(a, b));
		assertFalse(ranking.isStrictlyLessAcceptableThan(a, b));
		assertFalse(ranking.isStrictlyLessAcceptableThan(b, a));
		assertFalse(ranking.isStrictlyMoreAcceptableThan(a, b));
		assertFalse(ranking.isStrictlyMoreAcceptableThan(b, a));
	}

	

}
