/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.dung.examples;

import java.util.Collection;

import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.GroundReasoner;
import net.sf.tweety.arg.dung.divisions.Division;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

/**
 * Example code for working with divisions
 * @author Matthias Thimm
 */
public class DivisionExample {

	public static void main(String[] args){
		// create some Dung theory
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		theory.add(a);
		theory.add(b);
		theory.add(c);		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(c,b));
		
		// Instantiate reasoner
		AbstractExtensionReasoner r = new GroundReasoner(theory);
		Collection<Extension> exts = r.getExtensions();
		
		// print theory
		System.out.println("AAF: " + theory);
		
		// print extensions
		System.out.println();
		System.out.println("Extensions: ");
		for(Extension e: exts)
			System.out.println(e);
		
		// print divisions
		System.out.println();
		System.out.println("Divisions: ");
		for(Division div: Division.getDivisions(exts, theory)){
			System.out.println(div);
			System.out.println("\tDividers:");
			for(DungTheory d: div.getDividers(theory, Semantics.GROUNDED_SEMANTICS))
				System.out.println("\t" +d);			
		}
		
		
	}
}
