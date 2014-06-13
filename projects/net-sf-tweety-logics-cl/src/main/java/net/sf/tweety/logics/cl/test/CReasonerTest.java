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
package net.sf.tweety.logics.cl.test;

import net.sf.tweety.logics.cl.*;
import net.sf.tweety.logics.cl.syntax.*;
import net.sf.tweety.logics.pl.syntax.*;

public class CReasonerTest {
	public static void main(String[] args){
		Proposition f = new Proposition("f");
		Proposition b = new Proposition("b");
		Proposition p = new Proposition("p");
		
		Conditional c1 = new Conditional(b,f);
		Conditional c2 = new Conditional(p,b);
		Conditional c3 = new Conditional(p,new Negation(f));
		
		ClBeliefSet bs = new ClBeliefSet();
		bs.add(c1);
		bs.add(c2);
		bs.add(c3);
		
		System.out.println(bs);
		
		BruteForceCReasoner reasoner = new BruteForceCReasoner(bs);
		
		System.out.println(reasoner.getCRepresentation());
		
	}
}
