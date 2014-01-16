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
