package org.tweetyproject.arg.adf.bench.current;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.adf.bench.common.RunnerSupport;
import org.tweetyproject.arg.adf.io.KppAdfParser;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleThreeValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleTwoValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.commons.InferenceMode;

/**
 * Runner for the reworked implementation. Until the SAT reasoners exist
 * (phase 4) it runs the brute-force Simple* reasoners: {@code getModels} (EE,
 * CE), {@code getModel} (SE), {@code query} (DC, DS) and {@code isModel} (VE).
 * Each solve works on the freshly parsed framework.
 */
public class NewAdfRunner implements RunnerSupport.Implementation<AbstractDialecticalFramework, ThreeValuedInterpretation> {

	public static void main(String[] args) {
		RunnerSupport.run(new NewAdfRunner(), args);
	}

	@Override
	public String name() {
		return "new-simple";
	}

	@Override
	public AbstractDialecticalFramework parse(Path file) throws Exception {
		return new KppAdfParser().parse(file);
	}

	private static AbstractAdfReasoner reasoner(String semantics) {
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
