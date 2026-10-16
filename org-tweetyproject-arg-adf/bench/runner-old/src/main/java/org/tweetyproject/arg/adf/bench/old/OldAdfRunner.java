package org.tweetyproject.arg.adf.bench.old;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.bench.common.RunnerSupport;
import org.tweetyproject.arg.adf.io.KppADFFormatParser;
import org.tweetyproject.arg.adf.reasoner.query.ConditionlessTaskStep;
import org.tweetyproject.arg.adf.reasoner.query.SemanticsStep;
import org.tweetyproject.arg.adf.reasoner.sat.execution.Configuration;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Runner for the frozen old implementation (adf:1.31-baseline) with native
 * MiniSat, using the query API with an explicit iterator over a closed stream
 * (never {@code count()}, whose spliterator is broken in the baseline). Link
 * types are computed lazily, i.e. as part of the timed solve.
 */
public class OldAdfRunner implements RunnerSupport.Implementation<AbstractDialecticalFramework, Interpretation> {

	private final NativeMinisatSolver solver = new NativeMinisatSolver();

	public static void main(String[] args) {
		RunnerSupport.run(new OldAdfRunner(), args);
	}

	@Override
	public String name() {
		return "old";
	}

	@Override
	public AbstractDialecticalFramework parse(Path file) throws Exception {
		return new KppADFFormatParser(new SatLinkStrategy(solver), true).parse(file.toFile());
	}

	@Override
	public List<Interpretation> enumerate(AbstractDialecticalFramework adf, String semantics) {
		SemanticsStep query = adf.query();
		ConditionlessTaskStep step = switch (semantics) {
			case "MOD" -> query.model();
			case "CF" -> query.conflictFree();
			case "NA" -> query.naive();
			case "ADM" -> query.admissible();
			case "CO" -> query.complete();
			case "GR" -> query.ground();
			case "PR" -> query.preferred();
			case "ST" -> query.stable();
			default -> throw new UnsupportedOperationException("Semantics not supported by the old implementation: " + semantics);
		};
		List<Interpretation> models = new ArrayList<>();
		try (Stream<Interpretation> stream = step.interpretations()
				.configure(Configuration.builder().setSatSolver(solver).build()).execute()) {
			Iterator<Interpretation> it = stream.iterator();
			while (it.hasNext()) {
				models.add(it.next());
			}
		}
		return models;
	}

	@Override
	public Set<String> satisfied(Interpretation model) {
		return names(model.satisfied());
	}

	@Override
	public Set<String> unsatisfied(Interpretation model) {
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
