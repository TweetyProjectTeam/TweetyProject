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
package net.sf.tweety.arg.prob.test.deductive;

import java.io.IOException;

import net.sf.tweety.arg.deductive.semantics.attacks.*;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.commons.TweetyConfiguration;
import net.sf.tweety.commons.TweetyLogging;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.math.probability.Probability;

public class DeductiveProbTest {

	public static void main(String[] args) throws ParserException, IOException{
		TweetyLogging.logLevel =  TweetyConfiguration.LogLevel.ERROR;
		TweetyLogging.initLogging();
		// Create some knowledge base
		DeductiveProbabilisticKnowledgebase myKb = new DeductiveProbabilisticKnowledgebase();
		PlParser parser = new PlParser();
		// add formulas
		myKb.getKb().add((PropositionalFormula)parser.parseFormula("a"));
		myKb.getKb().add((PropositionalFormula)parser.parseFormula("b"));
		myKb.getKb().add((PropositionalFormula)parser.parseFormula("!b || !a"));
		//myKb.getKb().add((PropositionalFormula)parser.parseFormula("!a || !b"));
		//myKb.getKb().add((PropositionalFormula)parser.parseFormula("!c || d"));
		// add probability assignments
		myKb.getProbabilityAssignments().put((PropositionalFormula)parser.parseFormula("b"), new Probability(0.6));
		myKb.getProbabilityAssignments().put((PropositionalFormula)parser.parseFormula("!b || !a"), new Probability(1d));
		
		// List signature and knowledge base
		System.out.println("Signature: " + myKb.getSignature());
		System.out.println("Knowledge base: " + myKb);
		System.out.println("Arguments of knowledge base: " + myKb.getKb().getDeductiveArguments());
		System.out.println("#Arguments of knowledge base: " + myKb.getKb().getDeductiveArguments().size());		
		System.out.println();
		
		// Compute ME-function for the above knowledge base and give some probabilities to queries
		Reasoner reasoner = new DeductiveArgMeReasoner(myKb, DirectDefeat.getInstance());
		System.out.println("P(a) = " + reasoner.query(parser.parseFormula("a")).getAnswerDouble());
		System.out.println("P(b) = " + reasoner.query(parser.parseFormula("b")).getAnswerDouble());
//		System.out.println("P(c) = " + reasoner.query(parser.parseFormula("c")).getAnswerDouble());
		System.out.println("P(a && b) = " + reasoner.query(parser.parseFormula("a && b")).getAnswerDouble());
		System.out.println("P(!a && b) = " + reasoner.query(parser.parseFormula("!a && b")).getAnswerDouble());
		System.out.println("P(a && !b) = " + reasoner.query(parser.parseFormula("a && !b")).getAnswerDouble());
		System.out.println("P(!a && !b) = " + reasoner.query(parser.parseFormula("!a && !b")).getAnswerDouble());		
//		System.out.println("P(a && c) = " + reasoner.query(parser.parseFormula("a && c")).getAnswerDouble());
		
	}
}
