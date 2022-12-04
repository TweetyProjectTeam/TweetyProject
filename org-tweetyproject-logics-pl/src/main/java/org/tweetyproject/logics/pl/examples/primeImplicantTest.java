package org.tweetyproject.logics.pl.examples;

import java.io.IOException;
import java.util.Set;

import org.tweetyproject.commons.InterpretationSet;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.analysis.tmp_primImplicantAndMinModel;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.tmp_allModelEnumerator;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

public class primeImplicantTest {

	public static void main(String[] args) throws ParserException, IOException {
		PlBeliefSet beliefSet = new PlBeliefSet();
		PlParser parser = new PlParser();
		for(int i = 0; i < 7; i++){
			beliefSet.add((PlFormula)parser.parseFormula("a"+i));
			beliefSet.add((PlFormula)parser.parseFormula("!a"+i));
		}
		beliefSet.add((PlFormula)parser.parseFormula("!a1 && !a2"));
		beliefSet.add((PlFormula)parser.parseFormula("!a3 || !a4"));
		beliefSet.add((PlFormula)parser.parseFormula("!a3 || !a4 || a5"));		
		System.out.println(beliefSet);
		
		Set<InterpretationSet<Proposition,PlBeliefSet,PlFormula>> Models = new tmp_allModelEnumerator().getWitness(beliefSet);
		System.out.println(Models.toString());
		System.out.println(new tmp_primImplicantAndMinModel().getMinModels(Models).toString());
		Set<Set<PlFormula>> aa = new tmp_primImplicantAndMinModel().getPrimeImplicants(new tmp_primImplicantAndMinModel().getMinModels(Models), beliefSet);
		System.out.println(aa.toString());
		
	}
}
