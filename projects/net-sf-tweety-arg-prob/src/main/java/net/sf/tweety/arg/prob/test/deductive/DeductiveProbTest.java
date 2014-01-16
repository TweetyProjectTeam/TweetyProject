package net.sf.tweety.arg.prob.test.deductive;

import java.io.IOException;

import net.sf.tweety.ParserException;
import net.sf.tweety.Reasoner;
import net.sf.tweety.TweetyConfiguration;
import net.sf.tweety.TweetyLogging;
import net.sf.tweety.arg.deductive.semantics.attacks.*;
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
