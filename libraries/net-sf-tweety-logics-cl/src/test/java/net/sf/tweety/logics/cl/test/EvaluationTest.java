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
package net.sf.tweety.logics.cl.test;

import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.logics.cl.semantics.RankingFunction;
import net.sf.tweety.logics.cl.syntax.Conditional;
import net.sf.tweety.logics.pl.semantics.PossibleWorld;
import net.sf.tweety.logics.pl.syntax.Disjunction;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PlFormula;

import org.junit.Test;
import static org.junit.Assert.*;

public class EvaluationTest {
	@Test
	public void testDisjunctionFact() {
		List<Proposition> lst = new LinkedList<Proposition>();
		Proposition a = new Proposition("a");
		Proposition b = new Proposition("b");
		lst.add(a);
		lst.add(b);
		
		Disjunction dis = new Disjunction();
		dis.add(b);
		dis.add((PlFormula) a.complement());
		
		PossibleWorld world = new PossibleWorld(lst);
		Conditional c = new Conditional(dis);
		assertEquals(true, RankingFunction.verifies(world, c));
	}
}
