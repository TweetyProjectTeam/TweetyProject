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
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

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
import net.sf.tweety.arg.adf.sat.NativeMinisatSolver;
import net.sf.tweety.arg.adf.semantics.SatLinkStrategy;
import net.sf.tweety.arg.adf.semantics.interpretation.Interpretation;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.util.TestUtil;

public class ReasonerBenchmark {

	public static final String[] ALL_SEMANTICS = { "cf", "nai", "mod", "stm", "adm", "com", "prf", "grd" };

	private KppADFFormatParser parser = new KppADFFormatParser(new SatLinkStrategy(satSolver), false);

	private static final DecimalFormat FORMAT = new DecimalFormat("#");

	// TODO add some getDefaultIncrementalSolver method to obtain type safety
	// private static IncrementalSatSolver satSolver = new
	// NativeLingelingSolver();
//	 private static IncrementalSatSolver satSolver = new NativePicosatSolver();
	private static IncrementalSatSolver satSolver = new NativeMinisatSolver();

	private LazyModelStorage modelStorage = new LazyModelStorage();

	public void testAllInDirectoryAsync(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File dir,
			ExecutorService executor) throws IOException {
		File[] instances = dir.listFiles((File f, String name) -> name.endsWith(".adf"));
		for (File f : instances) {
			testSingleAsync(reasoner, semantics, f, executor);
		}
	}

