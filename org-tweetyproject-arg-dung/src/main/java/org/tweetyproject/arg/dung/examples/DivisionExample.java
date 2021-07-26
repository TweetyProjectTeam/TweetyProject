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
package org.tweetyproject.arg.dung.examples;

import java.util.Collection;

import org.tweetyproject.arg.dung.reasoner.AbstractExtensionReasoner;
import org.tweetyproject.arg.dung.divisions.Division;
import org.tweetyproject.arg.dung.reasoner.SimpleGroundedReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Example code for working with divisions.
 * 
 * @author Matthias Thimm
 */
public class DivisionExample {

	/**
	 * 
	 * @param args string
	 */
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
		AbstractExtensionReasoner r = new SimpleGroundedReasoner();
		Collection<Extension> exts = r.getModels(theory);
		
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
