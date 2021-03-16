package org.tweetyproject.arg.adf.reasoner.sat.execution;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.decomposer.Decomposer;
import org.tweetyproject.arg.adf.reasoner.sat.decomposer.RandomDecomposer;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ConflictFreeGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.GroundGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ModelGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.ConflictFreeMaximizer;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.KBipolarStateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.AdmissibleMaximizer;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.AdmissibleVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.CompleteVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.GrounderStableVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.NaiveVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder;
import org.tweetyproject.arg.adf.transform.FixPartialTransformer;
import org.tweetyproject.arg.adf.transform.Transformer;

/**
 * 
 * The provided prefix is a subset of each interpretation computed by this semantics.
 * 
 * @author Mathias
 * 
 */
abstract class PrefixSemantics implements Semantics {

	final AbstractDialecticalFramework adf;
	
	final AbstractDialecticalFramework reduct;
	
	final PropositionalMapping mapping;
	
	final Interpretation prefix;

	public PrefixSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
		this.adf = Objects.requireNonNull(adf);
		this.reduct = reduct(adf, prefix);
		this.mapping = Objects.requireNonNull(mapping);
		this.prefix = Objects.requireNonNull(prefix);
	}
	
	@Override
	public Decomposer createDecomposer() {
		return new RandomDecomposer();
	}
	
	private static AbstractDialecticalFramework reduct(AbstractDialecticalFramework adf, Interpretation interpretation) {
		Transformer<AcceptanceCondition> fixPartials = new FixPartialTransformer(interpretation);		
		Builder builder = AbstractDialecticalFramework.builder().eager(new SatLinkStrategy(new NativeMinisatSolver())); // TODO fix
		for (Argument arg : adf.getArguments()) {
			builder.add(arg, fixPartials.transform(adf.getAcceptanceCondition(arg)));
		}
		return builder.build();
	}
	
	static final class ConflictFreeSemantics extends PrefixSemantics {

		public ConflictFreeSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping, prefix);
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withPrefix(reduct, mapping, prefix);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new ConflictFreeSemantics(adf, mapping, Interpretation.union(this.prefix, prefix));			
		}
	
	}
	
	static final class NaiveSemantics extends PrefixSemantics {
		
		public NaiveSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping, prefix);
		}
		
		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withPrefix(reduct, mapping, prefix);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of(ConflictFreeMaximizer.withPrefix(stateSupplier, reduct, mapping, prefix));
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new NaiveVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new NaiveSemantics(adf, mapping, Interpretation.union(this.prefix, prefix));			
		}
		
	}
	
	static final class AdmissibleSemantics extends PrefixSemantics {

		public AdmissibleSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping, prefix);
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withPrefix(reduct, mapping, prefix);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(reduct, mapping));
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new AdmissibleVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new AdmissibleSemantics(adf, mapping, Interpretation.union(this.prefix, prefix));			
		}
	
	}
	
	static final class PreferredSemantics extends PrefixSemantics {
		
		public PreferredSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping, prefix);
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withPrefix(reduct, mapping, prefix);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(reduct, mapping));
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of(AdmissibleMaximizer.withPrefix(stateSupplier, adf, reduct, mapping, prefix));
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new AdmissibleVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PreferredSemantics(adf, mapping, Interpretation.union(this.prefix, prefix));
		}
		
	}
	
	static final class CompleteSemantics extends PrefixSemantics {
		
		public CompleteSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping, prefix);
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withPrefix(reduct, mapping, prefix);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(reduct, mapping));
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new CompleteVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new CompleteSemantics(adf, mapping, Interpretation.union(this.prefix, prefix));
		}
		
	}
	
	static final class ModelSemantics extends PrefixSemantics {
		
		public ModelSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping, prefix);
		}
		
		@Override
		public Decomposer createDecomposer() {
			return new RandomDecomposer().asTwoValued();
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ModelGenerator.withPrefix(reduct, mapping, prefix);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new ModelSemantics(adf, mapping, Interpretation.union(this.prefix, prefix));
		}
		
	}
	
	static final class StableSemantics extends PrefixSemantics {
		
		public StableSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping, prefix);
		}
		
		@Override
		public Decomposer createDecomposer() {
			return new RandomDecomposer().asTwoValued();
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ModelGenerator.withPrefix(reduct, mapping, prefix);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new GrounderStableVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new StableSemantics(adf, mapping, Interpretation.union(this.prefix, prefix));
		}
		
	}
	
	static final class GroundSemantics extends PrefixSemantics {

		public GroundSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
			super(adf, mapping, prefix);
		}
		
		@Override
		public CandidateGenerator createCandidateGenerator() {
			return GroundGenerator.withPrefix(adf, mapping, prefix);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new GroundSemantics(adf, mapping, Interpretation.union(this.prefix, prefix));
		}

	}
	
}
