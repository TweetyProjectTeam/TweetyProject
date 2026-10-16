package org.tweetyproject.arg.adf.bench.old;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.tweetyproject.arg.adf.bench.common.Answers;
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
 * Runner for the frozen old implementation (adf:1.31-baseline or
 * 1.31-baseline-fixed) with native MiniSat, using the query API. Result
 * streams are consumed with an explicit iterator and closed (never
 * {@code count()}, whose spliterator is broken in the unfixed baseline). Link
 * types are computed lazily, i.e. as part of the timed solve.
 * <ul>
 * <li>SE takes the first element of {@code interpretations()}: the same
 * execution as {@code interpretation()}, which throws instead of answering
 * when there is no model.</li>
 * <li>DC is {@code where(a).exists()}, DS is {@code where(a).forAll()}.</li>
 * <li>VE has no counterpart in the old API and is answered by membership in
 * the enumeration.</li>
 * <li>3MOD is not supported.</li>
 * </ul>
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

	private Configuration configuration() {
		return Configuration.builder().setSatSolver(solver).build();
	}

	private static ConditionlessTaskStep step(AbstractDialecticalFramework adf, String semantics) {
		SemanticsStep query = adf.query();
		return switch (semantics) {
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
	}

	@Override
	public List<Interpretation> enumerate(AbstractDialecticalFramework adf, String semantics) {
		List<Interpretation> models = new ArrayList<>();
		try (Stream<Interpretation> stream = step(adf, semantics).interpretations().configure(configuration()).execute()) {
			Iterator<Interpretation> it = stream.iterator();
			while (it.hasNext()) {
				models.add(it.next());
			}
		}
		return models;
	}

	@Override
	public Interpretation witness(AbstractDialecticalFramework adf, String semantics) {
		try (Stream<Interpretation> stream = step(adf, semantics).interpretations().configure(configuration()).execute()) {
			Iterator<Interpretation> it = stream.iterator();
			return it.hasNext() ? it.next() : null;
		}
	}

	@Override
	public boolean accepted(AbstractDialecticalFramework adf, String semantics, String argument, boolean credulous) {
		Argument a = argument(adf, argument);
		ConditionlessTaskStep step = step(adf, semantics);
		return credulous ? step.where(a).exists().configure(configuration()).execute()
				: step.where(a).forAll().configure(configuration()).execute();
	}

	@Override
	public Object interpretation(AbstractDialecticalFramework adf, Set<String> satisfied, Set<String> unsatisfied) {
		for (String name : satisfied) {
			argument(adf, name);
		}
		for (String name : unsatisfied) {
			argument(adf, name);
		}
		return Answers.canonical(satisfied, unsatisfied);
	}

	@Override
	public boolean verify(AbstractDialecticalFramework adf, String semantics, Object interpretation) {
		try (Stream<Interpretation> stream = step(adf, semantics).interpretations().configure(configuration()).execute()) {
			Iterator<Interpretation> it = stream.iterator();
			while (it.hasNext()) {
				Interpretation model = it.next();
				if (Answers.canonical(satisfied(model), unsatisfied(model)).equals(interpretation)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String verificationMethod() {
		return "membership in the enumeration";
	}

	private static Argument argument(AbstractDialecticalFramework adf, String name) {
		for (Argument a : adf.getArguments()) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		throw new IllegalArgumentException("Unknown argument " + name);
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
