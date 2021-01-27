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
 *  Copyright 2017 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.deductive.examples;

import java.io.IOException;
import java.io.StringReader;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.deductive.parser.SimplePlLogicParser;
import org.tweetyproject.arg.deductive.syntax.SimplePlLogicDeductiveKnowledgebase;
import org.tweetyproject.commons.ParserException;

/**
 * Example code for simple logic argumentation.
 * 
 * @author Federico Cerutti (federico.cerutti@acm.org)
 *
 */
public class SimplePlLogicExample {

	public static void main(String[] args) {
		
		String skb = "a" + "\n" +
					"t" + "\n" +
					"a, t -> b" + "\n" + 
					"b -> c" + "\n" +
					"-> d" + "\n" +
					"d -> !a" + "\n" +
					"d -> !c";
		
		
		SimplePlLogicParser p = new SimplePlLogicParser();
		
		SimplePlLogicDeductiveKnowledgebase k = null;
		try {
			k = p.parseBeliefBase(new StringReader(skb));
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(k.getAF());
		
		DungTheory af = k.getAF();
		for (Argument arg : af.getNodes())
			System.out.println(arg);
		
		for (Attack att : af.getAttacks()){
			System.out.println(att);
		}
	}

}
