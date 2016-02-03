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
package net.sf.tweety.logics.fol.parser;

import java.io.StringReader;

import net.sf.tweety.logics.fol.FolBeliefSet;

/**
 * Functional test for the new javacc parser 'FolParserB'
 * 
 * @author Tim Janus
 */
public class FunctionalTest {
	public static void main(String [] args) {
		/*
		String 	bb	= "married(john, mary)\n";
				bb += "-argued(john)";
		
				bb  = "Persons={john,mary}\n";
				bb += "type (married(Persons, Persons))\n";
				bb += "type (argued(Persons))\n";
				bb += "type (test(Persons))\n";
				bb += "type (Persons = wife(Persons))\n";
				bb += "married(john, mary)\n";
				bb += "-argued(john)\n";
				bb += "test(wife(john))";
		*/
		//String bb = "(q_Mary(said(X), 1) && a) || b";
			
		String bb = "not_sure(excused(employee))";		
		FolParserB bp = new FolParserB(new StringReader(bb));
		//bp.setForce(true);
		
		try {
			FolBeliefSet fbs = bp.KB();
			System.out.println("Signature: " + fbs.getSignature());
			System.out.println("Beliefbase: " + fbs);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

