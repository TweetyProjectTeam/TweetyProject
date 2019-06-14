package net.sf.tweety.arg.adf.reasoner.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sf.tweety.arg.adf.parser.KPPADFFormatParser;
import net.sf.tweety.arg.adf.reasoner.AbstractDialecticalFrameworkReasoner;
import net.sf.tweety.arg.adf.reasoner.AdmissibleInterpretationReasoner;
import net.sf.tweety.arg.adf.reasoner.NaiveInterpretationReasoner;
import net.sf.tweety.arg.adf.reasoner.SatModelReasoner;
import net.sf.tweety.arg.adf.sat.IncrementalSatSolver;
import net.sf.tweety.arg.adf.sat.NativeLingelingSolver;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;
import net.sf.tweety.arg.adf.util.TestUtil;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.logics.pl.sat.SatSolver;

public class ReasonerBenchmark {
	
	static {
		SatSolver.setDefaultSolver(new NativeLingelingSolver());
	}

	public static final String[] ALL_SEMANTICS = { "mod", "cf", "nai", "adm", "com", "prf", "grd" };

	private KPPADFFormatParser parser = new KPPADFFormatParser();

	//TODO add some getDefaultIncrementalSolver method to obtain type safety
	private static IncrementalSatSolver satSolver = (IncrementalSatSolver) SatSolver.getDefaultSolver();

	private LazyModelStorage modelStorage = new LazyModelStorage();

	private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newFixedThreadPool(1);

	public void testAllInDirectory(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File dir,
			ExecutorService executor) throws IOException {
		File[] instances = dir.listFiles((File f, String name) -> name.endsWith(".adf"));
		for (File f : instances) {
			testSingle(reasoner, semantics, f, executor);
		}
	}
	
	public void testSingle(AbstractDialecticalFrameworkReasoner reasoner, String semantics, File f,
			ExecutorService executor) throws IOException {
		List<Map<String, Boolean>> models = modelStorage.getModels(f, semantics);
		AbstractDialecticalFramework adf = parser.parseBeliefBaseFromFile(f.getPath());
		CompletableFuture.supplyAsync(() -> runBenchmark(adf, reasoner, models), executor)
				.exceptionally(th -> handleException(th)).thenAccept(result -> printResults(f, result, System.out));
	}

	public BenchmarkResult handleException(Throwable th) {
		System.out.println(th);
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
			List<Map<String, Boolean>> assignments) {
		long startTimeInMillis = System.currentTimeMillis();
		Collection<Interpretation> models = reasoner.getModels(adf);
		long endTimeInMillis = System.currentTimeMillis();

		// now check if the found models are correct
		boolean correct = true;
		int correctModels = 0;
		for (Interpretation mod : models) {
			boolean foundEqual = false;
			for (Map<String, Boolean> solution : assignments) {
				if (TestUtil.equalInterpretations(mod, solution)) {
					foundEqual = true;
					correctModels++;
					break;
				}
			}
			if (!foundEqual) {
				// we got some model which is not in assignments. since we
				// assume that assignments is complete and correct, we must have
				// calculated at least one wrong model.
				correct = false;
				break;
			}
		}

		int modelSize = models.size();
		int modelDifference = modelSize - assignments.size();
		return new BenchmarkResult(modelSize, modelDifference, correctModels, correct, startTimeInMillis,
				endTimeInMillis);
	}
	
	public void testAdmissibleInterpretationSemantics() throws IOException {
		testAllInDirectory(new AdmissibleInterpretationReasoner(satSolver), "adm", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);
	}

	public void testNaiveInterpretationSemantics() throws IOException {
		testAllInDirectory(new NaiveInterpretationReasoner(satSolver), "nai", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);
	}

	public void testModelSemantics() throws IOException {
		testAllInDirectory(new SatModelReasoner(satSolver), "mod", new File("src/test/resources/instances"),
				DEFAULT_EXECUTOR_SERVICE);
	}

	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException {
		new ReasonerBenchmark().testAdmissibleInterpretationSemantics();
//		new ReasonerBenchmark().testNaiveInterpretationSemantics();
//		new ReasonerBenchmark().testModelSemantics();
//		 new ReasonerBenchmark().testSingle(new SatModelReasoner(satSolver), "mod", new File("src/test/resources/instances/adfgen_nacyc_se05_a_02_s_02_b_02_t_02_x_02_c_sXOR_ABA2AF_afinput_exp_acyclic_depvary_step1_batch_yyy03_10_21.apx.adf"),
//				DEFAULT_EXECUTOR_SERVICE);
		
	}

	private class LazyModelStorage {

		/**
		 * file -> semantics -> list of modelStorage
		 */
		private Map<File, Map<String, List<Map<String, Boolean>>>> semanticsByFile = new HashMap<File, Map<String, List<Map<String, Boolean>>>>();

		public List<Map<String, Boolean>> getModels(File instance, String semantics) throws IOException {
			File file = new File(instance.getPath() + ".solutions");
			Map<String, List<Map<String, Boolean>>> solutionsPerSemantics = semanticsByFile.get(file);
			if (solutionsPerSemantics == null) {
				solutionsPerSemantics = TestUtil.readSolutionFile(file);
				semanticsByFile.put(file, solutionsPerSemantics);
			}
			return solutionsPerSemantics.get(semantics);
		}

	}
}
