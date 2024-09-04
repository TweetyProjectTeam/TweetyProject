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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.pl.examples;

import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.util.Pair;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasureEvaluator;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasureReport;
import org.tweetyproject.logics.commons.analysis.InconsistencyMeasureResult;
import org.tweetyproject.logics.pl.analysis.ContensionInconsistencyMeasure;
import org.tweetyproject.logics.pl.analysis.ContensionSatInconsistencyMeasure;
import org.tweetyproject.logics.pl.parser.DimacsParser;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.sat.CmdLineSatSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.logics.pl.syntax.PlSignature;
import org.tweetyproject.logics.pl.util.ContensionSampler;

/**
 * Example code for using InconsistencyMeasureEvaluator for comparing
 * inconsistency measure implementations.
 * 
 * @author Anna Gessler
 *
 */
public class InconsistencyMeasureEvaluationExample {

	private static String solverPath = "/home/anna/snap/sat/kissat/build/kissat";
	/**
	 * main
	 * @param args arguments
	 * @throws ParserException ParserException
	 * @throws IOException IOException
	 */
	public static void main(String[] args) throws ParserException, IOException {
		SatSolver.setDefaultSolver(new CmdLineSatSolver(solverPath));
		InconsistencyMeasureEvaluator<PlFormula, PlBeliefSet> ev = new InconsistencyMeasureEvaluator<PlFormula, PlBeliefSet>();
		ev = new InconsistencyMeasureEvaluator<PlFormula, PlBeliefSet>();

		//Add inconsistency measures
		ContensionSatInconsistencyMeasure im1 = new ContensionSatInconsistencyMeasure();
		ContensionInconsistencyMeasure im2 = new ContensionInconsistencyMeasure();
		ev.addInconsistencyMeasure(im1);
		ev.addInconsistencyMeasure(im2);
		
		//Add random knowledge bases
		ev.addFromSampler(new ContensionSampler(new PlSignature(5), 2), 1);
		ev.addFromSampler(new ContensionSampler(new PlSignature(8), 7), 1);
		ev.addFromSampler(new ContensionSampler(new PlSignature(12), 11), 1);
		
		//Add knowledge base from file or folder
		ev.parseDatasetFromPath("src/main/resources/dimacs_ex4.cnf", new DimacsParser(), 1);
		
		//Add manually created knowledge base
		PlParser parser = new PlParser();
		PlBeliefSet kb = parser.parseBeliefBase("a && b \n"
				+ "!a && !b \n"
				+ "c");
		ev.addKnowledgeBase(kb);
		
		//Print all knowledge bases
		for (Pair<String, PlBeliefSet> ix : ev.getDatasetWithNames())
			System.out.println(ix.getFirst() + " " + ix.getSecond());
		
		//Print results
		InconsistencyMeasureReport<PlFormula, PlBeliefSet> report = ev.compareMeasures();
		System.out.println("\n" + report.prettyPrint(true, true));
		//Get results of specific instance
		int i = 2;
		InconsistencyMeasureResult result = report.getIthResult(im1.toString(), i);
		System.out.println(report.getIthInstanceName(i) + i + ", " + im1.toString() + ": " + result.getValue() + ", " + result.getElapsedTime()/1000.0 + " s" );
		result = report.getIthResult(im2.toString(), i);
		System.out.println(report.getIthInstanceName(i) + i + ", " + im2.toString() + ": " + result.getValue() + ", " + result.getElapsedTime()/1000.0 + " s" );
	}

    /** Default Constructor */
    public InconsistencyMeasureEvaluationExample(){}
}
