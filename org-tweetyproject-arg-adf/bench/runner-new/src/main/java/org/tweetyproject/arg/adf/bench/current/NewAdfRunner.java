package org.tweetyproject.arg.adf.bench.current;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.bench.common.RunnerSupport;
import org.tweetyproject.arg.adf.io.KppAdfParser;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatAdmissibleAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatCompleteAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatConflictFreeAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatGroundedAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatNaiveAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatPreferredAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatStableAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.sat.SatTwoValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleThreeValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleTwoValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;
import org.tweetyproject.sat.SatSolver;
import org.tweetyproject.sat.minisat.MinisatSatSolver;
import org.tweetyproject.sat.sat4j.Sat4jIncrementalSolver;

/**
 * Runner for the reworked implementation: the brute-force Simple* reasoners,
 * or with {@code --reasoner sat} the SAT reasoners (semantics without one are
 * unsupported), with Sat4j or, with {@code --backend minisat}, native MiniSat. It uses {@code getModels} (EE, CE), {@code getModel} (SE),
 * {@code query} (DC, DS) and {@code isModel} (VE). Each solve works on the
 * freshly parsed framework.
 */
public class NewAdfRunner implements RunnerSupport.Implementation<AbstractDialecticalFramework, ThreeValuedInterpretation> {

	private final boolean sat;

	private final boolean minisat;

	NewAdfRunner(boolean sat, boolean minisat) {
		this.sat = sat;
		this.minisat = minisat;
	}

	public static void main(String[] args) {
		boolean sat = false, minisat = false;
		for (int i = 0; i + 1 < args.length; i++) {
			sat |= args[i].equals("--reasoner") && args[i + 1].equals("sat");
			minisat |= args[i].equals("--backend") && args[i + 1].equals("minisat");
		}
		RunnerSupport.run(new NewAdfRunner(sat, minisat), args);
	}

	@Override
	public String name() {
		return sat ? (minisat ? "new-sat-native" : "new-sat") : "new-simple";
	}

	@Override
	public AbstractDialecticalFramework parse(Path file) throws Exception {
		return new KppAdfParser().parse(file);
	}

	private AbstractAdfReasoner reasoner(String semantics) {
		if (sat) {
			Supplier<SatSolver> solvers = minisat ? MinisatSatSolver::new : Sat4jIncrementalSolver::new;
			return switch (semantics) {
				case "MOD" -> new SatTwoValuedModelAdfReasoner(solvers);
				case "GR" -> new SatGroundedAdfReasoner(solvers);
				case "CF" -> new SatConflictFreeAdfReasoner(solvers);
				case "NA" -> new SatNaiveAdfReasoner(solvers);
				case "ADM" -> new SatAdmissibleAdfReasoner(solvers);
				case "PR" -> new SatPreferredAdfReasoner(solvers);
				case "ST" -> new SatStableAdfReasoner(solvers);
				case "CO" -> new SatCompleteAdfReasoner(solvers);
				default -> throw new UnsupportedOperationException("No SAT reasoner for " + semantics + " yet");
			};
		}
		return switch (semantics) {
			case "MOD" -> new SimpleTwoValuedModelAdfReasoner();
			case "3MOD" -> new SimpleThreeValuedModelAdfReasoner();
			default -> AbstractAdfReasoner.getSimpleReasonerForSemantics(Semantics.valueOf(semantics));
		};
	}

	@Override
	public List<ThreeValuedInterpretation> enumerate(AbstractDialecticalFramework adf, String semantics) {
		return new ArrayList<>(reasoner(semantics).getModels(adf));
	}

	@Override
	public ThreeValuedInterpretation witness(AbstractDialecticalFramework adf, String semantics) {
		return reasoner(semantics).getModel(adf);
	}

	@Override
	public boolean accepted(AbstractDialecticalFramework adf, String semantics, String argument, boolean credulous) {
		return reasoner(semantics).query(adf, argument(adf, argument), credulous ? InferenceMode.CREDULOUS : InferenceMode.SKEPTICAL);
	}

	@Override
	public Object interpretation(AbstractDialecticalFramework adf, Set<String> satisfied, Set<String> unsatisfied) {
		Set<Argument> t = new HashSet<>(), f = new HashSet<>();
		for (String name : satisfied) {
			t.add(argument(adf, name));
		}
		for (String name : unsatisfied) {
			f.add(argument(adf, name));
		}
		return ThreeValuedInterpretation.of(t, f, adf.getNodes());
	}

	@Override
	public boolean verify(AbstractDialecticalFramework adf, String semantics, Object interpretation) {
		return reasoner(semantics).isModel(adf, (ThreeValuedInterpretation) interpretation);
	}

	@Override
	public String verificationMethod() {
		return "isModel";
	}

	private static Argument argument(AbstractDialecticalFramework adf, String name) {
		Argument a = new Argument(name);
		if (!adf.contains(a)) {
			throw new IllegalArgumentException("Unknown argument " + name);
		}
		return a;
	}

	@Override
	public Set<String> satisfied(ThreeValuedInterpretation model) {
		return names(model.satisfied());
	}

	@Override
	public Set<String> unsatisfied(ThreeValuedInterpretation model) {
		return names(model.unsatisfied());
	}

	private static Set<String> names(Set<Argument> arguments) {
		Set<String> names = new HashSet<>();
		for (Argument a : arguments) {
			names.add(a.getName());
		}
		return names;
	}

}
