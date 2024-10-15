/*
* This file is part of "TweetyProject", a collection of Java libraries for
* logical aspects of artificial intelligence and knowledge representation.
*
* TweetyProject is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License version 3 as
* published by the Free Software Foundation.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.caf.examples;

import java.util.Collection;

import org.tweetyproject.arg.caf.reasoner.SimpleCAFAdmissibleReasoner;
import org.tweetyproject.arg.caf.reasoner.SimpleCAFGroundedReasoner;
import org.tweetyproject.arg.caf.reasoner.SimpleCAFPreferredReasoner;
import org.tweetyproject.arg.caf.reasoner.SimpleCAFStableReasoner;
import org.tweetyproject.arg.caf.syntax.ConstrainedArgumentationFramework;
import org.tweetyproject.arg.dung.reasoner.SimplePreferredReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * Example Class on how to perform inference on constrained argumentation frameworks.
 * @author Sandra Hoffmann
 *
 */
public class CAFReasonerExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		String propForm = "!a || !d || !e";

		DungTheory af = new DungTheory();
		
		af.add(a);
		af.add(b);
		af.add(c);
		af.add(d);
		af.add(e);
		af.add(f);
		
		af.addAttack(a,b);
		af.addAttack(a,c);
		af.addAttack(b,d);	
		af.addAttack(c,e);
		af.addAttack(e,f);
		af.addAttack(f,e);
	
		
		ConstrainedArgumentationFramework caf = new ConstrainedArgumentationFramework(af, propForm);		
		
		System.out.println("The CAF: \n"+ caf.prettyPrint() +"\n\nhas the following C-extensions:\n");
		
		System.out.println("C-Admissible Extensions:");
		SimpleCAFAdmissibleReasoner cafAdmR = new SimpleCAFAdmissibleReasoner();
		Collection<Extension<DungTheory>> cAdmSets = cafAdmR.getModels(caf);
		System.out.println(cAdmSets);
		
		
		SimpleCAFGroundedReasoner cafGr = new SimpleCAFGroundedReasoner();
		System.out.println("C-Grounded Extension:");
		System.out.println(cafGr.getModel(caf));
		
		System.out.println("Weak C-Extension:");
		System.out.println(cafGr.getWeakModel(caf));
		
		System.out.println("\nC-Preferred Extensions:");
		SimpleCAFPreferredReasoner cafPrefR = new SimpleCAFPreferredReasoner();
		System.out.println(cafPrefR.getModels(caf));
		
		System.out.println("\nC-Stable Extensions:");
		SimpleCAFStableReasoner cafStR = new SimpleCAFStableReasoner();
		System.out.println(cafStR.getModels(caf));
	
	}

}
