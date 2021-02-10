package org.tweetyproject.arg.adf.reasoner.sat.parallel;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.interpretation.InterpretationIterator;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder;
import org.tweetyproject.arg.adf.transform.FixPartialTransformer;
import org.tweetyproject.arg.adf.transform.Transformer;

abstract class AbstractDecomposer implements Decomposer {

	@Override
	public Collection<AbstractDialecticalFramework> decompose(AbstractDialecticalFramework adf, int count) {
		int argumentsToFix = Math.min( adf.size(), (int) ( Math.log(count) / Math.log(3) ) ); // 3 = number of truth values
		Set<Argument> arguments = partition(adf, argumentsToFix);
		Iterator<Interpretation> interpretations = new InterpretationIterator(arguments);

		List<AbstractDialecticalFramework> decompositions = new LinkedList<>();
		while (interpretations.hasNext()) {
			Interpretation interpretation = interpretations.next();
			AbstractDialecticalFramework reduced = reduce(adf, interpretation);
			decompositions.add(reduced);
//			System.out.println(interpretation);
//			KppADFFormatWriter.writeTo(reduced, System.out);
//			System.out.println();
		}
//		System.exit(0);

		return decompositions;
	}

	private AbstractDialecticalFramework reduce(AbstractDialecticalFramework adf, Interpretation interpretation) {
		Transformer<AcceptanceCondition> fixPartials = new FixPartialTransformer(interpretation);		
		Builder builder = AbstractDialecticalFramework.builder().eager(new SatLinkStrategy(new NativeMinisatSolver()));
		for (Argument arg : adf.getArguments()) {
			if (interpretation.undecided(arg)) {
				builder.add(arg, arg);
			} else if (interpretation.satisfied(arg)) {
				builder.add(arg, AcceptanceCondition.TAUTOLOGY);
			} else if (interpretation.unsatisfied(arg)) {
				builder.add(arg, AcceptanceCondition.CONTRADICTION);
			} else {
				builder.add(arg, fixPartials.transform(adf.getAcceptanceCondition(arg)));
			}
		}
		return builder.build();
	}

	abstract Set<Argument> partition(AbstractDialecticalFramework adf, int size);

}
