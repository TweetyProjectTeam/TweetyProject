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

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.eaf.syntax.EpistemicArgumentationFramework;

/**
 * 
 */
public class EafPreferenceRepresentationExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Argument a = new Argument("a");
		Argument r = new Argument("r");
		
		DungTheory af = new DungTheory();
		
		af.add(a);
		af.add(r);
		
		af.addAttack(a,r);
		af.addAttack(r,a);


		String constEAF1 = "[]((a)=>(r))";
		EpistemicArgumentationFramework eaf1 = new EpistemicArgumentationFramework(af, constEAF1);	
		
		System.out.println("Preference over arguments can be represented using the implication operator, where []((a)=>(r)) means that the preferred argument r should be accepted, whenever a is accepted.");
		System.out.print("The EAF: \n"+ eaf1.prettyPrint() +"\n\nhas the following stable labelling set:");
		System.out.println(eaf1.getWEpistemicLabellingSets(Semantics.ST));
		System.out.print("This EAF has the following complete labelling set:");
		System.out.println(eaf1.getWEpistemicLabellingSets(Semantics.CO));
		System.out.println();
		String constEAF2 = "[]((a)||und(a)=>(r))";
		EpistemicArgumentationFramework eaf2 = new EpistemicArgumentationFramework(af, constEAF2);
		System.out.print("This EAF has the following complete labelling set:");
		System.out.println(eaf2.getWEpistemicLabellingSets(Semantics.CO));

	}

}
