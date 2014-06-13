/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.conditionallogic.semantics;

import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.logics.conditionallogic.syntax.Conditional;
import net.sf.tweety.logics.propositionallogic.semantics.PossibleWorld;
import net.sf.tweety.logics.propositionallogic.syntax.Disjunction;
import net.sf.tweety.logics.propositionallogic.syntax.Proposition;
import net.sf.tweety.logics.propositionallogic.syntax.PropositionalFormula;

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
		dis.add((PropositionalFormula) a.complement());
		
		PossibleWorld world = new PossibleWorld(lst);
		Conditional c = new Conditional(dis);
		assertEquals(true, RankingFunction.verifies(world, c));
	}
}
