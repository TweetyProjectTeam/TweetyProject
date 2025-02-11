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
* Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
*/
package org.tweetyproject.arg.eaf.examples;

import java.io.IOException;
import java.util.Collection;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.eaf.reasoner.AbstractEAFReasoner;
import org.tweetyproject.arg.eaf.reasoner.SimpleEAFStableReasoner;
import org.tweetyproject.arg.eaf.semantics.EAFSemantics;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.commons.ParserException;



/**
 * 
 */
public class EafReasonerExample {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParserException 
	 */
	public static void main(String[] args) throws ParserException, IOException {
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");

		String constEAF1 = "<>(b)";
		String constEAF2 = "[](a)||[](b)";
		String constEAF3 = "<>(c)=>[](a)";
		String constEAF4 = "(<>(c)=>[](a)) && [](d)";
		String constEAF5 = "(<>(c)=>[](a)) && [](d) && [](!a)";


		DungTheory af = new DungTheory();
		
		af.add(a);
		af.add(b);
		af.add(c);
		af.add(d);

		
		af.addAttack(a,b);
		af.addAttack(b,a);
		af.addAttack(c,b);	
		af.addAttack(c,d);
		af.addAttack(d,c);

	
		//Create EAFs
		EpistemicArgumentationFramework eaf1 = new EpistemicArgumentationFramework(af, constEAF1);	
		EpistemicArgumentationFramework eaf2 = new EpistemicArgumentationFramework(af, constEAF2);
		EpistemicArgumentationFramework eaf3 = new EpistemicArgumentationFramework(af, constEAF3);
		EpistemicArgumentationFramework eaf4 = new EpistemicArgumentationFramework(af, constEAF4);
		EpistemicArgumentationFramework eaf5 = new EpistemicArgumentationFramework(af, constEAF5);
		
		//Get epistemic labelling sets for specified semantics
		
		System.out.println("The EAF: \n"+ eaf1.prettyPrint() +"\n\nhas the following stable labelling set:\n");
		System.out.println(eaf1.getWEpistemicLabellingSets(Semantics.ST));
		
		System.out.println("\nThe EAF with the same underlying AF and the constraint "+ eaf2.getConstraint()+" has the following stable labelling sets:\n");
		System.out.println(eaf2.getWEpistemicLabellingSets(Semantics.ST));
		
		System.out.println("\nThe EAF with the same underlying AF and the constraint "+ eaf3.getConstraint()+" has the following stable labelling sets:\n");
		System.out.println(eaf3.getWEpistemicLabellingSets(Semantics.ST));
		
		
		//Reasoners can be created using the abstract superclass
		AbstractEAFReasoner eafReasoner = AbstractEAFReasoner.getSimpleReasonerForSemantics(EAFSemantics.EAF_PR);
		Collection<Extension<EpistemicArgumentationFramework>> eafPrSets = eafReasoner.getModels(eaf1);
		System.out.println(eafPrSets);
		System.out.println("Credulous justification status of each argument under preferred semantics:");
		for(Argument arg: eaf1) {
			System.out.println(arg +": " + eafReasoner.query(eaf1, arg, InferenceMode.CREDULOUS));
		}
		
		//Or by instantiating a reasoner for the required semantics
		SimpleEAFStableReasoner eafSt = new SimpleEAFStableReasoner();
		System.out.println("Stable Extension:");
		System.out.println(eafSt.getModel(eaf4));
	}

}
