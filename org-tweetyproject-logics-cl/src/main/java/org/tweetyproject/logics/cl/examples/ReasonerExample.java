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
package org.tweetyproject.logics.cl.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.tweetyproject.logics.cl.reasoner.SimpleCReasoner;
import org.tweetyproject.logics.cl.reasoner.RuleBasedCReasoner;
import org.tweetyproject.logics.cl.semantics.RankingFunction;
import org.tweetyproject.logics.cl.syntax.ClBeliefSet;
import org.tweetyproject.logics.cl.syntax.Conditional;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.Proposition;

/**
 * Compares reasoning with different reasoners.
 */
public class ReasonerExample {
	/** Creates the example application. */
	public ReasonerExample(){
		// default
	}

	/** Indicates if the program should continue running. */
	private static boolean run = true;

	/** Indicates if the comparison between rule-based and brute-force reasoners should be performed. */
	private static boolean compare = true;

	/** Menu entries shown by the example application. */
	private static final String [] entries = {
		"Beispiel 'Simpel'", "Beispiel 'Drowning Problem'", "Beispiel 'Komplex (Kiwis)'",
		"Beispiel-Beschreibungen", "Toggle Bruteforce Reasoner", "Beenden"
	};

	/** Belief sets used by the menu entries. */
	private static ClBeliefSet [] beliefSets;

	/** Short descriptions for the belief sets. */
	private static final String [] descriptions = {
		"Einfaches Beispiel enthaelt 3 Konditionale und 4 Symbole jedoch keine Spezialfaelle.",
		"Enthaelt das Drowning-Problem enkodiert als 'Pinguine sind Voegel die keine Fluegel haben'.",
		"Erhoeht die Komplexitaet des Beispiels, so dass 6 Konditionale und 5 Symbole verwendet werden."
	};

	/**
	 * Runs the example application.
	 *
	 * @param args the command-line arguments
	 * @throws IOException if reading from standard input fails
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		init();
		while(run) {
			System.out.println("Status:");
			printStatus();

			System.out.println();
			System.out.println("Menu:");
			for(int i=0; i<entries.length; ++i) {
				System.out.print(String.valueOf(i+1) + ". " + entries[i]);
				if(i < beliefSets.length) {
					System.out.print(" - " + beliefSets[i].toString());
				}
				System.out.println();
			}

			Integer selection = -1;
			String str = in.readLine();
			try {
				selection = Integer.parseInt(str);
			} catch (NumberFormatException nfe) {
				System.out.println(nfe.getMessage());
			}

			if(selection >= 1 && selection < entries.length-2) {
				test(beliefSets[selection-1]);
			} else if(selection == (entries.length-2)) {
				printDescription();
			} else if (selection == (entries.length-1)) {
				compare = !compare;
			} else if(selection == entries.length) {
				run = false;
			}


		}
	}

	/** Initializes the predefined belief sets. */
	private static void init() {
		beliefSets = new ClBeliefSet[3];
		Proposition b = new Proposition("b");
		Proposition f = new Proposition("f");
		Proposition k = new Proposition("k");
		Proposition p = new Proposition("p");
		Proposition w = new Proposition("w");

		beliefSets[0] = new ClBeliefSet();
		beliefSets[0].add(new Conditional(b, f));
		beliefSets[0].add(new Conditional(b, w));
		beliefSets[0].add(new Conditional(p, b));

		beliefSets[1] = new ClBeliefSet();
		beliefSets[1].addAll(beliefSets[0]);
		beliefSets[1].add(new Conditional(p, new Negation(f)));

		beliefSets[2] = new ClBeliefSet();
		beliefSets[2].addAll(beliefSets[1]);
		beliefSets[2].add(new Conditional(k, b));
		beliefSets[2].add(new Conditional(k, new Negation(w)));
	}

	/** Prints the current application status. */
	private static void printStatus() {
		System.out.println("Comparision with Bruteforce Reasoner: " +
				(compare ? "On" : "Off"));
	}

	/** Prints the descriptions for the predefined belief sets. */
	private static void printDescription() {
		System.out.println("Bespiel-Beschreibungen:");
		for(int i=0; i<descriptions.length; ++i) {
			System.out.println();
			System.out.println(entries[i]+":");
			System.out.println(descriptions[i]);
		}
	}

	/**
	 * Compares the available reasoners for the given belief set.
	 *
	 * @param beliefset the belief set to compare
	 */
	private static void test(ClBeliefSet beliefset) {
		System.out.println("Start Calculation RuleBased:");
		long begin = System.nanoTime();
		RuleBasedCReasoner rReasoner = new RuleBasedCReasoner();
		RankingFunction cReprRuleBased = rReasoner.getModel(beliefset);
		long end = System.nanoTime();
		long duration = (end-begin) / (1000*1000);
		System.out.println("Finished RuleBased in '" + String.valueOf(duration) + "' ms");
		System.out.println();

		System.out.println("Ranking Function:");
		System.out.println(cReprRuleBased);


		if(compare) {
			System.out.println();
			System.out.println();

			System.out.println("Start Calculation BruteForce:");
			begin = System.nanoTime();
			SimpleCReasoner reasoner = new SimpleCReasoner();
			RankingFunction cReprBruteForce = reasoner.getModel(beliefset);
			end = System.nanoTime();
			duration = (end-begin) / (1000*1000);
			System.out.println("Finished Bruteforce in '" + String.valueOf(duration) + "' ms");
			System.out.println();

			System.out.println("Ranking Function:");
			System.out.println(cReprBruteForce);

			boolean equal = cReprBruteForce.equals(cReprRuleBased);
			System.out.println();
			System.out.println("The results of the two reasoner are " + (equal ? "EQUAL" : "NOT EQUAL"));
		}
	}

}
