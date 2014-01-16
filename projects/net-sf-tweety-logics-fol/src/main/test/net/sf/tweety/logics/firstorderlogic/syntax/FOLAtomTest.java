package net.sf.tweety.logics.firstorderlogic.syntax;


import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;



import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.StringTerm;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

public class FOLAtomTest {

	@Test
	public void testGetTerms() {
		List<Term<?>> arguments = new ArrayList<Term<?>>();
		arguments.add(new Constant("bob"));
		arguments.add(new Variable("F"));
		FOLAtom atom = new FOLAtom(new Predicate("married", 2), arguments);
		
		assertEquals(2, atom.getTerms().size());
		assertEquals(0, atom.getTerms(StringTerm.class).size());
		assertEquals(1, atom.getTerms(Constant.class).size());
		assertEquals(1, atom.getTerms(Variable.class).size());
		assertEquals(true, atom.getTerms(Constant.class).contains(new Constant("bob")));
		assertEquals(true, atom.getTerms(Variable.class).contains(new Variable("F")));
	}
}
