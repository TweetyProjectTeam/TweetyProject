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
