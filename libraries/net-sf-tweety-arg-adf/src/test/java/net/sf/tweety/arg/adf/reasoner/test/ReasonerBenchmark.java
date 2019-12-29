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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.tweety.arg.adf.parser.KppADFFormatParser;
import net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import net.sf.tweety.arg.adf.reasoner.AdmissibleReasoner;
import net.sf.tweety.arg.adf.reasoner.CompleteReasoner;
import net.sf.tweety.arg.adf.reasoner.GroundReasoner;
import net.sf.tweety.arg.adf.reasoner.ModelReasoner;
import net.sf.tweety.arg.adf.reasoner.NaiveReasoner;
import net.sf.tweety.arg.adf.reasoner.PreferredReasoner;
import net.sf.tweety.arg.adf.reasoner.StableReasoner;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.NativeLingelingSolver;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.util.TestUtil;
import net.sf.tweety.commons.ParserException;

public class ReasonerBenchmark {
	
	public static final String[] ALL_SEMANTICS = { "cf", "nai", "mod", "stm", "adm", "com", "prf", "grd" };

	private KppADFFormatParser parser = new KppADFFormatParser();

	//TODO add some getDefaultIncrementalSolver method to obtain type safety
	private static IncrementalSatSolver satSolver = new NativeLingelingSolver();

	private LazyModelStorage modelStorage = new LazyModelStorage();

	private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newFixedThreadPool(1);

	public void testAllInDirectoryAsync(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File dir,
			ExecutorService executor) throws IOException {
		File[] instances = dir.listFiles((File f, String name) -> name.endsWith(".adf"));
		for (File f : instances) {
			testSingleAsync(reasoner, semantics, f, executor);
		}
	}
	
	public void testAllInDirectory(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File dir,
			ExecutorService executor) throws IOException {
		File[] instances = dir.listFiles((File f, String name) -> name.endsWith(".adf"));
		long endTimes = 0;
		long startTimes = 0;
		for (File f : instances) {
			BenchmarkResult result = testSingle(reasoner, semantics, f, executor);
			endTimes += result.getEndTimeInMillis();
			startTimes += result.getStartTimeInMillis();
		}
		double secs = (endTimes - startTimes) / 1000.0;
		System.out.println(semantics + " total: " + secs + "s");
	}
	
	public void testSingleAsync(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File f,
			ExecutorService executor) throws IOException {
		Set<Map<String, Boolean>> models = modelStorage.getModels(f, semantics);
		AbstractDialecticalFramework adf = parser.parseBeliefBaseFromFile(f.getPath());
		CompletableFuture.supplyAsync(() -> runBenchmark(adf, reasoner, models), executor)
				.exceptionally(th -> handleException(th)).thenAccept(result -> printResults(f, result, System.out));
	}
	
	public BenchmarkResult testSingle(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File f,
			ExecutorService executor) throws IOException {
		Set<Map<String, Boolean>> models = modelStorage.getModels(f, semantics);
		AbstractDialecticalFramework adf = parser.parseBeliefBaseFromFile(f.getPath());
		BenchmarkResult result = runBenchmark(adf, reasoner, models);
		printResults(f, result, System.out);
		return result;
	}

	public BenchmarkResult handleException(Throwable th) {
		return new BenchmarkResult(th);
	}

	public void printResults(File file, BenchmarkResult result, PrintStream out) {
		boolean success = result.isCorrect() && result.getModelDifference() == 0;
		out.print("Instance " + file.getName() + ": ");
		if (success) {
			double secs = (result.getEndTimeInMillis() - result.getStartTimeInMillis()) / 1000.0;
			out.println("Success! (" + secs + "s)");
		} else if (result.getException() != null) {
			out.println("Failed with exception: " + result.getException());
		} else {
			out.print("Fail! (");
			out.print("Found models: " + result.getModelCount());
			out.print(" (Correct: " + result.getCorrectModels() + ")");
			out.print(", Expected models: " + (result.getModelCount() - result.getModelDifference()));
			out.println(")");
		}
	}

