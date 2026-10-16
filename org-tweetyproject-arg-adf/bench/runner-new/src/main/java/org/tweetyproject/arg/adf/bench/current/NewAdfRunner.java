package org.tweetyproject.arg.adf.bench.current;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.adf.bench.common.RunnerSupport;
import org.tweetyproject.arg.adf.io.KppAdfParser;
import org.tweetyproject.arg.adf.reasoner.AbstractAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.ModelIterator;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleThreeValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.reasoner.simple.SimpleTwoValuedModelAdfReasoner;
import org.tweetyproject.arg.adf.semantics.ThreeValuedInterpretation;
import org.tweetyproject.arg.adf.syntax.AbstractDialecticalFramework;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;

/**
 * Runner for the reworked implementation. Until the SAT reasoners exist
 * (phase 4) it runs the brute-force Simple* reasoners. Each solve compiles a
 * fresh snapshot of the freshly parsed framework.
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

	@Override
	public List<ThreeValuedInterpretation> enumerate(AbstractDialecticalFramework adf, String semantics) {
		AbstractAdfReasoner reasoner = switch (semantics) {
			case "MOD" -> new SimpleTwoValuedModelAdfReasoner();
			case "3MOD" -> new SimpleThreeValuedModelAdfReasoner();
			default -> AbstractAdfReasoner.getSimpleReasonerForSemantics(Semantics.valueOf(semantics));
		};
		List<ThreeValuedInterpretation> models = new ArrayList<>();
		try (ModelIterator it = reasoner.modelIterator(adf)) {
			it.forEachRemaining(models::add);
		}
		return models;
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