	public void testAllInDirectory(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File dir, int times)
			throws IOException {
		File[] instances = dir.listFiles((File f, String name) -> name.endsWith(".adf"));
		long endTimes = 0;
		long startTimes = 0;
		for (File f : instances) {
			BenchmarkResult result = null;
			if (times > 1) {
				int warmup = times / 5;
				result = testMultiple(reasoner, semantics, f, times, warmup);
			} else {
				result = testSingle(reasoner, semantics, f);
			}
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

	public BenchmarkResult testSingle(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File f)
			throws IOException {
		Set<Map<String, Boolean>> models = modelStorage.getModels(f, semantics);
		AbstractDialecticalFramework adf = parser.parseBeliefBaseFromFile(f.getPath());
		BenchmarkResult result = runBenchmark(adf, reasoner, models);
		printResults(f, result, System.out);
		return result;
	}

	public BenchmarkResult testMultiple(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File f,
			int times, int warmup) throws IOException {
		Set<Map<String, Boolean>> models = modelStorage.getModels(f, semantics);
		AbstractDialecticalFramework adf = parser.parseBeliefBaseFromFile(f.getPath());

		List<BenchmarkResult> results = new ArrayList<BenchmarkResult>(times);
		LongSummaryStatistics stats = new LongSummaryStatistics();
		for (int i = 0; i < times + warmup; i++) {
			BenchmarkResult result = runBenchmark(adf, reasoner, models);
			if (i >= warmup) {
				results.add(result);
				long duration = (result.getEndTimeInMillis() - result.getStartTimeInMillis());
				stats.accept(duration);
			}
		}

		printResults(f, results, stats, System.out);

		BenchmarkResult first = results.get(0);
		if (first.getException() == null) {
			return new BenchmarkResult(first.getModelCount(), first.getModelDifference(), first.getCorrectModels(),
					first.isCorrect(), 0, stats.getSum());
		}
		return new BenchmarkResult(first.getException());
	}

	public BenchmarkResult handleException(Throwable th) {
		return new BenchmarkResult(th);
	}

	private void printResults(File file, List<BenchmarkResult> results, LongSummaryStatistics stats, PrintStream out) {
		long count = stats.getCount();
		double avg = stats.getAverage();
		double sum = 0.0;
		for (BenchmarkResult result : results) {
			long duration = (result.getEndTimeInMillis() - result.getStartTimeInMillis());
			sum += (duration - avg) * (duration - avg);
		}
		double var = sum / (count - 1);
		double stdDev = Math.sqrt(var);

		// we run the same instance multiple times, so if the first succeeds all
		// others do so as well
		BenchmarkResult first = results.get(0);
		boolean success = first.isCorrect() && first.getModelDifference() == 0;

		StringBuilder output = new StringBuilder();
		output.append("Instance ").append(file.getName()).append(": ");

		if (success) {
			output.append("Success! ");
			output.append(FORMAT.format(avg)).append(" (+-");
			output.append(FORMAT.format(stdDev)).append("), ");
			output.append(stats.getMin()).append(" min, ");
			output.append(stats.getMax()).append(" max (in ms)");
		} else if (first.getException() != null) {
			output.append("Exception! ");
		} else {
			output.append("Fail! (");
			output.append("Found models: " + first.getModelCount());
			output.append(" (Correct: " + first.getCorrectModels() + ")");
			output.append(", Expected models: " + (first.getModelCount() - first.getModelDifference()));
			output.append(")");
		}
		out.println(output);
	}

	private void printResults(File file, BenchmarkResult result, PrintStream out) {
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

		long startTimeInMillis = System.currentTimeMillis();
		Iterator<Interpretation> iterator = reasoner.modelIterator(adf);
		while (iterator.hasNext()) {
			models.add(toMap(iterator.next()));
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
		for (Argument a : interpretation.satisfied()) {
			assignment.put(a.getName(), true);
		}
		for (Argument a : interpretation.unsatisfied()) {
			assignment.put(a.getName(), false);
		}
		for (Argument a : interpretation.undecided()) {
			assignment.put(a.getName(), null);
		}
		return assignment;
	}

	public void testAdmissibleInterpretationSemantics(int times) throws IOException {
		testAllInDirectory(new AdmissibleReasoner(satSolver), "adm", new File("src/test/resources/instances"), times);
	}

	public void testNaiveInterpretationSemantics(int times) throws IOException {
		testAllInDirectory(new NaiveReasoner(satSolver), "nai", new File("src/test/resources/instances"), times);
	}

	public void testPreferredInterpretationSemantics(int times) throws IOException {
		testAllInDirectory(new PreferredReasoner(satSolver), "prf", new File("src/test/resources/instances"), times);
	}

	public void testCompleteInterpretationSemantics(int times) throws IOException {
		testAllInDirectory(new CompleteReasoner(satSolver), "com", new File("src/test/resources/instances"), times);
	}

	public void testGroundInterpretationSemantics(int times) throws IOException {
		testAllInDirectory(new GroundReasoner(satSolver), "grd", new File("src/test/resources/instances"), times);
	}

	public void testModelSemantics(int times) throws IOException {
		testAllInDirectory(new ModelReasoner(satSolver), "mod", new File("src/test/resources/instances"), times);
	}

	public void testStableModelSemantics(int times) throws IOException {
		testAllInDirectory(new StableReasoner(satSolver), "stm", new File("src/test/resources/instances"), times);
	}

	public static void main(String[] args) throws Exception {
		// Thread.sleep(10000);
		// System.out.println("Start test...");
		 new ReasonerBenchmark().testAdmissibleInterpretationSemantics(1);
		 new ReasonerBenchmark().testNaiveInterpretationSemantics(1);
		 new ReasonerBenchmark().testPreferredInterpretationSemantics(1);
		 new ReasonerBenchmark().testCompleteInterpretationSemantics(1);
		 new ReasonerBenchmark().testGroundInterpretationSemantics(1);
		 new ReasonerBenchmark().testModelSemantics(1);
		 new ReasonerBenchmark().testStableModelSemantics(1);
//		new ReasonerBenchmark().testMultiple(new AdmissibleReasoner(satSolver), "adm", new File(
//				"src/test/resources/instances/medium/adfgen_nacyc_se05_a_02_s_02_b_02_t_02_x_02_c_sXOR_Traffic_benton-or-us.gml.80_25_56.apx.adf"),
//				10, 5);

		// TestUtil.mergeSolutionFiles("C:\\Users\\Mathias\\Downloads\\adf
		// instances\\instances", ALL_SEMANTICS, "solutions");
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