	public BenchmarkResult runBenchmark(AbstractDialecticalFramework adf, AbstractDialecticalFrameworkReasoner reasoner,
			Set<Map<String, Boolean>> assignments) {
		Collection<Map<String, Boolean>> models = new LinkedList<>();
		int size = assignments.size();
//		System.out.println("Overall: " + size);
		long startTimeInMillis = System.currentTimeMillis();
		Iterator<Interpretation> iterator = reasoner.modelIterator(adf);
		int count = 0;
		
		while(iterator.hasNext()) {
			models.add(toMap(iterator.next()));
//			System.out.println(++count);
		}
		long endTimeInMillis = System.currentTimeMillis();
		
		// now check if the found models are correct
		boolean correct = true;
		int correctModels = 0;
		for (Map<String, Boolean> mod : models) {
			if (assignments.contains(mod)) {
				correctModels++;
			} else {
				correct = false;
			}
		}

		int modelSize = models.size();
		int modelDifference = modelSize - assignments.size();
		return new BenchmarkResult(modelSize, modelDifference, correctModels, correct, startTimeInMillis,
				endTimeInMillis);
	}
	
	private Map<String, Boolean> toMap(Interpretation interpretation) {
		Map<String, Boolean> assignment = new HashMap<>();
		for (Argument a : interpretation.getSatisfied()) {
			assignment.put(a.getName(), true);
		}
		for (Argument a : interpretation.getUnsatisfied()) {
			assignment.put(a.getName(), false);
		}
		for (Argument a : interpretation.getUndecided()) {
			assignment.put(a.getName(), null);
		}
		return assignment;
	}
	
	public void testAdmissibleInterpretationSemantics() throws IOException {
		testAllInDirectory(new AdmissibleReasoner(satSolver), "adm", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);
	}

	public void testNaiveInterpretationSemantics() throws IOException {
		testAllInDirectory(new NaiveReasoner(satSolver), "nai", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);
	}
	
	public void testPreferredInterpretationSemantics() throws IOException {
		testAllInDirectory(new PreferredReasoner(satSolver), "prf", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);	
	}

	public void testCompleteInterpretationSemantics() throws IOException {
		testAllInDirectory(new CompleteReasoner(satSolver), "com", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);	
	}
	
	public void testGroundInterpretationSemantics() throws IOException {
		testAllInDirectory(new GroundReasoner(satSolver), "grd", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);	
	}
	
	public void testModelSemantics() throws IOException {
		testAllInDirectory(new ModelReasoner(satSolver), "mod", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);
	}
	
	public void testStableModelSemantics() throws IOException {
		testAllInDirectory(new StableReasoner(satSolver), "stm", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);
	}
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException {
//		new ReasonerBenchmark().testAdmissibleInterpretationSemantics();
//		new ReasonerBenchmark().testNaiveInterpretationSemantics();
//		new ReasonerBenchmark().testPreferredInterpretationSemantics();
//		new ReasonerBenchmark().testCompleteInterpretationSemantics();
//		new ReasonerBenchmark().testGroundInterpretationSemantics();
//		new ReasonerBenchmark().testModelSemantics();
//		new ReasonerBenchmark().testStableModelSemantics();
		 new ReasonerBenchmark().testSingle(new AdmissibleReasoner(satSolver), "adm", new File("src/test/resources/instances/medium/adfgen_nacyc_se05_a_02_s_02_b_02_t_02_x_02_c_sXOR_Traffic_benton-or-us.gml.80_25_56.apx.adf"),
				DEFAULT_EXECUTOR_SERVICE);
		
//		TestUtil.mergeSolutionFiles("C:\\Users\\Mathias\\Downloads\\adf instances\\instances", ALL_SEMANTICS, "solutions");
	}

	private static class LazyModelStorage {

		/**
		 * file -> semantics -> list of modelStorage
		 */
		private Map<File, Map<String, Set<Map<String, Boolean>>>> semanticsByFile = new HashMap<>();

		public Set<Map<String, Boolean>> getModels(File instance, String semantics) throws IOException {
			File file = new File(instance.getPath() + ".solutions");
			Map<String, Set<Map<String, Boolean>>> solutionsPerSemantics = semanticsByFile.get(file);
			if (solutionsPerSemantics == null) {
				solutionsPerSemantics = TestUtil.readSolutionFile(file);
				semanticsByFile.put(file, solutionsPerSemantics);
			}
			return solutionsPerSemantics.get(semantics);
		}

	}
}
