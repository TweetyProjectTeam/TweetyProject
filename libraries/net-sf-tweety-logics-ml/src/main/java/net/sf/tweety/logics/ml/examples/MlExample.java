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
package net.sf.tweety.logics.ml.examples;

import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.ml.reasoner.SimpleMlReasoner;
import net.sf.tweety.logics.ml.syntax.MlBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.ml.parser.MlParser;

/**
 * Some examples for testing ModalParser and NaiveModalReasoner. Shows how to
 * construct a modal logic knowledge base programmatically and how to query it
 * using the naive reasoner.
 * 
 * @author Anna Gessler
 */
public class MlExample {

	public static void main(String[] args) throws ParserException, IOException {
		// Parse simple BeliefBase from file
		MlParser parser = new MlParser();
		MlBeliefSet b1 = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase2.mlogic");
		FolFormula f1 = (FolFormula) parser.parseFormula("<>(A&&B)");
		System.out.println("Parsed belief base:" + b1 + "\nSignature of belief base:" + b1.getMinimalSignature());

		// Parse simple BeliefBase from string
		parser = new MlParser();
		MlBeliefSet b2 = parser.parseBeliefBase("Animal = {penguin,eagle} \n type(Flies(Animal)) \n (Flies(eagle))");
		FolFormula f2 = (FolFormula) parser.parseFormula("(Flies(penguin)) || (!(Flies(penguin)))");
		System.out.println("Parsed belief base:" + b2);

		// Parse more complex BeliefBase from file
		parser = new MlParser();
		MlBeliefSet b3 = parser.parseBeliefBaseFromFile("src/main/resources/examplebeliefbase.mlogic");
		System.out.println("Parsed belief base:" + b3 + "\nSignature of belief base:" + b3.getMinimalSignature());

		// Reasoner examples
		SimpleMlReasoner reasoner = new SimpleMlReasoner();
		System.out.println("Answer to query: " + reasoner.query(b1, f1));
		System.out.println("Answer to query: " + reasoner.query(b2, f2));
	}

}
