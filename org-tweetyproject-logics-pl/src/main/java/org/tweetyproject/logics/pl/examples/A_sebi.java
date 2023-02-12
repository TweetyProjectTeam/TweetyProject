package org.tweetyproject.logics.pl.examples;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.Proposition;

public class A_sebi {
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException {
		PlParser parser = new PlParser();
		PlBeliefSet p = parser.parseBeliefBaseFromFile("C:\\Users\\Sebastian\\Desktop\\a.txt");
		PlFormula f = p.getCanonicalOrdering().get(0);
		Proposition a = f.getAtoms().iterator().next();
		Proposition a1 = new Proposition("a");
		System.out.println("formula: " + f.toString());
		System.out.println("chosen from formula: " + a.toString());
		System.out.println("occurence of " + a + "(chosen from formula): " + f.numberOfOccurrences(a));
		System.out.println("occurence of " + a1 + "(newly created): " + f.numberOfOccurrences(a1));
	}
}
